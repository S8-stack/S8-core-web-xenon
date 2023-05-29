package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class ExposeSpaceOp extends XeAsyncFlowOperation {


	public final String spaceId;

	public final Object[] exposure;

	public final S8ResultProcessor<Long> onRebased;

	public final S8ExceptionCatcher onException;
	
	
	
	

	public ExposeSpaceOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String spaceId, 
			Object[] exposure,
			S8ResultProcessor<Long> onRebased, 
			S8ExceptionCatcher onException) {
		super(server, flow);
		this.spaceId = spaceId;
		this.exposure = exposure;
		this.onRebased = onRebased;
		this.onException = onException;
	}




	@Override
	public AsyncTask createTask() { 
		
		return new AsyncTask() {
			
			@Override
			public void run() {
				server.spaceDb.exposeObjects(0, spaceId, exposure, 
						version -> {
							onRebased.run(version);
							flow.roll(true);
						},
						exception -> {
							onException.run(exception);
							flow.roll(true);
						});
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
