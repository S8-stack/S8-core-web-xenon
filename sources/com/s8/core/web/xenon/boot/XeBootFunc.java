package com.s8.core.web.xenon.boot;

import com.s8.api.S8BootFunc;
import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.web.S8WebFront;


/**
 * 
 */
public interface XeBootFunc extends S8BootFunc {
	

	/**
	 * Declare boot name
	 * @return
	 */
	public String getName();
	
	
	/**
	 * 
	 * @param front
	 * @param flow
	 * @throws Exception
	 */
	@Override
	public void boot(S8WebFront front, S8AsyncFlow flow) throws Exception;
	
	
}
