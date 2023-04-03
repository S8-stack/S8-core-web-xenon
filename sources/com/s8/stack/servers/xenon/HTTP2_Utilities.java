package com.s8.stack.servers.xenon;

import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.headers.ContentLength;
import com.s8.stack.arch.helium.http2.headers.ContentType;
import com.s8.stack.arch.helium.http2.headers.Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.arch.helium.mime.MIME_Type;

public class HTTP2_Utilities {
	
	
	
	/**
	 * 
	 * @param request
	 * @param head
	 */
	public static void respondOk(HTTP2_Message response, LinkedBytes head) {

		response.status = new Status(HTTP2_Status.OK);
		response.contentType = new ContentType(MIME_Type.OCTET_STREAM);
		response.contentLength = new ContentLength(0);

		response.appendDataFragment(head);
		response.send();
	}
	
}
