package com.s8.stack.servers.xenon.flow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.S8CodeBlock;
import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8Filter;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.fluor.S8User;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.XeUser;
import com.s8.stack.servers.xenon.XenonWebServer;

public class XeAsyncFlow implements S8AsyncFlow  {


	public final XenonWebServer server;

	public final SiliconEngine ng;

	public final XeUser user;

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
	private Deque<XeAsyncFlowOperation> operations = new ArrayDeque<>();
	
	private Deque<XeAsyncFlowOperation> sequenceBuilder = new ArrayDeque<>();


	public XeAsyncFlow(XenonWebServer server,
			SiliconEngine ng, 
			XeUser user,
			NeBranch branch,
			HTTP2_Message response) {
		super();
		this.server = server;
		this.ng = ng;
		this.user = user;
		this.branch = branch;
		this.response = response;

	}









	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOperationLast(XeAsyncFlowOperation operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			sequenceBuilder.addLast(operation);
		}
	}


	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOperationFirst(XeAsyncFlowOperation operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			sequenceBuilder.addLast(operation);
		}
	}

	


	@Override
	public void play() {
		
		synchronized (lock) {
			
			/* Empty sequence builder and append operations to the main deque */
			while(!sequenceBuilder.isEmpty()) {
				operations.addFirst(sequenceBuilder.pollLast());
			}
			
		}
		
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
	void roll(boolean isContinued) {

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
					XeAsyncFlowOperation operation = operations.poll();					

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
		pushOperationLast(new RunBlockOp(server, this, runnable));
		return this;
	}


	/* <process-zerp> */



	/* <process-beryllium> */



	@Override
	public S8User getMe() {
		return user;
	}
	
	
	//private static class Operation
	

	@Override
	public S8AsyncFlow getUser(String username, S8ResultProcessor<S8User> onRetrieved, S8ExceptionCatcher onFailed) {
		pushOperationLast(new GetUserOp(server, this, username, onRetrieved, onFailed));
		return this;
	}


	@Override
	public S8AsyncFlow selectUsers(S8Filter<S8User> filter, S8ResultProcessor<List<S8User>> onSelected, S8ExceptionCatcher onFailed) {
		pushOperationLast(new SelectUsersOp(server, this, filter, onSelected, onFailed));
		return this;
	}



	/* <process-lithum> */




	@Override
	public S8AsyncFlow accessMySpace(S8ResultProcessor<Object[]> onAccessed, S8ExceptionCatcher onException) {
		return accessSpace(getMySpaceId(), onAccessed, onException);
	}


	@Override
	public S8AsyncFlow exposeMySpace(Object[] exposure, S8ResultProcessor<Long> onRebased, S8ExceptionCatcher onException) {
		return exposeSpace(getMySpaceId(), exposure, onRebased, onException);
	}


	@Override
	public S8AsyncFlow accessSpace(String spaceId, S8ResultProcessor<Object[]> onAccessed, S8ExceptionCatcher onException) {
		pushOperationLast(new AccessSpaceOp(server, this, spaceId, onAccessed, onException));
		return this;
	}


	@Override
	public S8AsyncFlow exposeSpace(String spaceId, Object[] exposure, S8ResultProcessor<Long> onRebased,
			S8ExceptionCatcher onException) {
		pushOperationLast(new ExposeSpaceOp(server, this, spaceId, exposure, onRebased, onException));
		return this;
	}


	@Override
	public S8AsyncFlow commit(String repositoryAddress, String branchId, Object[] objects, 
			S8ResultProcessor<Long> onCommitted,
			S8ExceptionCatcher onException) {
		pushOperationLast(new CommitOp(server, this, repositoryAddress, branchId, objects, onCommitted, onException));
		return this;
	}


	@Override
	public S8AsyncFlow cloneHead(String repositoryAddress, String branchId, 
			S8ResultProcessor<Object[]> onCloned,
			S8ExceptionCatcher onException) {
		pushOperationLast(new CloneHeadOp(server, this, repositoryAddress, branchId, onCloned, onException));
		return this;
	}




	@Override
	public S8AsyncFlow cloneVersion(String repositoryAddress, String branchId, long version,
			S8ResultProcessor<Object[]> onCloned, 
			S8ExceptionCatcher onException) {
		pushOperationLast(new CloneVersionOp(server, this, repositoryAddress, branchId, version, onCloned, onException));
		return this;
	}




	@Override
	public void send() {
		pushOperationLast(new SendOp(server, this, branch, response));
		play();
	}


	public void terminate() {
		isTerminated = true;
	}






























}
