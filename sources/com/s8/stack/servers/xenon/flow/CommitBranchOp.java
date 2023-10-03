package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CommitBranchOp extends XeAsyncFlowOperation {

	public final String repositoryAddress;


	public final String branchId;


	public final Object[] objects;

	public final String author;
	
	public final String comment;
	

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
			String author,
			String comment,
			S8OutputProcessor<BranchVersionS8AsyncOutput> onCommitted, 
			long options) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.objects = objects;
		this.author = author;
		this.comment = comment;
		this.onCommitted = onCommitted;
		this.options = options;
	}




	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {
			
			
			@Override
			public void run() {
				server.repoDb.commitBranch(0L, flow.user, repositoryAddress, branchId, objects, comment,
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
