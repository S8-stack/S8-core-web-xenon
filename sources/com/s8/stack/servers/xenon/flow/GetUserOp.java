package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.GetUserS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

class GetUserOp extends XeAsyncFlowOperation {


	public final String username;

	public final S8OutputProcessor<GetUserS8AsyncOutput> onRetrieved;

	public final long options;


	public GetUserOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String username, 
			S8OutputProcessor<GetUserS8AsyncOutput> onRetrieved,
			long options) {
		super(server, flow);
		this.username = username;
		this.onRetrieved = onRetrieved;
		this.options = options;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {
				server.userDb.get(0L, username, 
						output -> {
							onRetrieved.run(output);
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
