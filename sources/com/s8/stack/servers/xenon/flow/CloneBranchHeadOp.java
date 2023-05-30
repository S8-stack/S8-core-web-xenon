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
public class CloneBranchHeadOp extends XeAsyncFlowOperation {


	/**
	 * repository address
	 */
	public final String repositoryAddress;

	
	/**
	 * branch id
	 */
	public final String branchId;

	
	/**
	 * 
	 */
	public final S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned;

	
	public final long options;
	


	/**
	 * 
	 * @param server
	 * @param flow
	 * @param repositoryAddress
	 * @param branchId
	 * @param onCloned
	 * @param onException
	 */
	public CloneBranchHeadOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.onCloned = onCloned;
		this.options = options;
	}





	@Override
	public AsyncTask createTask() { 
		
		return new AsyncTask() {
			
			@Override
			public void run() {
				server.repoDb.cloneBranchHead(0L, repositoryAddress, branchId, 
						output -> { 
							onCloned.run(output); 
							flow.roll(true);
						}, 
						options);
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX1; 
			}
			
			@Override
			public String describe() { 
				return "Committing"; 
			}
		};
	}

}

