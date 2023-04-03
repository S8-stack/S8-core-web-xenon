package com.s8.stack.servers.xenon;

import com.s8.arch.silicon.async.AsyncTask;
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
public abstract class HTTP2_ResponseT1Task implements AsyncTask {

	protected final HTTP2_Message response;

	public HTTP2_ResponseT1Task(HTTP2_Message response) {
		super();
		this.response = response;
	}


	public static class OK extends HTTP2_ResponseT1Task {

		private final LinkedBytes head;

		public OK(HTTP2_Message response, LinkedBytes head) {
			super(response);
			this.head = head;
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



	public static class Error extends HTTP2_ResponseT1Task {

		private final HTTP2_Status status;

		private final String message;

		public Error(HTTP2_Message response, HTTP2_Status status, String message) {
			super(response);
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
}
