package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8OutputProcessor;
import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.PutUserS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.object.BeObject;
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
	public AsyncTask createTask() { 
		return new AsyncTask() {


			@Override
			public void run() {
				server.userDb.put(0L, (BeObject) user, 
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
