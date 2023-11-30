package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.record.requests.GetRecordS8Request;
import com.s8.core.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

class GetUserOp extends XeAsyncFlowOperation {


	public final RecordsMgDatabase db;
	
	public final GetRecordS8Request request;


	public GetUserOp(
			XeAsyncFlow flow, 
			RecordsMgDatabase db,
			GetRecordS8Request request) {
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
					db.get(0L, () -> flow.roll(true), request);	
				}
				else {
					/* issue erreor directly */
					request.onFailed(new IOException("User DB is undefined in this context"));
					
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
