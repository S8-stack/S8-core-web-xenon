package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.PutUserS8AsyncOutput;
import com.s8.api.objects.table.TableS8Object;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

class PutUserOp extends XeAsyncFlowOperation {


	public final S8User user;

	public final S8OutputProcessor<PutUserS8AsyncOutput> onInserted;

	public final long options;


	public PutUserOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			S8User user, 
			S8OutputProcessor<PutUserS8AsyncOutput> onInserted,
			long options) {
		super(server, flow);
		this.user = user;
		this.onInserted = onInserted;
		this.options = options;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {
				server.userDb.put(0L, (TableS8Object) user, 
						output -> {
							onInserted.run(output);
							flow.roll(true);
						}, 
						options);
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
