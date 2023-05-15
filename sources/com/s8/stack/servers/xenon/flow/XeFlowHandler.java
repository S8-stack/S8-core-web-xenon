package com.s8.stack.servers.xenon.flow;

import java.util.ArrayDeque;
import java.util.Deque;

public class XeFlowHandler {
	

	final Deque<XeAsyncTask> deque = new ArrayDeque<>();
	
	
	public XeFlowHandler then(AsyncBlock block) {
		deque.addLast(new Fx0XeAsyncTask(block));
		return this;
	}
	
	
	public XeFlowHandler immediately(AsyncBlock block) {
		deque.addFirst(new Fx0XeAsyncTask(block));
		return this;
	}
	
	
	public XeFlowHandler eventually(AsyncBlock block) {
		deque.addLast(new Fx0XeAsyncTask(block));
		return this;
	}
	
	
	public XeFlowHandler clear() {
		deque.clear();
		return this;
	}
}
