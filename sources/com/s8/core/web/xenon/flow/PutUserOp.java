package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.PutUserS8AsyncOutput;
import com.s8.api.objects.table.TableS8Object;
import com.s8.core.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

class PutUserOp extends XeAsyncFlowOperation {


	public final RecordsMgDatabase db;

	public final S8User user;

	public final S8OutputProcessor<PutUserS8AsyncOutput> onInserted;

	public final long options;


	public PutUserOp(
			XeAsyncFlow flow, 
			RecordsMgDatabase db,
			S8User user, 
			S8OutputProcessor<PutUserS8AsyncOutput> onInserted,
			long options) {
		super(flow);
		this.db = db;
		this.user = user;
		this.onInserted = onInserted;
		this.options = options;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {
				if(db != null) {
					db.put(0L, (TableS8Object) user, 
							output -> {
								onInserted.run(output);
								
								/* continue */
								flow.roll(true);
							}, 
							options);
				}
				else {
					PutUserS8AsyncOutput output = new PutUserS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("User DB is undefined in this context");
					onInserted.run(output);
					
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
