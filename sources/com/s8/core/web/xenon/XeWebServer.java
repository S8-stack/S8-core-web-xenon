package com.s8.core.web.xenon;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

import com.s8.core.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.core.arch.magnesium.databases.repository.store.RepoMgDatabase;
import com.s8.core.arch.magnesium.databases.space.store.SpaceMgDatabase;
import com.s8.core.arch.magnesium.service.MgConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.io.xml.codebase.XML_Codebase;
import com.s8.core.web.carbon.web.CarbonWebService;
import com.s8.core.web.helium.http1.HTTP1_Server;
import com.s8.core.web.helium.http1.pre.HTTP1_Redirection;
import com.s8.core.web.helium.http2.HTTP2_Server;
import com.s8.core.web.helium.http2.HTTP2_WebConfiguration;
import com.s8.core.web.helium.rx.RxConnection;
import com.s8.core.web.xenon.config.XeConfiguration;
import com.s8.core.web.xenon.config.XeMode;
import com.s8.core.web.xenon.sessions.XeWebSession;



/**
 * 
 * @author pc
 *
 */
public class XeWebServer extends HTTP2_Server {




	public static XeWebServer build(XeCodebase codebase, 
			XeBoot boot, 
			String configPathname) throws Exception {
		// build context
		XML_Codebase lexicon = XML_Codebase.from(XeConfiguration.class);

		XeConfiguration configuration = XeConfiguration.load(lexicon, configPathname);

		return new XeWebServer(codebase, boot, lexicon, configuration);
	}





	public final SiliconEngine siliconEngine;

	public final XeCodebase codebase;
	
	public final XeBoot boot;
	
	public final XeMode mode;


	/**
	 * carbon web service
	 */
	public final CarbonWebService carbonWebService;

	
	
	public final RecordsMgDatabase userDb;
	
	public final SpaceMgDatabase spaceDb;
	
	public final RepoMgDatabase repoDb;
	
	

	private final HTTP2_WebConfiguration webConfig;

	private boolean isRedirecting;

	private HTTP1_Server http1_redirectionServer;


	/**
	 * 
	 * @param configuration
	 * @param methodsService
	 * @throws Exception
	 */
	public XeWebServer(
			XeCodebase codebase,
			XeBoot boot,
			XML_Codebase lexicon,
			XeConfiguration configuration) throws Exception {
		super();


		this.codebase = codebase;
		this.boot = boot;


		webConfig = configuration.web;
		
		
		/** set mode */
		this.mode = configuration.mode;


		/*
		 * create SILICON ENGINE
		 */
		siliconEngine = new SiliconEngine(configuration.silicon);


		/**
		 * create CARBON SERVICE
		 */
		carbonWebService = new CarbonWebService(siliconEngine, lexicon, configuration.carbon);
		
		
		
		/* create user database */
		MgConfiguration magnesium = configuration.magnesium;
		
		if(magnesium != null) {
			if(magnesium.userDbConfigPathname != null) {
				userDb = new RecordsMgDatabase(siliconEngine, 
						codebase.userCodebase, 
						Path.of(magnesium.userDbConfigPathname));	
			}
			else {
				userDb = null;
			}
			
			
			
			
			/* create space database */
			if(magnesium.spaceDbConfigPathname != null) {
				spaceDb = new SpaceMgDatabase(siliconEngine, 
						codebase.spaceCodebase, 
						Path.of(magnesium.spaceDbConfigPathname));
			}
			else {
				spaceDb = null;
			}
			
			
			
			
			/* repository database */
			if(magnesium.repoDbConfigPathname != null) {
				repoDb = new RepoMgDatabase(siliconEngine, 
						codebase.repoCodebase, 
						Path.of(magnesium.repoDbConfigPathname));
			}
			else {
				repoDb = null;
			}
			
		}
		else {
			userDb = null;
			spaceDb = null;
			repoDb = null;
		}
		
		
		


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
		return new XeWebSession(socketChannel, this);
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
