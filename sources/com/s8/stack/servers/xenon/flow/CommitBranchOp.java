package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CommitBranchOp extends XeAsyncFlowOperation {

	public final String repositoryAddress;


	public final String branchId;


	public final Object[] objects;


	/**
	 * 
	 */
	public final S8OutputProcessor<BranchVersionS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public CommitBranchOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			Object[] objects, 
			S8OutputProcessor<BranchVersionS8AsyncOutput> onCommitted, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.objects = objects;
		this.onCommitted = onCommitted;
		this.options = options;
	}




	@Override
	public AsyncTask createTask() { 
		return new AsyncTask() {
			
			
			@Override
			public void run() {
				server.repoDb.commitBranch(0L, repositoryAddress, branchId, objects, 
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
