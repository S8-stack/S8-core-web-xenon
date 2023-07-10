package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;


/**
 * 
 * @author pierreconvert
 *
 */
public class CloneBranchOp extends XeAsyncFlowOperation {


	public final String repositoryAddress;


	public final String branchId;


	public final long version;


	public final S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned;

	
	public final long options;
	
	
	

	/**
	 * 
	 * @param server
	 * @param flow
	 * @param repositoryAddress
	 * @param branchId
	 * @param version
	 * @param onCloned
	 * @param onException
	 */
	public CloneBranchOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			long version, 
			S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned, 
			long options) {
		
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.version = version;
		this.onCloned = onCloned;
		this.options = options;
	}






	@Override
	public AsyncTask createTask() { 
		
		
		return new AsyncTask() {
			
			@Override
			public void run() {
				server.repoDb.cloneBranch(0L, repositoryAddress, branchId, version,  
						output -> { 
							onCloned.run(output); 
							flow.roll(true);
						},
						options);
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
