package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class CloneBranchOp extends XeAsyncFlowOperation {


	public final RepoMgDatabase db;
	
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
	public CloneBranchOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String repositoryAddress, 
			String branchId,
			long version, 
			S8OutputProcessor<BranchExposureS8AsyncOutput> onCloned, 
			long options) {
		
		super(flow);
		this.db = db;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.version = version;
		this.onCloned = onCloned;
		this.options = options;
	}






	@Override
	public AsyncSiTask createTask() { 
		
		
		return new AsyncSiTask() {
			
			@Override
			public void run() {
				if(db == null) {

					/* create output */
					BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Repo DB is unavailable in this context");
					onCloned.run(output);

					/* continue immediately */
					flow.roll(true);					

				}
				else {
					db.cloneBranch(0L, flow.user, repositoryAddress, branchId, version,  
							output -> { 
								onCloned.run(output); 
								flow.roll(true);
							},
							options);	
				}
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
