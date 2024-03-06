package com.s8.core.web.xenon.flow.repos;

import java.io.IOException;

import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.store.CuRepoDB;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;


/**
 * 
 * @author pierreconvert
 *
 */
public class CloneBranchOp extends XeAsyncFlowOperation {


	public final CuRepoDB db;
	
	public final CloneBranchS8Request request;
	

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
			CuRepoDB db,
			CloneBranchS8Request request) {
		
		super(flow);
		this.db = db;
		this.request = request;
	}






	@Override
	public AsyncSiTask createTask() { 
		
		
		return new AsyncSiTask() {
			
			@Override
			public void run() {
				if(db == null) {

					/* create output */
					request.onError(new IOException("Repo DB is unavailable in this context"));

					/* continue immediately */
					flow.roll(true);					

				}
				else {
					db.cloneBranch(0L, flow.session.user, 
							() -> flow.roll(true), /* callback: continue after request has been performed */
							request);	
				}
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
