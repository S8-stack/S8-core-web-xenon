package com.s8.core.web.xenon.flow;

import com.s8.core.arch.silicon.async.AsyncSiTask;

public abstract class XeAsyncFlowOperation {
	

	/* public final XeWebServer server; */
	
	public final XeAsyncFlow flow;
	


	public abstract AsyncSiTask createTask();


	public XeAsyncFlowOperation(XeAsyncFlow flow) {
		super();
		this.flow = flow;
	}
	
	
	
	
}
