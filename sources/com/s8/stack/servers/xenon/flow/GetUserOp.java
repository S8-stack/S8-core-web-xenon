package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.fluor.S8User;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

class GetUserOp extends XeAsyncFlowOperation {
	
	
	public final String username;
	
	public final S8ResultProcessor<S8User> onRetrieved;
	
	public final S8ExceptionCatcher onFailed;
	
	

	public GetUserOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String username, 
			S8ResultProcessor<S8User> onRetrieved,
			S8ExceptionCatcher onFailed) {
		super(server, flow);
		this.username = username;
		this.onRetrieved = onRetrieved;
		this.onFailed = onFailed;
	}



	public @Override AsyncTask createTask() { 
		return new AsyncTask() {
			public @Override void run() {
				server.userDb.get(0L, username, 
						user -> {
							onRetrieved.run((S8User) user);
							flow.roll(true);
						},
						exception -> {
							onFailed.run(exception); 
							flow.roll(true);
						});
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
