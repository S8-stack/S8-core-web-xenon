package com.s8.core.web.xenon.flow.delivery;

import com.s8.api.flow.delivery.S8WebResource;
import com.s8.api.flow.delivery.S8WebResourceGenerator;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.web.helium.http2.HTTP2_Status;
import com.s8.core.web.helium.http2.headers.ContentLength;
import com.s8.core.web.helium.http2.headers.ContentType;
import com.s8.core.web.helium.http2.headers.Status;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.helium.mime.MIME_Type;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

/**
 * 
 * @author pierreconvert
 *
 */
public class XeDeliveryOp extends XeAsyncFlowOperation {

	private final HTTP2_Message response;

	private final S8WebResourceGenerator generator;

	/**
	 * 
	 * @param generator
	 */
	public XeDeliveryOp(XeAsyncFlow flow, S8WebResourceGenerator generator, HTTP2_Message response) {
		super(flow);
		this.generator = generator;
		this.response = response;
	}


	@Override
	public AsyncSiTask createTask() {
		return new AsyncSiTask(){




			@Override
			public String describe() {
				return "Generating Delivery task";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX3;
			}

			@Override
			public void run() {
				try {
					// publish response
					S8WebResource resource = generator.generate();
					LinkedBytes head = new LinkedBytes(resource.data);
					sendResponse(head);
				} 
				catch (Exception e) {
					e.printStackTrace();
					sendError(HTTP2_Status.PROCESSING, e.getMessage());
				}
			}
		};
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
