package com.s8.stack.servers.xenon.flow;

import com.s8.arch.silicon.SiliconEngine;

public interface XeAsyncTask {


	public abstract void launch(SiliconEngine ng, XeFlow flow);
	
}
