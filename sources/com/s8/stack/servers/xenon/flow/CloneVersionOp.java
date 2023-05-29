package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.stack.servers.xenon.XenonWebServer;


/**
 * 
 * @author pierreconvert
 *
 */
public class CloneVersionOp extends XeAsyncFlowOperation {


	public final String repositoryAddress;


	public final String branchId;


	public final long version;


	public final S8ResultProcessor<Object[]> onCloned;

	public final S8ExceptionCatcher onException;


	
	
	

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
	public CloneVersionOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			long version, 
			S8ResultProcessor<Object[]> onCloned, 
			S8ExceptionCatcher onException) {
		
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.version = version;
		this.onCloned = onCloned;
		this.onException = onException;
	}






	@Override
	public AsyncTask createTask() { 
		
		
		return new AsyncTask() {
			
			@Override
			public void run() {
				server.repoDb.cloneVersion(0L, repositoryAddress, branchId, version,  
						objects -> { 
							onCloned.run((NdObject[]) objects); 
							flow.roll(true);
						}, 
						exception -> {
							onException.run(exception);
							flow.roll(true);
						});
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
