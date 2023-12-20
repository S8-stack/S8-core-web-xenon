package com.s8.core.web.xenon.flow.tables;

import java.io.IOException;

import com.s8.api.flow.table.requests.PutRowS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.tellurium.store.TeDatabaseHandler;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

public class PutRowOp extends XeAsyncFlowOperation {


	public final TeDatabaseHandler db;

	public final PutRowS8Request request;


	public PutRowOp(
			XeAsyncFlow flow, 
			TeDatabaseHandler db,
			PutRowS8Request request) {
		super(flow);
		this.db = db;
		this.request = request;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {
				if(db != null) {
					db.putRow(0L, null, () -> flow.roll(true), request);
				}
				else {
					/* issue error directly */
					request.onError(new IOException("User DB is undefined in this context"));
					
					/* continue */
					flow.roll(true);
				}
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
