package com.s8.core.web.xenon.flow.repos;

import java.io.IOException;

import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.store.RepoMgDatabase;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;


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
	public ForkBranchS8Request request;



	public ForkBranchOp(XeAsyncFlow flow, 
			RepoMgDatabase db,
			ForkBranchS8Request request) {
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
					db.forkBranch(0L, flow.session.user, 
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