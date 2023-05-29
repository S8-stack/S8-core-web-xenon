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
public class CloneHeadOp extends XeAsyncFlowOperation {


	/**
	 * repository address
	 */
	public final String repositoryAddress;

	
	/**
	 * branch id
	 */
	public final String branchId;

	
	/**
	 * 
	 */
	public final S8ResultProcessor<Object[]> onCloned;

	
	/**
	 * 
	 */
	public final S8ExceptionCatcher onException;
	


	/**
	 * 
	 * @param server
	 * @param flow
	 * @param repositoryAddress
	 * @param branchId
	 * @param onCloned
	 * @param onException
	 */
	public CloneHeadOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String repositoryAddress, 
			String branchId,
			S8ResultProcessor<Object[]> onCloned, 
			S8ExceptionCatcher onException) {
		super(server, flow);
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.onCloned = onCloned;
		this.onException = onException;
	}





	@Override
	public AsyncTask createTask() { 
		
		return new AsyncTask() {
			
			@Override
			public void run() {
				server.repoDb.cloneHead(0L, repositoryAddress, branchId, 
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
			public MthProfile profile() { 
				return MthProfile.FX1; 
			}
			
			@Override
			public String describe() { 
				return "Committing"; 
			}
		};
	}

}

