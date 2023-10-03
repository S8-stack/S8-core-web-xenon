package com.s8.stack.servers.xenon.flow.delivery;

import com.s8.api.flow.delivery.S8WebResource;
import com.s8.api.flow.delivery.S8WebResourceGenerator;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.tasks.RespondOk;
import com.s8.stack.servers.xenon.tasks.SendError;

/**
 * 
 * @author pierreconvert
 *
 */
public class XeDeliveryTask implements AsyncSiTask {
	
	public final SiliconEngine ng;
	
	public final HTTP2_Message response;
	
	public final S8WebResourceGenerator generator;


	/**
	 * 
	 * @param generator
	 */
	public XeDeliveryTask(SiliconEngine ng, HTTP2_Message response, S8WebResourceGenerator generator) {
		super();
		this.ng = ng;
		this.response = response;
		this.generator = generator;
	}

	
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
			S8WebResource resource = generator.generate();
			LinkedBytes head = new LinkedBytes(resource.data);
			ng.pushAsyncTask(new RespondOk(response, head));
		}
		catch(Exception exception) {
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, exception.getMessage()));
		}	
	}

}
