package com.s8.core.web.xenon.flow.repos;

import java.io.IOException;

import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.store.CuRepoDB;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

public class CommitBranchOp extends XeAsyncFlowOperation {

	public final CuRepoDB db;
	
	public final CommitBranchS8Request request;



	public CommitBranchOp(XeAsyncFlow flow, 
			CuRepoDB db,
			CommitBranchS8Request request) {
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
					request.onFailed(new IOException("Repo DB is unavailable in this context"));

					/* continue immediately */
					flow.roll(true);					

				}
				else {
					db.commitBranch(0L, flow.session.user, 
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
