package com.s8.stack.servers.xenon.flow;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.stack.servers.xenon.XenonWebServer;

public abstract class XeAsyncFlowOperation {
	

	public final XenonWebServer server;
	
	public final XeAsyncFlow flow;
	


	public abstract AsyncTask createTask();


	public XeAsyncFlowOperation(XenonWebServer server, XeAsyncFlow flow) {
		super();
		this.server = server;
		this.flow = flow;
	}
	
	
	
	
}
