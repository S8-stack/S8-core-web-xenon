package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CreateRepoOp extends XeAsyncFlowOperation {

	public final String repositoryAddress;


	/**
	 * 
	 */
	public final S8OutputProcessor<RepoCreationS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public CreateRepoOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			S8OutputProcessor<RepoCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.onCommitted = onCommitted;
		this.options = options;
	}




	@Override
	public AsyncTask createTask() { 
		return new AsyncTask() {
			
			
			@Override
			public void run() {
				server.repoDb.createRepository(0L, repositoryAddress, 
						output -> { 
							onCommitted.run(output); 
							flow.roll(true);
						}, options);
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}
}
