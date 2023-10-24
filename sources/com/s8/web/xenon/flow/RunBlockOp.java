package com.s8.web.xenon.flow;

import com.s8.api.flow.S8CodeBlock;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;

public class RunBlockOp extends XeAsyncFlowOperation {




	public final S8CodeBlock runnable;

	public RunBlockOp(XeAsyncFlow flow, S8CodeBlock runnable) {
		super(flow);
		this.runnable = runnable;
	}


	@Override
	public AsyncSiTask createTask() { 

		return new AsyncSiTask() {

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
