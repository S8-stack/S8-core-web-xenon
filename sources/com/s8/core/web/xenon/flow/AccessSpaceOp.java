package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.space.requests.AccessSpaceS8Request;
import com.s8.api.flow.space.requests.AccessSpaceS8Request.Status;
import com.s8.core.arch.magnesium.databases.space.store.SpaceMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class AccessSpaceOp extends XeAsyncFlowOperation {

	public final SpaceMgDatabase db;

	public final AccessSpaceS8Request request;


	public AccessSpaceOp(XeAsyncFlow flow, SpaceMgDatabase db, AccessSpaceS8Request request) {
		super(flow);
		this.db = db;
		this.request = request;
	}



	@Override
	public AsyncSiTask createTask() { 


		return new AsyncSiTask() {

			@Override
			public void run() {

				/* check db */
				if(db == null) {

					/* create output */
					request.onFailed(new IOException("Space DB is unavailable in this context"));

					/* continue immediately */
					flow.roll(true);
				}
				/* check id */
				else if(request.spaceId == null) {

					/* create output */
					request.onAccessed(Status.INVALID_SPACE_ID, null);

					/* continue immediately */
					flow.roll(true);
				}
				else { /* valid! */
					db.accessSpace(0L, flow.session.user, 
							() -> flow.roll(true), /* callback: continue */
							request); 
				}
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
