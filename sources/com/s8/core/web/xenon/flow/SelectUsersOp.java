package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8Filter;
import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.ObjectsListS8AsyncOutput;
import com.s8.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
public class SelectUsersOp extends XeAsyncFlowOperation {

	
	
	public final RecordsMgDatabase db;

	/**
	 * 
	 */
	public final S8Filter<S8User> filter;

	
	/**
	 * 
	 */
	public final S8OutputProcessor<ObjectsListS8AsyncOutput<S8User>> onSelected;

	public final long options;

	/**
	 * 
	 * @param server
	 * @param flow
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public SelectUsersOp(XeAsyncFlow flow, 
			RecordsMgDatabase db,
			S8Filter<S8User> filter,
			S8OutputProcessor<ObjectsListS8AsyncOutput<S8User>> onSelected, 
			long options) {
		super(flow);
		this.db = db;
		this.filter = filter;
		this.onSelected = onSelected;
		this.options = options;
	}





	@Override
	public AsyncSiTask createTask() { 

		return new AsyncSiTask() {

			@Override
			public void run() {
				if(db != null) {
					db.select(0L, filter, 
							output -> {
								onSelected.run(output);
								
								/* continue */
								flow.roll(true);
							},
							options);	
				}
				else {
					ObjectsListS8AsyncOutput<S8User> output = new ObjectsListS8AsyncOutput<>();
					output.exception = new IOException("User DB missing in this context");
					output.hasException = true;
					onSelected.run(output);
					
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
