package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CreateBranchOp extends XeAsyncFlowOperation {

	public final String repositoryAddress;


	public final String branchId;


	/**
	 * 
	 */
	public final S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public CreateBranchOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.onCommitted = onCommitted;
		this.options = options;
	}




	@Override
	public AsyncTask createTask() { 
		return new AsyncTask() {
			
			
			@Override
			public void run() {
				server.repoDb.createBranch(0L, repositoryAddress, branchId, 
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
