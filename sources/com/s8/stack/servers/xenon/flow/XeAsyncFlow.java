package com.s8.stack.servers.xenon.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.S8CodeBlock;
import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.XenonWebServer;

public class XeAsyncFlow implements S8AsyncFlow  {


	public final XenonWebServer server;

	public final SiliconEngine ng;
	
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
	private Deque<Operation> operations = new ArrayDeque<>();


	public XeAsyncFlow(XenonWebServer server,
			SiliconEngine ng, 
			NeBranch branch,
			HTTP2_Message response) {
		super();
		this.server = server;
		this.ng = ng;
		this.branch = branch;
		this.response = response;

	}









	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOperationLast(Operation operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.addLast(operation);

		}

		/* launch rolling */
		roll(false);

	}
	
	
	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOperationFirst(Operation operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.addLast(operation);

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
					Operation operation = operations.poll();					

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
	public S8AsyncFlow prior(int profile, S8CodeBlock runnable) {
		pushOperationFirst(new Operation() {
			public @Override AsyncTask createTask() { 
				return new AsyncTask() {
					public @Override void run() {
						runnable.run();
						roll(true);
					}
					public @Override MthProfile profile() { return MthProfile.FX1; }
					public @Override String describe() { return "Committing"; }
				};
			}
		});
		return this;
	}









	@Override
	public S8AsyncFlow then(int profile, S8CodeBlock runnable) {
		pushOperationLast(new Operation() {
			public @Override AsyncTask createTask() { 
				return new AsyncTask() {
					public @Override void run() {
						runnable.run();
						roll(true);
					}
					public @Override MthProfile profile() { return MthProfile.FX1; }
					public @Override String describe() { return "Committing"; }
				};
			}
		});
		return this;
	}









	@Override
	public void commit(String repositoryAddress, String branchId, Object[] objects, 
			S8ResultProcessor<Long> onCommitted,
			S8ExceptionCatcher onException) {
		pushOperationLast(new Operation() {
			public @Override AsyncTask createTask() { 
				return new AsyncTask() {
					public @Override void run() {
						server.repoDb.commit(0L, repositoryAddress, branchId, objects, 
								version -> { 
									onCommitted.run(version); 
									roll(true);
								}, 
								exception -> {
									onException.run(exception);
									roll(true);
								});
					}
					public @Override MthProfile profile() { return MthProfile.FX1; }
					public @Override String describe() { return "Committing"; }
				};
			}
		});
	}


	@Override
	public void cloneHead(String repositoryAddress, String branchId, S8ResultProcessor<Object[]> onCloned,
			S8ExceptionCatcher onException) {
		pushOperationLast(new Operation() {
			public @Override AsyncTask createTask() { 
				return new AsyncTask() {
					public @Override void run() {
						server.repoDb.cloneHead(0L, repositoryAddress, branchId, 
								objects -> { 
									onCloned.run((NdObject[]) objects); 
									roll(true);
								}, 
								exception -> {
									onException.run(exception);
									roll(true);
								});
					}
					public @Override MthProfile profile() { return MthProfile.FX1; }
					public @Override String describe() { return "Committing"; }
				};
			}
		});
		
	}




	@Override
	public void cloneVersion(String repositoryAddress, String branchId, long version,
			S8ResultProcessor<Object[]> onCloned, 
			S8ExceptionCatcher onException) {
		pushOperationLast(new Operation() {
			public @Override AsyncTask createTask() { 
				return new AsyncTask() {
					public @Override void run() {
						server.repoDb.cloneVersion(0L, repositoryAddress, branchId, version,  
								objects -> { 
									onCloned.run((NdObject[]) objects); 
									roll(true);
								}, 
								exception -> {
									onException.run(exception);
									roll(true);
								});
					}
					public @Override MthProfile profile() { return MthProfile.FX1; }
					public @Override String describe() { return "Committing"; }
				};
			}
		});
	}




	@Override
	public void send() {
		pushOperationLast(new Operation() {
			public @Override AsyncTask createTask() { 
				return new SendingTask(XeAsyncFlow.this, branch, response);
			}
		});
	}


	public void terminate() {
		isTerminated = true;
	}

}