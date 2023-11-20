package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.databases.space.store.SpaceMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class ExposeSpaceOp extends XeAsyncFlowOperation {

	public final SpaceMgDatabase db;

	public final String spaceId;

	public final Object[] exposure;

	public final S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased;

	public final long options;



	public ExposeSpaceOp(XeAsyncFlow flow, 
			SpaceMgDatabase db,
			String spaceId, 
			Object[] exposure,
			S8OutputProcessor<SpaceVersionS8AsyncOutput> onRebased, 
			long options) {
		super(flow);
		this.db = db;
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
				if(db == null) {

					/* create output */
					SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Space DB is unavailable in this context");
					onRebased.run(output);

					/* continue immediately */
					flow.roll(true);

				}
				else if(spaceId == null) {

					/* create output */
					SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Space index is invalid this context: " + spaceId);
					onRebased.run(output);

					/* continue immediately */
					flow.roll(true);

				}
				else {
					db.exposeObjects(0, flow.session.user, spaceId, exposure, 
							output -> {
								onRebased.run(output);

								/* continue */
								flow.roll(true);
							},
							options);	
				}


			}

			@Override
			public MthProfile profile() { return MthProfile.FX1; }

			@Override
			public String describe() { return "Committing"; }
		};
	}

}
