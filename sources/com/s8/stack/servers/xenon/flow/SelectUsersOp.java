package com.s8.stack.servers.xenon.flow;

import java.util.List;

import com.s8.arch.fluor.S8ExceptionCatcher;
import com.s8.arch.fluor.S8Filter;
import com.s8.arch.fluor.S8ResultProcessor;
import com.s8.arch.fluor.S8User;
import com.s8.arch.silicon.async.AsyncTask;
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
	public final S8ResultProcessor<List<S8User>> onSelected;

	
	/**
	 * 
	 */
	public final S8ExceptionCatcher onFailed;


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
			S8ResultProcessor<List<S8User>> onSelected, 
			S8ExceptionCatcher onFailed) {
		super(server, flow);
		this.filter = filter;
		this.onSelected = onSelected;
		this.onFailed = onFailed;
	}





	@Override
	public AsyncTask createTask() { 

		return new AsyncTask() {

			@Override
			public void run() {
				server.userDb.select(0L, filter, 
						selection -> {
							onSelected.run(selection);
							flow.roll(true);
						},
						exception -> {
							onFailed.run(exception); 
							flow.roll(true);
						});
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
