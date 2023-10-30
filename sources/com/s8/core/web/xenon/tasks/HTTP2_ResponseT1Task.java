package com.s8.core.web.xenon.tasks;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class HTTP2_ResponseT1Task implements AsyncSiTask {

	protected final HTTP2_Message response;

	public HTTP2_ResponseT1Task(HTTP2_Message response) {
		super();
		this.response = response;
	}


}
