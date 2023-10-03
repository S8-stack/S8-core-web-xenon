package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class ExposeSpaceOp extends XeAsyncFlowOperation {


	public final String spaceId;

	public final Object[] exposure;

	public final S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased;

	public final long options;
	
	

	public ExposeSpaceOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			String spaceId, 
			Object[] exposure,
			S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased, 
			long options) {
		super(server, flow);
		this.spaceId = spaceId;
		this.exposure = exposure;
		this.onRebased = onRebased;
		this.options = options;
	}




	@Override
	public AsyncSiTask createTask() { 
		
		return new AsyncSiTask() {
			
			@Override
			public void run() {
				server.spaceDb.exposeObjects(0, flow.user, spaceId, exposure, 
						output -> {
							onRebased.run(output);
							flow.roll(true);
						},
						options);
			}
			
			@Override
			public MthProfile profile() { return MthProfile.FX1; }
			
			@Override
			public String describe() { return "Committing"; }
		};
	}

}
