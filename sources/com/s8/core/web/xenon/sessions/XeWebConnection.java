package com.s8.core.web.xenon.sessions;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.s8.api.bytes.ByteInflow;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.bytes.linked.LinkedByteInflow;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.web.helium.http2.HTTP2_Connection;
import com.s8.core.web.helium.http2.HTTP2_Endpoint;
import com.s8.core.web.helium.http2.HTTP2_Status;
import com.s8.core.web.helium.http2.messages.HTTP2_Message;
import com.s8.core.web.xenon.XeUser;
import com.s8.core.web.xenon.XeWebServer;
import com.s8.core.web.xenon.boot.XeBootFunc;
import com.s8.core.web.xenon.flow.XeAsyncFlow;
import com.s8.core.web.xenon.protocol.XeRequestKeywords;
import com.s8.core.web.xenon.protocol.XeRequestSyntax;
import com.s8.core.web.xenon.tasks.HTTP2_ResponseT1Task;
import com.s8.core.web.xenon.tasks.SendError;
import com.s8.io.bohr.neon.core.NeBranch;

/**
 * This fourth level of <code>Endpoint</code> encapsulates session-related
 * aspects.
 * 
 * <p>
 * <b>Important notice</b>: Since WebConnection is base on
 * com.qx.level0.io.http2 package that implements <b>Reactor-based
 * </p>
 * pattern, a connection is handled by a single Thread, so no thread safety
 * issues to handle
 * </p>
 * 
 * 
 * @author pc
 *
 */
public class XeWebConnection extends HTTP2_Connection {

	/* <methods> */

	public final static String TOKEN_COOKIE_KEY = "QxTk";

	final SiliconEngine ng;

	final XeWebServer server;


	private volatile NeBranch neBranch;

	//public final Object lock = new Object();

	public final AtomicBoolean isBusy = new AtomicBoolean(false);


	public volatile XeUser user;


	public boolean hasBootedUp = false;

	/**
	 * Current session
	 */
	// private WebSession session;

	public XeWebConnection(SelectionKey key, SocketChannel channel, XeWebServer server) throws IOException {
		super("xenon", key, channel, server.getWebConfiguration());

		this.ng = server.siliconEngine;
		this.server = server;


		// perform initialization
		HTTP2_initialize(server.getWebConfiguration());
	}


	@Override
	public HTTP2_Endpoint getEndpoint() {
		return server;
	}


	@Override
	public void HTTP2_onMessageReceived(HTTP2_Message request) {

		switch (request.method.value) {

		case GET:
			serve_GET(request);
			break;

		case POST:
			serve_POST(request);
			break;

		default:
			serveError(request, HTTP2_Status.METHOD_NOT_ALLOWED, "This server does not implement OPTIONS method");

			break;

		}
	}

	/**
	 * 
	 * @param request
	 */
	private void serve_GET(HTTP2_Message request) {
		if(XeRequestSyntax.isBootPage(request.path.pathname)) {
			/* serve boot */
			server.bootService.serveBootPage(request);
		}
		else {
			/* serve resource */
			server.carbonWebService.serve(request);
		}
	}
	
	

	/**
	 * 
	 * @param request
	 */
	private void serve_POST(HTTP2_Message request) {
		try {

			// convert request DATA payload into ByteInflow
			LinkedBytes head = request.getDataFragmentHead();

			/* create inflow from payload */
			ByteInflow inflow = new LinkedByteInflow(head);

			HTTP2_Message response = request.respond();

			// retrieve fork code
			int forkCode = inflow.getUInt8();

			switch (forkCode) {

			/* typically step 1 : sucessful log-in call boot */
			case XeRequestKeywords.BOOT:
				// debugBoot.serve(neRequest, response); break;
				serveBoot(inflow, response);
				break;

				/* typically further steps */
			case XeRequestKeywords.RUN_FUNC:
				serveFunc(inflow, response);
				break;

			default:
				throw new IOException("POST_FORK_CODE unsupported: " + forkCode);

			}

		} catch (IOException e) {
			serveError(request, HTTP2_Status.INTERNAL_SERVER_ERROR, "servePOST failed");
		}
	}





	/**
	 * 
	 * @param inflow
	 * @param response
	 * @throws IOException 
	 */
	private void serveBoot(ByteInflow inflow, HTTP2_Message response) throws IOException {
		
		String name = inflow.getStringUTF8();
		XeBootFunc boot = server.bootService.getBoot(name);
		
		if(boot != null) {
			ng.pushAsyncTask(new HTTP2_ResponseT1Task(response) {

				public @Override String describe() {
					return "Normal server processing";
				}

				public @Override MthProfile profile() {
					return MthProfile.FX2;
				}

				@Override
				public void run() {
					
					// build new branch
					neBranch = new NeBranch("w");

					XeAsyncFlow flow = new XeAsyncFlow(ng, server, XeWebConnection.this, neBranch, response);

					try {
						// boot...
						boot.boot(neBranch, flow);

					} catch (Exception e) {
						e.printStackTrace();
						ng.pushAsyncTask(new SendError(response, HTTP2_Status.BAD_REQUEST, "Error"));
					}
				}
			});	
		}
		else {
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.BAD_REQUEST, "Boot cannot be found"));
		}
	}



	/**
	 * 
	 * @param inflow
	 * @param response
	 */
	private void serveFunc(ByteInflow inflow, HTTP2_Message response) {
		if(isBusy.compareAndSet(false, true)) {

			ng.pushAsyncTask(new HTTP2_ResponseT1Task(response) {

				public @Override String describe() {
					return "Normal server processing";
				}

				public @Override MthProfile profile() {
					return MthProfile.FX2;
				}

				@Override
				public void run() {
					if(neBranch != null) { /* on-going session _*/
						try {

							XeAsyncFlow flow = new XeAsyncFlow(ng, server, XeWebConnection.this, neBranch, response);

							/* branch inbound -> fire the appropriate function */
							neBranch.inbound.consume(flow, inflow);

						} catch (IOException e) {
							
							/* clear busy */
							isBusy.set(false);
							
							ng.pushAsyncTask(new SendError(response, HTTP2_Status.BAD_REQUEST, "Error: " + e.getMessage()));
						}	
					}
					else {

						/* clear busy */
						isBusy.set(false);
						
						ng.pushAsyncTask(new SendError(response, HTTP2_Status.Bad_Gateway, "Error"));
					}
				}
			});
		}
		else {
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.PROCESSING, "Busy connection"));
		}
	}



	/**
	 * 
	 * @param request
	 * @param status
	 * @param message
	 */
	private void serveError(HTTP2_Message request, HTTP2_Status status, String message) {
		ng.pushAsyncTask(new SendError(request.respond(), status, message));
	}


}
