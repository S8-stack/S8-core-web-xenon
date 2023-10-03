package com.s8.stack.servers.xenon.flow;

import com.s8.api.flow.S8Filter;
import com.s8.api.flow.S8OutputProcessor;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.ObjectsListS8AsyncOutput;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

/**
 * 
 * @author pierreconvert
 *
 */
public class SelectUsersOp extends XeAsyncFlowOperation {


	/**
	 * 
	 */
	public final S8Filter<S8User> filter;

	
	/**
	 * 
	 */
	public final S8OutputProcessor<ObjectsListS8AsyncOutput<S8User>> onSelected;

	public final long options;

	/**
	 * 
	 * @param server
	 * @param flow
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public SelectUsersOp(XenonWebServer server, 
			XeAsyncFlow flow, 
			S8Filter<S8User> filter,
			S8OutputProcessor<ObjectsListS8AsyncOutput<S8User>> onSelected, 
			long options) {
		super(server, flow);
		this.filter = filter;
		this.onSelected = onSelected;
		this.options = options;
	}





	@Override
	public AsyncSiTask createTask() { 

		return new AsyncSiTask() {

			@Override
			public void run() {
				server.userDb.select(0L, filter, 
						output -> {
							onSelected.run(output);
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
