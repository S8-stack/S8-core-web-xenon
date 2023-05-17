package com.s8.stack.servers.xenon;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.xml.handler.XML_Lexicon;
import com.s8.stack.arch.helium.http1.HTTP1_Server;
import com.s8.stack.arch.helium.http1.pre.HTTP1_Redirection;
import com.s8.stack.arch.helium.http2.HTTP2_Server;
import com.s8.stack.arch.helium.http2.HTTP2_WebConfiguration;
import com.s8.stack.arch.helium.rx.RxConnection;
import com.s8.stack.servers.xenon.session.XenonWebConnection;
import com.s8.web.carbon.web.CarbonWebService;



/**
 * 
 * @author pc
 *
 */
public class XenonWebServer extends HTTP2_Server {




	public static XenonWebServer build(XeBoot boot, String configPathname) throws Exception {
		// build context
		XML_Lexicon lexicon = new XML_Lexicon(XenonConfiguration.class);

		XenonConfiguration configuration = XenonConfiguration.load(lexicon, configPathname);

		return new XenonWebServer(boot, lexicon, configuration);
	}





	public final SiliconEngine siliconEngine;

	public final XeBoot boot;


	/**
	 * 
	 */
	public final CarbonWebService carbonWebService;


	private final HTTP2_WebConfiguration webConfig;

	private boolean isRedirecting;

	private HTTP1_Server http1_redirectionServer;


	/**
	 * 
	 * @param configuration
	 * @param methodsService
	 * @throws Exception
	 */
	public XenonWebServer(XeBoot boot,
			XML_Lexicon lexicon,
			XenonConfiguration configuration) throws Exception {
		super();


		this.boot = boot;


		webConfig = configuration.web;


		/*
		 * create SILICON ENGINE
		 */
		siliconEngine = new SiliconEngine(configuration.silicon);


		/**
		 * create CARBON SERVICE
		 */
		carbonWebService = new CarbonWebService(siliconEngine, lexicon, configuration.carbon);


		if(isRedirecting = (configuration.http1_redirection!=null)) {
			http1_redirectionServer = new HTTP1_Redirection(siliconEngine, configuration.http1_redirection);
		}
	}


	@Override
	public HTTP2_WebConfiguration getWebConfiguration() {
		return webConfig;
	}


	@Override
	public RxConnection open(SocketChannel socketChannel) throws IOException {
		return new XenonWebConnection(socketChannel, this);
	}


	@Override
	public void start() throws Exception {
		
		// start engine (first of all)
		siliconEngine.start();
		
		// start carbon service (before web server)
		carbonWebService.start();

		// redirection module
		if(isRedirecting) { http1_redirectionServer.start(); }
		
		// start server
		super.start();

		System.out.println("Xenon server launched... ");
	}


	@Override
	public void stop() throws Exception {
		
		// stop server
		super.stop();

		// stop redirection
		if(isRedirecting) { http1_redirectionServer.stop(); }
		
		// stop service
		carbonWebService.stop();

		// finally stop the engine
		siliconEngine.stop();

	}

	@Override
	public SiliconEngine getEngine() {
		return siliconEngine;
	}
}
