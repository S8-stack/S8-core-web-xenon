package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.RepoCreationS8AsyncOutput;
import com.s8.api.objects.repo.RepoS8Object;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class CreateRepoOp extends XeAsyncFlowOperation {

	
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



	public CreateRepoOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryName, 
			String repositoryAddress, 
			String repositoryInfo,
			String mainBranchName,
			RepoS8Object[] objects,
			String initialCommitComment,
			S8OutputProcessor<RepoCreationS8AsyncOutput> onCommitted, 
			long options) {
		super(server, flow);
		
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
				server.repoDb.createRepository(0L, flow.user, 
						repositoryName,
						repositoryAddress, 
						repositoryInfo,
						mainBranchName,
						objects, initialCommitComment, 
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
