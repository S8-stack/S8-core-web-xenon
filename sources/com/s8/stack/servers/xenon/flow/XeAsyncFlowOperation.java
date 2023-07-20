package com.s8.stack.servers.xenon.flow;

import com.s8.arch.silicon.async.SiAsyncTask;
import com.s8.stack.servers.xenon.XenonWebServer;

public abstract class XeAsyncFlowOperation {
	

	public final XenonWebServer server;
	
	public final XeAsyncFlow flow;
	


	public abstract SiAsyncTask createTask();


	public XeAsyncFlowOperation(XenonWebServer server, XeAsyncFlow flow) {
		super();
		this.server = server;
		this.flow = flow;
	}
	
	
	
	
}
