package com.s8.stack.servers.xenon.session;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.s8.api.bytes.ByteInflow;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neon.core.NeBranch;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.stack.arch.helium.http2.HTTP2_Connection;
import com.s8.stack.arch.helium.http2.HTTP2_Endpoint;
import com.s8.stack.arch.helium.http2.HTTP2_Status;
import com.s8.stack.arch.helium.http2.messages.HTTP2_Message;
import com.s8.stack.servers.xenon.HTTP2_ResponseT1Task;
import com.s8.stack.servers.xenon.XeUser;
import com.s8.stack.servers.xenon.XenonWebServer;
import com.s8.stack.servers.xenon.flow.XeAsyncFlow;
import com.s8.stack.servers.xenon.protocol.XeRequestKeywords;
import com.s8.stack.servers.xenon.protocol.XeResponseKeywords;
import com.s8.stack.servers.xenon.tasks.RespondOk;
import com.s8.stack.servers.xenon.tasks.SendError;

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
public class XenonWebConnection extends HTTP2_Connection {

	/* <methods> */

	public final static String TOKEN_COOKIE_KEY = "QxTk";

	// final XenonWebService server;

	final XenonWebServer server;

	final SiliconEngine ng;


	private XeSessionStatus status = XeSessionStatus.UNINITIALIZED;

	private NeBranch neBranch;

	public final Object lock = new Object();


	public XeUser user;

	/**
	 * Current session
	 */
	// private WebSession session;

	public XenonWebConnection(SocketChannel socketChannel, XenonWebServer server) throws IOException {
		super(socketChannel, server.getWebConfiguration());
		this.server = server;
		this.ng = server.siliconEngine;

		// perform initialization
		HTTP2_initialize(server.getWebConfiguration());

		/*
		 * signUp = new SignUpModule(this); logIn = new LogInModule(this);
		 * 
		 * debugBoot = new DebugBootModule(this);
		 */

		status = XeSessionStatus.UNINITIALIZED;
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
		server.carbonWebService.serve(request);
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

			/* typically step 0 */
			case XeRequestKeywords.LOG_IN:
				serveLogIn(inflow, response);
				break;

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
	private void serveLogIn(ByteInflow inflow, HTTP2_Message response) throws IOException {

		/* can always go back to log-in -> no filter */
		String username = inflow.getStringUTF8();
		String password = inflow.getStringUTF8();

		server.userDb.get(0, username, output -> {
			try {

				/* false by default */
				boolean isSuccessfullyLoggedIn = false;

				if(output.isUserDefined) {

					XeUser user = (XeUser) output.user;

					isSuccessfullyLoggedIn = user.password.equals(password);	

					/* log-in effectively */
					if (isSuccessfullyLoggedIn) {
						this.user = user;
						this.status = XeSessionStatus.LOGGED_IN;
					}
				}

				LinkedByteOutflow outflow = new LinkedByteOutflow(64);
				outflow.putUInt8(isSuccessfullyLoggedIn ? 
						XeResponseKeywords.SUCCESSFULLY_LOGGED_IN : XeResponseKeywords.LOG_IN_FAILED);
				LinkedBytes head = outflow.getHead();
				ng.pushAsyncTask(new RespondOk(response, head));

			} catch (IOException e) {
				e.printStackTrace();
				ng.pushAsyncTask(new SendError(response, HTTP2_Status.Locked, "Error"));

			}
		}, 0x0L);

	}




	/**
	 * 
	 * @param inflow
	 * @param response
	 */
	private void serveBoot(ByteInflow inflow, HTTP2_Message response) {
		if(status == XeSessionStatus.LOGGED_IN) {
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
					status = XeSessionStatus.BOOTED_UP;

					XeAsyncFlow flow = new XeAsyncFlow(server, ng, user, neBranch, response);

					try {
						// boot...
						server.boot.boot(neBranch, flow);

					} catch (Exception e) {
						e.printStackTrace();
						ng.pushAsyncTask(new SendError(response, HTTP2_Status.BAD_REQUEST, "Error"));
					}
				}
			});
		}
		else {
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.Network_Authentication_Required, "Must be logged-in"));
		}
	}



	/**
	 * 
	 * @param inflow
	 * @param response
	 */
	private void serveFunc(ByteInflow inflow, HTTP2_Message response) {
		if(status == XeSessionStatus.BOOTED_UP) {
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

							XeAsyncFlow flow = new XeAsyncFlow(server, ng, user, neBranch, response);

							/* branch inbound -> fire the appropriate function */
							neBranch.inbound.consume(flow, inflow);

						} catch (IOException e) {
							ng.pushAsyncTask(new SendError(response, HTTP2_Status.BAD_REQUEST, "Error: " + e.getMessage()));
						}	
					}
					else {
						ng.pushAsyncTask(new SendError(response, HTTP2_Status.Bad_Gateway, "Error"));
					}

				}

			});
		}
		else {
			ng.pushAsyncTask(new SendError(response, HTTP2_Status.SWITCHING_PROTOCOL, "Must be booted-up"));
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
