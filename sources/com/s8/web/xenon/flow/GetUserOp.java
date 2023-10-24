package com.s8.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.GetUserS8AsyncOutput;
import com.s8.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;

class GetUserOp extends XeAsyncFlowOperation {


	public final RecordsMgDatabase db;
	
	public final String username;

	public final S8OutputProcessor<GetUserS8AsyncOutput> onRetrieved;

	public final long options;


	public GetUserOp(
			XeAsyncFlow flow, 
			RecordsMgDatabase db,
			String username, 
			S8OutputProcessor<GetUserS8AsyncOutput> onRetrieved,
			long options) {
		super(flow);
		this.db = db;
		this.username = username;
		this.onRetrieved = onRetrieved;
		this.options = options;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {
				if(db != null) {
					db.get(0L, username, 
							output -> {
								onRetrieved.run(output);
								
								/* continue */
								flow.roll(true);
							}, 
							options);	
				}
				else {
					GetUserS8AsyncOutput output = new GetUserS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("User DB is undefined in this context");
					onRetrieved.run(output);
					
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
