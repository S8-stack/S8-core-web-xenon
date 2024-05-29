package com.s8.core.web.xenon.flow;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8CodeBlock;
import com.s8.api.flow.S8User;
import com.s8.api.flow.delivery.S8WebResourceGenerator;
import com.s8.api.flow.mail.SendMailS8Request;
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
import com.s8.api.flow.table.objects.RowS8Object;
import com.s8.api.flow.table.requests.CreateTableS8Request;
import com.s8.api.flow.table.requests.GetRowS8Request;
import com.s8.api.flow.table.requests.PutRowS8Request;
import com.s8.api.flow.table.requests.SelectRowsS8Request;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.xenon.XeUser;
import com.s8.core.web.xenon.XeWebServer;
import com.s8.core.web.xenon.flow.delivery.XeDeliveryTask;
import com.s8.core.web.xenon.flow.mail.SendMailOp;
import com.s8.core.web.xenon.flow.repos.CloneBranchOp;
import com.s8.core.web.xenon.flow.repos.CommitBranchOp;
import com.s8.core.web.xenon.flow.repos.CreateRepoOp;
import com.s8.core.web.xenon.flow.repos.ForkBranchOp;
import com.s8.core.web.xenon.flow.repos.ForkRepoOp;
import com.s8.core.web.xenon.flow.repos.GetRepoMetadataOp;
import com.s8.core.web.xenon.flow.spaces.AccessSpaceOp;
import com.s8.core.web.xenon.flow.spaces.CreateSpaceOp;
import com.s8.core.web.xenon.flow.spaces.ExposeSpaceOp;
import com.s8.core.web.xenon.flow.tables.CreateTableOp;
import com.s8.core.web.xenon.flow.tables.GetRowOp;
import com.s8.core.web.xenon.flow.tables.PutRowOp;
import com.s8.core.web.xenon.flow.tables.SelectRowsOp;
import com.s8.core.web.xenon.sessions.XeWebConnection;
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
	public final XeWebConnection session;


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

	private volatile boolean isLaunched = false;

	/**
	 * 
	 */
	private XeFlowChain<XeAsyncFlowOperation> operations = new XeFlowChain<>();


	public XeAsyncFlow(
			SiliconEngine ng, 
			XeWebServer server,
			XeWebConnection session,
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
		synchronized (lock) { return isLaunched; }
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

			if(!isTerminated && isLaunched == isContinued) {

				if(!operations.isEmpty()) {
					isLaunched = true;

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
					isLaunched = false;
					
				}
			}
		}
	}







	@Override
	public void runBlock(int profile, S8CodeBlock runnable) {
		pushOperation(new RunBlockOp(this, runnable));
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
	public void sendEMail(SendMailS8Request request) {
		if(!request.isOutOfFlow) {
			pushOperation(new SendMailOp(this, server.manganeseWebService, request));	
		}
		else {
			ng.pushAsyncTask(SendMailOp.createOutOfSyncTask(server.manganeseWebService, request));
		}
		
	}





	@Override
	public void createTable(CreateTableS8Request request) {
		pushOperation(new CreateTableOp(this, server.tablesDb, request));
	}
	
	@Override
	public void getRow(GetRowS8Request request) {
		pushOperation(new GetRowOp(this, server.tablesDb, request));
	}

	@Override
	public void putRow(PutRowS8Request request) {
		pushOperation(new PutRowOp(this, server.tablesDb, request));
	}

	@Override
	public <T extends RowS8Object> void selectRows(SelectRowsS8Request<T> request) {
		pushOperation(new SelectRowsOp<T>(this, server.tablesDb,request));
	}



	/* <spaces> */

	@Override
	public void accessSpace(AccessSpaceS8Request request) {
		pushOperation(new AccessSpaceOp(this, server.spacesDb, request));
	}


	@Override
	public void exposeSpace(ExposeSpaceS8Request request) {
		pushOperation(new ExposeSpaceOp(this, server.spacesDb, request));
	}


	@Override
	public void createSpace(CreateSpaceS8Request request) {
		pushOperation(new CreateSpaceOp(this, server.spacesDb, request));
	}


	/* </spaces> */

	
	
	/* <repositories> */

	@Override
	public void createRepository(CreateRepositoryS8Request request) {
		pushOperation(new CreateRepoOp(this, server.reposDb, request));
	}






	@Override
	public void getRepository(GetRepositoryMetadataS8Request request) {
		pushOperation(new GetRepoMetadataOp(this, server.reposDb, request));
	}



	@Override
	public void forkBranch(ForkBranchS8Request request) {
		pushOperation(new ForkBranchOp(this, server.reposDb, request));
	}



	@Override
	public void forkRepository(ForkRepositoryS8Request request) {
		pushOperation(new ForkRepoOp(this, server.reposDb, request));
	}





	@Override
	public void commitBranch(CommitBranchS8Request request) {
		pushOperation(new CommitBranchOp(this, server.reposDb, request));
	}



	@Override
	public void cloneBranch(CloneBranchS8Request request) {
		pushOperation(new CloneBranchOp(this, server.reposDb, request));
	}
	
	/* </repositories> */





	@Override
	public void send() {
		pushOperation(new SendOp(this, branch, response));
		play();
	}


	
	public void terminate() {
		isTerminated = true;
		

		/*
		 * When flow terminates, no more operation are expected to be added, 
		 * so we free the connection
		 */
		session.isBusy.set(false);
	}
	





	@Override
	public void deliver(int load, S8WebResourceGenerator generator) {
		ng.pushAsyncTask(new XeDeliveryTask(ng, response, generator));
	}





	@Override
	public void getBranch(GetBranchMetadataS8Request request) {
		throw new RuntimeException("Not implemented yet");
	}









}
