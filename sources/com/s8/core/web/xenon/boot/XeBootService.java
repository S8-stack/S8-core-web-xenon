package com.s8.core.web.xenon.boot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.xenon.XeBootException;
import com.s8.core.web.xenon.protocol.XeRequestSyntax;

public class XeBootService {
	
	
	public final SiliconEngine ng;
	
	public final String title;

	
	/**
	 * 
	 */
	final Map<String, XeBootHandler> handlers;
	
	
	public XeBootService(SiliconEngine ng, String title, XeBoot[] boots) throws XeBootException {
		super();
		this.ng = ng;
		this.title = title;
		
		
		handlers = new ConcurrentHashMap<String, XeBootHandler>();
		if(boots != null) {
			for(XeBoot boot : boots) {
				registerBoot(boot);
			}
			
			if(!handlers.containsKey(XeRequestSyntax.MAIN_BOOT_PAGE)) {
				throw new XeBootException("MAIN BOOT PAGE is mandatory");
			}
		}
		else {
			throw new XeBootException("Mandatory ot have at least one boot");	
		}
	}
	
	private void registerBoot(XeBoot boot) throws XeBootException {
		String name = boot.getName();
		if(name.contains("'")) {
			throw new XeBootException("Char : ' is now allowed inside a boot name");
		}
		if(name.contains("\"")) {
			throw new XeBootException("Char : \" is now allowed inside a boot name");
		}
		if(name.contains("`")) {
			throw new XeBootException("Char : ` is now allowed inside a boot name");
		}
		handlers.put(name, new XeBootHandler(boot));
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public XeBoot getBoot(String name) {
		XeBootHandler handler = handlers.get(name);
		if(handler != null) {
			return handler.boot;
		}
		else {
			return null;
		}
	}
	
	
	

	public void serveBootPage(HTTP2_Message request) {
		ng.pushAsyncTask(new XeWebBootPageTask(this, request));
	}
	
	
	
}
