package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.table.objects.RowS8Object;
import com.s8.api.flow.table.requests.SelectRecordsS8Request;
import com.s8.core.arch.magnesium.databases.table.TableMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
public class SelectUsersOp<T extends RowS8Object> extends XeAsyncFlowOperation {

	
	
	public final TableMgDatabase db;

	/**
	 * 
	 */
	public final SelectRecordsS8Request<T> request;

	/**
	 * 
	 * @param server
	 * @param flow
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public SelectUsersOp(XeAsyncFlow flow, 
			TableMgDatabase db,
			SelectRecordsS8Request<T> request) {
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
					db.select(0L, () -> flow.roll(true), request);	
				}
				else {
					/* request */
					request.onError(new IOException("User DB missing in this context"));
					
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
