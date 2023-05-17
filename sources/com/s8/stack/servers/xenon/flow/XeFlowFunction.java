package com.s8.stack.servers.xenon.flow;

import com.s8.io.bohr.neon.functions.none.VoidNeFunction;
import com.s8.stack.servers.xenon.session.XeContext;

public abstract class XeFlowFunction  {
	
	
	
	
	public static class Void implements VoidNeFunction {

		@Override
		public void run(Object context) {
			XeContext xc = (XeContext) context;
			
		}
	}

}
