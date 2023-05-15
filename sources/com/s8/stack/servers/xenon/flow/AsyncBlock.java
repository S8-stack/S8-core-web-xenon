package com.s8.stack.servers.xenon.flow;

public interface AsyncBlock {

	
	public void run(XeFlowHandler handler, XeFlowContext context) throws Exception;
	
	
}
