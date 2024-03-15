package com.s8.core.web.xenon;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.web.S8WebFront;

@FunctionalInterface
public interface XeBoot {
	
	
	/**
	 * 
	 * @param front
	 * @param flow
	 * @throws Exception
	 */
	public void boot(S8WebFront front, S8AsyncFlow flow) throws Exception;
	
	
	
	
}
