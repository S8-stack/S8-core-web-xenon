package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class AccessSpaceOp extends XeAsyncFlowOperation {

	public final String spaceId;

	public final S8ResultProcessor<Object[]> onAccessed;

	public final S8ExceptionCatcher onException;



	public AccessSpaceOp(XenonWebServer server,
			XeAsyncFlow flow, 
			String spaceId,
			S8ResultProcessor<Object[]> onAccessed, 
			S8ExceptionCatcher onException) {
		super(server, flow);
		this.spaceId = spaceId;
		this.onAccessed = onAccessed;
		this.onException = onException;
	}


	
	@Override
	public AsyncTask createTask() { 
		

		return new AsyncTask() {
			
			@Override
			public void run() {
				server.spaceDb.accessExposure(0L, spaceId, 
						exposure -> {
							onAccessed.run(exposure);
							flow.roll(true);
						},
						exception -> {
							onException.run(exception);
							flow.roll(true);
						}); 
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
