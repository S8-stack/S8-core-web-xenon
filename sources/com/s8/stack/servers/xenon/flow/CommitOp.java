package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CommitOp extends XeAsyncFlowOperation {

	public final String repositoryAddress;


	public final String branchId;


	public final Object[] objects;


	public final S8ResultProcessor<Long> onCommitted;


	public final S8ExceptionCatcher onException;






	public CommitOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			Object[] objects, 
			S8ResultProcessor<Long> onCommitted, 
			S8ExceptionCatcher onException) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.objects = objects;
		this.onCommitted = onCommitted;
		this.onException = onException;
	}




	@Override
	public AsyncTask createTask() { 
		return new AsyncTask() {
			
			
			@Override
			public void run() {
				server.repoDb.commit(0L, repositoryAddress, branchId, objects, 
						version -> { 
							onCommitted.run(version); 
							flow.roll(true);
						}, 
						exception -> {
							onException.run(exception);
							flow.roll(true);
						});
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}
}
