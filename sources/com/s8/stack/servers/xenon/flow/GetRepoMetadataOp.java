package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.RepositoryMetadataS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;


/**
 * 
 * @author pierreconvert
 *
 */
public class GetRepoMetadataOp extends XeAsyncFlowOperation {

	
	/**
	 * 
	 */
	public final String repositoryAddress;
	
	


	/**
	 * 
	 */
	public final S8OutputProcessor<RepositoryMetadataS8AsyncOutput> onDone;

	/**
	 * 
	 */
	public final long options;



	public GetRepoMetadataOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			S8OutputProcessor<RepositoryMetadataS8AsyncOutput> onDone, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.onDone = onDone;
		this.options = options;
	}




	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {
			
			
			@Override
			public void run() {
				server.repoDb.getRepositoryMetadata(0L, flow.user, repositoryAddress, output -> {
					onDone.run(output);
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
