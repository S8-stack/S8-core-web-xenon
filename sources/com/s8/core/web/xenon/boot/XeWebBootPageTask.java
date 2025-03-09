package com.s8.core.web.xenon.boot;

import com.s8.api.S8BootFunc;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.web.helium.http2.HTTP2_Status;
import com.s8.core.web.helium.http2.headers.ContentLength;
import com.s8.core.web.helium.http2.headers.ContentType;
import com.s8.core.web.helium.http2.headers.Status;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.helium.mime.MIME_Type;


/**
 * 
 * @author pierreconvert
 *
 */
public class XeWebBootPageTask implements AsyncSiTask {

	
	public final XeBootService service;
	
	public final HTTP2_Message request;


	/**
	 * 
	 * @param module
	 * @param request
	 */
	public XeWebBootPageTask(XeBootService service, HTTP2_Message request) {
		super();
		this.service = service;
		this.request = request;
	}

	@Override
	public MthProfile profile() {
		return MthProfile.FX0;
	}
	
	@Override
	public void run() {

		// resolve
		String webPathname = request.path.pathname;

		String bootName = S8BootFunc.getBootName(webPathname);
		
		// look-up
		XeBootHandler handler = service.handlers.get(bootName);
		
		if(handler!=null) {
			
			LinkedBytes page = handler.generatePage(service.title);
			
			sendPage(page);
			
		}
		else {
			sendError(HTTP2_Status.NOT_FOUND, "Failed to find page");
		}
	}

	public void sendPage(LinkedBytes bytes) {

		HTTP2_Message response = request.respond();
		

		
		response.status = new Status(HTTP2_Status.OK);

		// content-type
		response.contentType = new ContentType(MIME_Type.HTML);
		
		
		//response.cacheControl = new CacheControl(webAsset.getCacheControl().value);

		// content-length
		response.contentLength = new ContentLength(Long.toString(bytes.getBytecount()));

		response.appendDataFragment(bytes);
		response.send();
	}


	public void sendError(HTTP2_Status status, String message) {

		byte[] bytes = message.getBytes();
		HTTP2_Message response = request.respond();

		// headers
		response.status = new Status(status);
		// MIME_Type.get("text").getTemplate() == "text"
		response.contentType = new ContentType(MIME_Type.TEXT);
		response.contentLength = new ContentLength(Integer.toString(bytes.length));

		response.appendDataFragment(new LinkedBytes(bytes));

		response.send();

	}
	
	
	@Override
	public String describe() {
		return "(Carbon) RESPOND_TO_REQUEST task";
	}	

}
