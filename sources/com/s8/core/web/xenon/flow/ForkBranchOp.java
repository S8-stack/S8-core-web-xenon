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
public class ForkBranchOp extends XeAsyncFlowOperation {


	public final RepoMgDatabase db;

	/**
	 * 
	 */
	public final String repositoryAddress;


	/**
	 * 
	 */
	public final String originBranchId;


	/**
	 * 
	 */
	public final long originBranchVersion;


	/**
	 * 
	 */
	public final String targetBranchId;



	/**
	 * 
	 */
	public final S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public ForkBranchOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String repositoryAddress, 
			String originBranchId,
			long originBranchVersion,
			String targetBranchId,
			S8OutputProcessor<BranchCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(flow);
		this.db = db;
		this.repositoryAddress = repositoryAddress;
		this.originBranchId = originBranchId;
		this.originBranchVersion = originBranchVersion;
		this.targetBranchId = targetBranchId;
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
					db.forkBranch(0L, flow.session.user, repositoryAddress, 
							originBranchId, originBranchVersion, 
							targetBranchId, 
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
