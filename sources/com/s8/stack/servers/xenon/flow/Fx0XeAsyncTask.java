package com.s8.stack.servers.xenon.flow;

import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

public class Fx0XeAsyncTask implements XeAsyncTask {

	
	public final AsyncBlock block;
	

	public Fx0XeAsyncTask(AsyncBlock block) {
		super();
		this.block = block;
	}

	@Override
	public void launch(SiliconEngine ng, XeFlow flow) {
		ng.pushAsyncTask(new AsyncTask() {
			
			@Override
			public void run() {
				// run
				try {
					block.run(flow.handler, flow.context);
				} 
				catch (Exception e) {
					e.printStackTrace();
					flow.onFailed(e);
				}
				
				// roll
				flow.roll(true);
			}
			
			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}
			
			@Override
			public String describe() {
				return "xe runnable";
			}
		});
	}

}
