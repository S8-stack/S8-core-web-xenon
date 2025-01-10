package com.s8.core.web.xenon;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.db.cobalt.CoConfiguration;
import com.s8.core.db.cobalt.store.SpaceMgDatabase;
import com.s8.core.db.copper.CuRepoDB;
import com.s8.core.db.copper.CuRepoDBConfiguration;
import com.s8.core.db.tellurium.TeConfiguration;
import com.s8.core.db.tellurium.store.TeDatabaseHandler;
import com.s8.core.io.xml.codebase.XML_Codebase;
import com.s8.core.web.carbon.web.CarbonWebService;
import com.s8.core.web.helium.http1.HTTP1_Server;
import com.s8.core.web.helium.http1.pre.HTTP1_Redirection;
import com.s8.core.web.helium.http2.HTTP2_Server;
import com.s8.core.web.helium.http2.HTTP2_WebConfiguration;
import com.s8.core.web.helium.rx.RxConnection;
import com.s8.core.web.manganese.ManganeseWebService;
import com.s8.core.web.xenon.boot.XeBoot;
import com.s8.core.web.xenon.boot.XeBootService;
import com.s8.core.web.xenon.config.XeConfiguration;
import com.s8.core.web.xenon.config.XeMode;
import com.s8.core.web.xenon.sessions.XeWebConnection;



/**
 * 
 * @author pc
 *
 */
public class XeWebServer extends HTTP2_Server {




	public static XeWebServer build(XeCodebase codebase, 
			XeBoot[] boot, 
			String configPathname) throws Exception {
		// build context
		XML_Codebase lexicon = XML_Codebase.from(XeConfiguration.class);

		XeConfiguration configuration = XeConfiguration.load(lexicon, configPathname);

		return new XeWebServer(codebase, boot, lexicon, configuration);
	}





	public final SiliconEngine siliconEngine;

	public final XeCodebase codebase;
	
	public final String pageTitle;
	
	
	public final XeMode mode;
	
	
	public final XeBootService bootService;


	/**
	 * carbon web service
	 */
	public final CarbonWebService carbonWebService;

	public final ManganeseWebService manganeseWebService;
	
	
	public final TeDatabaseHandler tablesDb;
	
	public final SpaceMgDatabase spacesDb;
	
	public final CuRepoDB reposDb;
	
	

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
			XeBoot[] boots,
			XML_Codebase lexicon,
			XeConfiguration configuration) throws Exception {
		super();


		this.codebase = codebase;
		


		webConfig = configuration.web;
		

		/** set name */
		this.pageTitle = configuration.pageTitle;
		
		
		/** set mode */
		this.mode = configuration.mode;
		

		/*
		 * create SILICON ENGINE
		 */
		siliconEngine = new SiliconEngine(configuration.silicon);
		
		/*
		 * Create the boot service
		 */
		bootService = new XeBootService(siliconEngine, pageTitle, boots);

		/**
		 * create CARBON SERVICE
		 */
		carbonWebService = new CarbonWebService(siliconEngine, lexicon, configuration.carbon);
		
		
		/**
		 * Create manganse service
		 */
		manganeseWebService = configuration.manganese != null ? 
				new ManganeseWebService(configuration.manganese) : null;	
		
		
		/* create tables database */
		TeConfiguration tellurium = configuration.tablesDb;
		tablesDb = tellurium != null ? tellurium.create(siliconEngine, codebase.tableClasses) : null;
		
		/* create spaces database */
		CoConfiguration cobalt = configuration.cobalt;
		spacesDb = cobalt != null ? cobalt.create(siliconEngine, codebase.spaceClasses) : null;
		
		/* create repositories database */
		CuRepoDBConfiguration copper = configuration.copper;
		reposDb = copper != null ? copper.create(siliconEngine, codebase.repoClasses) : null;
		
			


		if(isRedirecting = (configuration.http1_redirection!=null)) {
			http1_redirectionServer = new HTTP1_Redirection(siliconEngine, configuration.http1_redirection);
		}
	}


	@Override
	public HTTP2_WebConfiguration getWebConfiguration() {
		return webConfig;
	}


	@Override
	public RxConnection createConnection(SelectionKey key, SocketChannel socketChannel) throws IOException {
		return new XeWebConnection(key, socketChannel, this);
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
	public SiliconEngine getSiliconEngine() {
		return siliconEngine;
	}
}
