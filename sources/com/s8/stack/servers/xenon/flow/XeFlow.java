package com.s8.stack.servers.xenon.flow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.tasks.RespondOk;
import com.s8.stack.servers.xenon.tasks.SendError;

public class XeFlow {

	public final SiliconEngine ng;
	
	public final NeBranch branch;
	
	public final XeFlowContext context;
	
	public final XeFlowHandler handler;
	
	public final HTTP2_Message response;
	
	
	public final AtomicBoolean isRolling = new AtomicBoolean(false);

	public final AtomicBoolean isTerminated = new AtomicBoolean(false);

	

	public XeFlow(SiliconEngine ng, 
			NeBranch branch, 
			XeFlowContext context, 
			XeFlowHandler handler, 
			HTTP2_Message response) {
		super();
		this.ng = ng;
		this.branch = branch;
		this.context = context;
		this.handler = handler;
		this.response = response;
		
	}
	
	
	public void roll(boolean isContinued) {
		if(isTerminated.get() && isRolling.compareAndSet(isContinued, !isContinued)) {
			
			XeAsyncTask task = handler.deque.poll();
			
			if(task != null) {
				task.launch(ng, this);	
			}
			else {
				
				/* no more task, sending */
				onSucceed();
			}	
		}
	}
	
	
	
	private void onSucceed() {
		if(isTerminated.compareAndSet(false, true)) {
			isRolling.set(false);
			
			try {
				// publish response
				LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
				branch.outbound.publish(outflow);
				ng.pushAsyncTask(new RespondOk(response, outflow.getHead()));
			} 
			catch (IOException e) {
				e.printStackTrace();
				ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, e.getMessage()));
			}
			catch (Exception e) {
				e.printStackTrace();
				ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
			}	
		}
	}
	
	
	public void onFailed(Exception e) {
		if(isTerminated.compareAndSet(false, true)) {
			isRolling.set(false);
		
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.INTERNAL_SERVER_ERROR, e.getMessage()));
		}
		
	}
	
	
}
