package com.s8.stack.servers.xenon.flow;

import java.io.IOException;

import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.headers.ContentLength;
import com.s8.stack.arch.helium.http2.headers.ContentType;
import com.s8.stack.arch.helium.http2.headers.Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.arch.helium.mime.MIME_Type;
import com.s8.stack.servers.xenon.XenonWebServer;


/**
 * 
 * @author pierreconvert
 *
 */
public class SendOp extends XeAsyncFlowOperation {


	public final NeBranch branch;

	public final HTTP2_Message response;


	/**
	 * 
	 * @param server
	 * @param flow
	 * @param branch
	 * @param response
	 */
	public SendOp(XenonWebServer server, XeAsyncFlow flow, NeBranch branch, HTTP2_Message response) {
		super(server, flow);
		this.branch = branch;
		this.response = response;
	}



	private class Task implements AsyncSiTask {


		public Task() {
			super();
		}

		@Override
		public String describe() {
			return "Closing async flow by responding";
		}

		@Override
		public MthProfile profile() {
			return MthProfile.FX1;
		}

		@Override
		public void run() {
			try {

				// publish response
				LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
				branch.outbound.publish(outflow);
				LinkedBytes head = outflow.getHead();
				sendResponse(head);
			} 
			catch (IOException e) {
				e.printStackTrace();
				sendError(HTTP2_Status.PROCESSING, e.getMessage());
			}
		}



		/**
		 * 
		 * @param head
		 */
		private void sendResponse(LinkedBytes head) {
			int length = (int) head.getBytecount();

			response.status = new Status(HTTP2_Status.OK);
			response.contentType = new ContentType(MIME_Type.OCTET_STREAM);
			response.contentLength = new ContentLength(length);

			response.appendDataFragment(head);
			response.send();

			flow.terminate();
		}



		/**
		 * 
		 * @param status
		 * @param message
		 */
		private void sendError(HTTP2_Status status, String message) {
			response.status = new Status(status);
			response.contentType = new ContentType(MIME_Type.OCTET_STREAM);
			response.contentLength = new ContentLength(0);

			response.appendDataFragment(new LinkedBytes(message.getBytes()));
			response.send();

			flow.terminate();
		}

	}



	@Override
	public AsyncSiTask createTask() { 
		return new Task();
	}
}
