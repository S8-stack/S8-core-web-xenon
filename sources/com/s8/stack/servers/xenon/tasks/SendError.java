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

public class SendError implements AsyncSiTask {

	public final HTTP2_Message response;
	
	private final HTTP2_Status status;

	private final String message;

	public SendError(HTTP2_Message response, HTTP2_Status status, String message) {
		super();
		this.response = response;
		this.status = status;
		this.message = message;
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
		response.status = new Status(status);
		response.contentType = new ContentType(MIME_Type.OCTET_STREAM);
		response.contentLength = new ContentLength(0);

		response.appendDataFragment(new LinkedBytes(message.getBytes()));
		response.send();	
	}

}
