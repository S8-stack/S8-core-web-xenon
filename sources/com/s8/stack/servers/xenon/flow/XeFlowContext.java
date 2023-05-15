package com.s8.stack.servers.xenon.flow;

import java.util.HashMap;
import java.util.Map;

public class XeFlowContext {
	
	private final Object arg;

	private final Map<String, Object> objects = new HashMap<>();
	
	
	public XeFlowContext(Object arg) {
		super();
		this.arg = arg;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getArg(){
		return (T) arg;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) { 
		return (T) objects.get(key); 
	}
	
	
	/**
	 * 
	 * @param <T>
	 * @param key
	 * @param value
	 */
	public <T> void set(String key, T value) {
		objects.put(key, value);
	}
}
