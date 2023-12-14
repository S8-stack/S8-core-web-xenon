package com.s8.core.web.xenon.flow;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8CodeBlock;
import com.s8.api.flow.S8User;
import com.s8.api.flow.delivery.S8WebResourceGenerator;
import com.s8.api.flow.mail.SendMailS8Request;
import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.api.flow.record.requests.GetRecordS8Request;
import com.s8.api.flow.record.requests.PutRecordS8Request;
import com.s8.api.flow.record.requests.SelectRecordsS8Request;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.api.flow.space.requests.AccessSpaceS8Request;
import com.s8.api.flow.space.requests.CreateSpaceS8Request;
import com.s8.api.flow.space.requests.ExposeSpaceS8Request;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.xenon.XeUser;
import com.s8.core.web.xenon.XeWebServer;
import com.s8.core.web.xenon.flow.delivery.XeDeliveryTask;
import com.s8.core.web.xenon.flow.mail.SendMailOp;
import com.s8.core.web.xenon.sessions.XeWebSession;
import com.s8.io.bohr.neon.core.NeBranch;


/**
 * 
 */
public class XeAsyncFlow implements S8AsyncFlow  {

	/**
	 * the light thread execution engine
	 */
	public final SiliconEngine ng;


	/** 
	 * the server 
	 */
	public final XeWebServer server;


	/** 
	 * The session 
	 */
	public final XeWebSession session;


	/**
	 * The branch
	 */
	public final NeBranch branch;


	public final HTTP2_Message response;



	/**
	 * Internal lock
	 */
	private final Object lock = new Object();



	private volatile boolean isTerminated = false;

	private volatile boolean isActive = false;

	/**
	 * 
	 */
	private XeFlowChain<XeAsyncFlowOperation> operations = new XeFlowChain<>();


	public XeAsyncFlow(
			SiliconEngine ng, 
			XeWebServer server,
			XeWebSession session,
			NeBranch branch,
			HTTP2_Message response) {
		super();
		this.ng = ng;
		this.server = server;
		this.session = session;
		this.branch = branch;
		this.response = response;
	}









	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOperation(XeAsyncFlowOperation operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.insertAfterActive(operation);
		}
	}




	@Override
	public void play() {

		/* launch rolling */
		roll(false);
	}



	/**
	 * 
	 * @return
	 */
	public boolean isRolling() {
		synchronized (lock) { return isActive; }
	}


	/**
	 * Should nto be called when transitioning
	 */
	public void roll(boolean isContinued) {

		/* 
		 * Start rolling if not already rolling. Two cases:
		 * <ul>
		 * <li>Called by pushOperation() -> initial start : !isContinued: proceed only if !isRolling</li>
		 * <li>Called by MgBranchOperation() -> continuation : (isContinued == true): proceed only if isRolling == true</li>
		 * </ul>
		 * => Almost equal to re-entrant lock
		 */

		synchronized (lock) {

			if(!isTerminated && isActive == isContinued) {

				if(!operations.isEmpty()) {
					isActive = true;

					if(isContinued) {
						/**
						 * The operation that calls back this flow is still the head of operations
						 * (We let them the head for proper insertion when running the task)
						 * Now, last time to remove the head and access next operation
						 */
						operations.popHead();
					}

					/*
					 * Retrieve but not removed the head
					 */
					XeAsyncFlowOperation operation = operations.getHead();					


					ng.pushAsyncTask(operation.createTask());
					/*
					 * Immediately exit Syncchronized block after pushing the task
					 * --> Leave time to avoid contention
					 */
					
				}
				else { // no more operation
					isActive = false;
				}
			}
		}
	}







	@Override
	public S8AsyncFlow runBlock(int profile, S8CodeBlock runnable) {
		pushOperation(new RunBlockOp(this, runnable));
		return this;
	}


	/* <process-zerp> */



	/* <process-beryllium> */



	@Override
	public S8User getMe() {
		return session.user;
	}

	@Override
	public void setMe(S8User user) {
		session.user = (XeUser) user;
	}


	//private static class Operation
	


	@Override
	public S8AsyncFlow sendEMail(SendMailS8Request request) {
		pushOperation(new SendMailOp(this, server.manganeseWebService, request));	
		return this;
	}


	@Override
	public S8AsyncFlow then(GetRecordS8Request request) {
		pushOperation(new GetUserOp(this, server.userDb, request));	
		return this;
	}

	@Override
	public S8AsyncFlow then(PutRecordS8Request request) {
		pushOperation(new PutUserOp(this, server.userDb, request));
		return this;
	}


	@Override
	public <T extends RecordS8Object> S8AsyncFlow then(SelectRecordsS8Request<T> request) {
		pushOperation(new SelectUsersOp<T>(this, server.userDb,request));
		return this;
	}



	/* <process-lithum> */


	@Override
	public S8AsyncFlow then(AccessSpaceS8Request request) {
		pushOperation(new AccessSpaceOp(this, server.spaceDb, request));
		return this;
	}


	@Override
	public S8AsyncFlow then(ExposeSpaceS8Request request) {
		pushOperation(new ExposeSpaceOp(this, server.spaceDb, request));
		return this;
	}





	@Override
	public S8AsyncFlow then(CreateRepositoryS8Request request) {
		pushOperation(new CreateRepoOp(this, server.repoDb, request));
		return this;
	}






	@Override
	public S8AsyncFlow then(GetRepositoryMetadataS8Request request) {
		pushOperation(new GetRepoMetadataOp(this, server.repoDb, request));
		return this;
	}



	@Override
	public S8AsyncFlow then(ForkBranchS8Request request) {
		pushOperation(new ForkBranchOp(this, server.repoDb, request));
		return this;
	}



	@Override
	public S8AsyncFlow then(ForkRepositoryS8Request request) {
		pushOperation(new ForkRepoOp(this, server.repoDb, request));
		return this;
	}





	@Override
	public S8AsyncFlow then(CommitBranchS8Request request) {
		pushOperation(new CommitBranchOp(this, server.repoDb, request));
		return this;
	}



	@Override
	public S8AsyncFlow then(CloneBranchS8Request request) {
		pushOperation(new CloneBranchOp(this, server.repoDb, request));
		return this;
	}




	@Override
	public void send() {
		pushOperation(new SendOp(this, branch, response));
		play();
	}


	public void terminate() {
		isTerminated = true;
	}






	@Override
	public void deliver(int load, S8WebResourceGenerator generator) {
		ng.pushAsyncTask(new XeDeliveryTask(ng, response, generator));
	}



	@Override
	public S8AsyncFlow then(CreateSpaceS8Request request) {
		throw new RuntimeException("Not implemented yet");
	}





	@Override
	public S8AsyncFlow then(GetBranchMetadataS8Request request) {
		throw new RuntimeException("Not implemented yet");
	}


}
