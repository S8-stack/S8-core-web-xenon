package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.table.requests.PutRecordS8Request;
import com.s8.core.arch.magnesium.databases.table.TableMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

class PutUserOp extends XeAsyncFlowOperation {


	public final TableMgDatabase db;

	public final PutRecordS8Request request;


	public PutUserOp(
			XeAsyncFlow flow, 
			TableMgDatabase db,
			PutRecordS8Request request) {
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
					db.put(0L, () -> flow.roll(true), request);
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
