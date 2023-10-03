package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class AccessSpaceOp extends XeAsyncFlowOperation {

	public final String spaceId;

	public final S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed;

	public final long options;


	public AccessSpaceOp(XenonWebServer server,
			XeAsyncFlow flow, 
			String spaceId,
			S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed, 
			long options) {
		super(server, flow);
		this.spaceId = spaceId;
		this.onAccessed = onAccessed;
		this.options = options;
	}


	
	@Override
	public AsyncSiTask createTask() { 
		

		return new AsyncSiTask() {
			
			@Override
			public void run() {
				server.spaceDb.accessSpace(0L, flow.user, spaceId, 
						exposure -> {
							onAccessed.run(exposure);
							flow.roll(true);
						},
						options); 
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
