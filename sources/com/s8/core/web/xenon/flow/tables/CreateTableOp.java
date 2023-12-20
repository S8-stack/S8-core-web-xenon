package com.s8.core.web.xenon.flow.tables;

import java.io.IOException;

import com.s8.api.flow.table.requests.CreateTableS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.tellurium.store.TeDatabaseHandler;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

/**
 * 
 */
public class CreateTableOp extends XeAsyncFlowOperation {

	
	
	public final TeDatabaseHandler db;

	/**
	 * 
	 */
	public final CreateTableS8Request request;

	/**
	 * 
	 * @param server
	 * @param flow
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public CreateTableOp(XeAsyncFlow flow, TeDatabaseHandler db, CreateTableS8Request request) {
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
					db.createTable(0L, null, () -> flow.roll(true), request);	
				}
				else {
					/* request */
					request.onFailed(new IOException("User DB missing in this context"));
					
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
