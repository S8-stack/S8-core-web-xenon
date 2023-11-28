package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.BranchCreationS8AsyncOutput;
import com.s8.core.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class ForkRepoOp extends XeAsyncFlowOperation {

	
	public final RepoMgDatabase db;
	
	/**
	 * 
	 */
	public final String originRepositoryAddress;
	
	
	public final String originBranchId;
	
	
	public final long originBranchVersion;


	/**
	 * 
	 */
	public final String targetRepositoryAddress;

	


	/**
	 * 
	 */
	public final S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public ForkRepoOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String originRepositoryAddress, 
			String originBranchId,
			long originBranchVersion,
			String targetRepositoryAddress,
			S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(flow);
		this.db = db;
		this.originRepositoryAddress = originRepositoryAddress;
		this.originBranchId = originBranchId;
		this.originBranchVersion = originBranchVersion;
		this.targetRepositoryAddress = targetRepositoryAddress;
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
					BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Repo DB is unavailable in this context");
					onCommitted.run(output);

					/* continue immediately */
					flow.roll(true);					

				}
				else {
					db.forkBranch(0L, flow.session.user, 
							originRepositoryAddress, originBranchId, originBranchVersion, 
							targetRepositoryAddress,
							output -> { 
								onCommitted.run(output); 
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
