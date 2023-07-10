package com.s8.stack.servers.xenon.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.S8CodeBlock;
import com.s8.arch.fluor.S8Filter;
import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.fluor.outputs.GetUserS8AsyncOutput;
import com.s8.arch.fluor.outputs.ObjectsListS8AsyncOutput;
import com.s8.arch.fluor.outputs.PutUserS8AsyncOutput;
import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.object.NdObject;
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
	public S8AsyncFlow getUser(String username, S8OutputProcessor<GetUserS8AsyncOutput> onRetrieved, long options) {
		pushOperationLast(new GetUserOp(server, this, username, onRetrieved, options));
		return this;
	}
	
	@Override
	public S8AsyncFlow putUser(S8User user, S8OutputProcessor<PutUserS8AsyncOutput> onInserted, long options) {
		pushOperationLast(new PutUserOp(server, this, user, onInserted, options));
		return this;
	}


	@Override
	public S8AsyncFlow selectUsers(S8Filter<S8User> filter, 
			S8OutputProcessor<ObjectsListS8AsyncOutput<S8User>> onSelected, 
			long options) {
		pushOperationLast(new SelectUsersOp(server, this, filter, onSelected, options));
		return this;
	}



	/* <process-lithum> */


	@Override
	public S8AsyncFlow accessSpace(String spaceId, S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed, long options) {
		pushOperationLast(new AccessSpaceOp(server, this, spaceId, onAccessed, options));
		return this;
	}



	@Override
	public S8AsyncFlow accessMySpace(S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed, long options) {
		pushOperationLast(new AccessSpaceOp(server, this, getMySpaceId(), onAccessed, options));
		return this;
	}


	@Override
	public S8AsyncFlow exposeSpace(String spaceId, Object[] exposure, S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased,
			long options) {
		pushOperationLast(new ExposeSpaceOp(server, this, spaceId, exposure, onRebased, options));
		return this;
	}
	

	@Override
	public S8AsyncFlow exposeMySpace(Object[] exposure, S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased, long options) {
		pushOperationLast(new ExposeSpaceOp(server, this, getMySpaceId(), exposure, onRebased, options));
		return this;
	}



	@Override
	public S8AsyncFlow createRepository(
			String repositoryAddress,
			String repositoryInfo, 
			String mainBranchName,
			Object[] objects,
			String initialCommitComment,
			S8OutputProcessor<RepoCreationS8AsyncOutput> onCreated, long options) {
		pushOperationLast(new CreateRepoOp(server, this, 
				repositoryAddress, repositoryInfo, 
				mainBranchName,
				(NdObject[]) objects, initialCommitComment,
				onCreated, options));
		return this;
	}


	@Override
	public S8AsyncFlow forkBranch(String repositoryAddress, 
			String originBranchId, long originBranchVersion, String targetBranchId,
			S8OutputProcessor<BranchCreationS8AsyncOutput> onCreated, long options) {
		pushOperationLast(new ForkBranchOp(server, this, repositoryAddress, 
				originBranchId, originBranchVersion, targetBranchId,
				onCreated, options));
		return this;
	}






	@Override
	public S8AsyncFlow forkRepository(String originRepositoryAddress, 
			String originBranchId, long originBranchVersion,
			String targetRepositoryAddress,
			S8OutputProcessor<BranchCreationS8AsyncOutput> onForked, long options) {
		pushOperationLast(new ForkRepoOp(server, this, 
				originRepositoryAddress, 
				originBranchId, originBranchVersion, 
				targetRepositoryAddress,
				onForked, options));
		return this;
	}





	@Override
	public S8AsyncFlow commitBranch(String repositoryAddress, String branchId, 
			Object[] objects, String author, String comment,
			S8OutputProcessor<BranchVersionS8AsyncOutput> onCommitted, long options) {
		pushOperationLast(new CommitBranchOp(server, this, repositoryAddress, branchId, 
				objects, author, comment,
				onCommitted, options));
		return this;
	}



	@Override
	public S8AsyncFlow cloneBranch(String repositoryAddress, String branchId, long version,
			S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned, 
			long options) {
		pushOperationLast(new CloneBranchOp(server, this, repositoryAddress, branchId, version, onCloned, options));
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
