package com.s8.core.web.xenon.flow.mail;

import java.io.IOException;

import com.s8.api.flow.mail.SendMailS8Request;
import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.web.manganese.ManganeseWebService;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.flow.XeAsyncFlowOperation;

public class SendMailOp extends XeAsyncFlowOperation {

	
	public static AsyncSiTask createOutOfSyncTask(
			ManganeseWebService service, 
			SendMailS8Request request) {
		
		return new AsyncSiTask() {

			@Override
			public void run() {

				if(service != null) {
					service.sendMail(request);
				}
				else {
					/* issue error directly */
					request.onFailed(new IOException("mail service is undefined in this context"));
				}
			}

			@Override
			public MthProfile profile() { 
				return MthProfile.IO_DATA_LAKE; 
			}


			@Override
			public String describe() { 
				return "Committing"; 
			}
		};
	}
	

	public final ManganeseWebService service;

	public final SendMailS8Request request;


	public SendMailOp(
			XeAsyncFlow flow, 
			ManganeseWebService service,
			SendMailS8Request request) {
		super(flow);
		this.service = service;
		this.request = request;
	}



	@Override
	public AsyncSiTask createTask() { 
		return new AsyncSiTask() {


			@Override
			public void run() {

				if(service != null) {
					service.sendMail(request);
				}
				else {
					/* issue error directly */
					request.onFailed(new IOException("mail service is undefined in this context"));
				}

				/* continue */
				flow.roll(true);
			}


			@Override
			public MthProfile profile() { 
				return MthProfile.FX6; 
			}


			@Override
			public String describe() { 
				return "Committing"; 
			}
		};
	}
}
