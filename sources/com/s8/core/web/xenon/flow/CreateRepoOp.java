package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.RepoCreationS8AsyncOutput;
import com.s8.api.objects.repo.RepoS8Object;
import com.s8.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class CreateRepoOp extends XeAsyncFlowOperation {

	
	/**
	 * mg database
	 */
	public final RepoMgDatabase db;
	
	/**
	 * 
	 */
	public final String repositoryName;
	
	
	/**
	 * 
	 */
	public final String repositoryAddress;
	
	
	/**
	 * 
	 */
	public final String repositoryInfo;
	
	public final String mainBranchName;
	
	public final RepoS8Object[] objects;
	
	public final String initialCommitComment;
	

	/**
	 * 
	 */
	public final S8OutputProcessor<RepoCreationS8AsyncOutput> onCommitted;

	/**
	 * 
	 */
	public final long options;



	public CreateRepoOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			String repositoryName, 
			String repositoryAddress, 
			String repositoryInfo,
			String mainBranchName,
			RepoS8Object[] objects,
			String initialCommitComment,
			S8OutputProcessor<RepoCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(flow);
		this.db = db;
		this.repositoryName = repositoryName;
		this.repositoryAddress = repositoryAddress;
		this.repositoryInfo = repositoryInfo;
			
	
		this.mainBranchName = mainBranchName;
		this.objects = objects;
		this.initialCommitComment = initialCommitComment;
		
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
					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Repo DB is unavailable in this context");
					onCommitted.run(output);

					/* continue immediately */
					flow.roll(true);

				}
				else {
					db.createRepository(0L, flow.session.user, 
							repositoryName,
							repositoryAddress, 
							repositoryInfo,
							mainBranchName,
							objects, initialCommitComment, 
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
