package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.space.requests.ExposeSpaceS8Request;
import com.s8.core.arch.magnesium.databases.space.store.SpaceMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class ExposeSpaceOp extends XeAsyncFlowOperation {

	public final SpaceMgDatabase db;

	public final ExposeSpaceS8Request request;



	public ExposeSpaceOp(XeAsyncFlow flow,
			SpaceMgDatabase db,
			ExposeSpaceS8Request request) {
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
					request.onFailed(new IOException("Space DB is unavailable in this context"));
					
					/* continue immediately */
					flow.roll(true);

				}
				else if(request.spaceId == null) {

					/* create output */
					request.onFailed(new IOException("Space index is invalid this context: " + request.spaceId));

					/* continue immediately */
					flow.roll(true);

				}
				else {
					db.exposeObjects(0, flow.session.user, 
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
