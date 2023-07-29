package com.s8.stack.servers.xenon.tasks;

import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.headers.ContentLength;
import com.s8.stack.arch.helium.http2.headers.ContentType;
import com.s8.stack.arch.helium.http2.headers.Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.arch.helium.mime.MIME_Type;


/**
 * 
 * @author pierreconvert
 *
 */
public class RespondOk implements AsyncSiTask {

	public final HTTP2_Message response;
	
	private final LinkedBytes head;

	public RespondOk(HTTP2_Message response, LinkedBytes head) {
		super();
		this.response = response;
		this.head = head;
	}
	
	
	public RespondOk(HTTP2_Message response, String message) {
		super();
		this.response = response;
		this.head = new LinkedBytes(message.getBytes());;
	}
	
	

	@Override
	public String describe() {
		return "Responding to a request";
	}
	
	

	@Override
	public MthProfile profile() {
		return MthProfile.WEB_REQUEST_PROCESSING;
	}

	
	
	@Override
	public void run() {
		int length = (int) head.getBytecount();
		
		response.status = new Status(HTTP2_Status.OK);
		response.contentType = new ContentType(MIME_Type.OCTET_STREAM);
		response.contentLength = new ContentLength(length);

		response.appendDataFragment(head);
		response.send();	
	}
}
