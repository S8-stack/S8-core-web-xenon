package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.BranchVersionS8AsyncOutput;
import com.s8.core.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class CommitBranchOp extends XeAsyncFlowOperation {

	public final RepoMgDatabase db;
	
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



	public CommitBranchOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String repositoryAddress, 
			String branchId,
			Object[] objects, 
			String author,
			String comment,
			S8OutputProcessor<BranchVersionS8AsyncOutput> onCommitted, 
			long options) {
		super(flow);
		this.db = db;
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
				if(db == null) {

					/* create output */
					BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Repo DB is unavailable in this context");
					onCommitted.run(output);

					/* continue immediately */
					flow.roll(true);					

				}
				else {
					db.commitBranch(0L, flow.session.user, repositoryAddress, branchId, objects, comment,
							output -> { 
								onCommitted.run(output); 
								
								/* continue */
								flow.roll(true);
							}, options);	
				}
				
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}
}
