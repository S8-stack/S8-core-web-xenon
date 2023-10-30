package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.RepositoryMetadataS8AsyncOutput;
import com.s8.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class GetRepoMetadataOp extends XeAsyncFlowOperation {

	
	public final RepoMgDatabase db;
	
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



	public GetRepoMetadataOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String repositoryAddress, 
			S8OutputProcessor<RepositoryMetadataS8AsyncOutput> onDone, 
			long options) {
		super(flow);
		this.db = db;
		this.repositoryAddress = repositoryAddress;
		this.onDone = onDone;
		this.options = options;
	}




	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {
			
			
			@Override
			public void run() {
				if(db == null) {
					
					/* create output */
					RepositoryMetadataS8AsyncOutput output = new RepositoryMetadataS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Repo DB is unavailable in this context");
					onDone.run(output);

					/* continue immediately */
					flow.roll(true);					
					
				}
				else {
					db.getRepositoryMetadata(0L, flow.user, repositoryAddress, output -> {
						onDone.run(output);
						
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
