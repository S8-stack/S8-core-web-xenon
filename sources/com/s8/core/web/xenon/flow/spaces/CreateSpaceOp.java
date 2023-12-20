package com.s8.core.web.xenon.flow.spaces;

import java.io.IOException;

import com.s8.api.flow.space.requests.CreateSpaceS8Request;
import com.s8.api.flow.space.requests.CreateSpaceS8Request.Status;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.cobalt.store.SpaceMgDatabase;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

public class CreateSpaceOp extends XeAsyncFlowOperation {

	public final SpaceMgDatabase db;

	public final CreateSpaceS8Request request;


	public CreateSpaceOp(XeAsyncFlow flow, SpaceMgDatabase db, CreateSpaceS8Request request) {
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
					request.onProcessed(Status.INVALID_SPACE_ID, -1L);

					/* continue immediately */
					flow.roll(true);
				}
				else { /* valid! */
					db.createSpace(0L, 
							flow.session.user, 
							() -> flow.roll(true), /* callback: continue */
							request);
				}
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
