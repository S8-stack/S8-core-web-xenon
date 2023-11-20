package com.s8.core.web.xenon.flow;

import java.io.IOException;

import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.magnesium.databases.space.store.SpaceMgDatabase;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

public class AccessSpaceOp extends XeAsyncFlowOperation {

	public final SpaceMgDatabase db;

	public final String spaceId;

	public final S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed;

	public final long options;


	public AccessSpaceOp(XeAsyncFlow flow, 
			SpaceMgDatabase db,
			String spaceId,
			S8OutputProcessor<SpaceExposureS8AsyncOutput> onAccessed, 
			long options) {
		super(flow);
		this.db = db;
		this.spaceId = spaceId;
		this.onAccessed = onAccessed;
		this.options = options;
	}



	@Override
	public AsyncSiTask createTask() { 


		return new AsyncSiTask() {

			@Override
			public void run() {
				
				/* check db */
				if(db == null) {

					/* create output */
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("Space DB is unavailable in this context");
					onAccessed.run(output);

					/* continue immediately */
					flow.roll(true);
				}
				/* check id */
				else if(spaceId == null) {

					/* create output */
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.hasException = true;
					output.exception = new IOException("space ID is invalid: "+spaceId);
					onAccessed.run(output);

					/* continue immediately */
					flow.roll(true);
				}
				else { /* valid! */
					db.accessSpace(0L, flow.session.user, spaceId, 
							exposure -> {
								onAccessed.run(exposure);

								/* continue */
								flow.roll(true);
							},
							options); 
				}
			}
			public @Override MthProfile profile() { return MthProfile.FX1; }
			public @Override String describe() { return "Committing"; }
		};
	}
}
