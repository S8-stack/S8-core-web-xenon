package com.s8.stack.servers.xenon.flow;

import com.s8.arch.fluor.S8CodeBlock;
import com.s8.arch.silicon.async.SiAsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.stack.servers.xenon.XenonWebServer;

public class RunBlockOp extends XeAsyncFlowOperation {




	public final S8CodeBlock runnable;

	public RunBlockOp(XenonWebServer server, XeAsyncFlow flow, S8CodeBlock runnable) {
		super(server, flow);
		this.runnable = runnable;
	}


	@Override
	public SiAsyncTask createTask() { 

		return new SiAsyncTask() {

			@Override
			public void run() {
				runnable.run();
				flow.roll(true);
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
