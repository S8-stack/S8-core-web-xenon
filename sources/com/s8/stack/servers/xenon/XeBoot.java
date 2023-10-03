package com.s8.stack.servers.xenon;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.io.bohr.neon.core.NeBranch;

@FunctionalInterface
public interface XeBoot {
	
	
	/**
	 * 
	 * @param branch
	 * @param flow
	 * @throws Exception
	 */
	public void boot(NeBranch branch, S8AsyncFlow flow) throws Exception;
	
	
	
	
}
