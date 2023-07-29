package com.s8.stack.servers.xenon;

import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;


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
