package com.s8.core.web.xenon.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.titanium.service.MgConfiguration;
import com.s8.core.db.cobalt.CoConfiguration;
import com.s8.core.db.copper.CuRepoDBConfiguration;
import com.s8.core.db.tellurium.TeConfiguration;
import com.s8.core.io.xml.annotations.XML_SetElement;
import com.s8.core.io.xml.annotations.XML_Type;
import com.s8.core.io.xml.codebase.XML_Codebase;
import com.s8.core.io.xml.handler.type.XML_TypeCompilationException;
import com.s8.core.io.xml.parser.XML_ParsingException;
import com.s8.core.web.carbon.web.CarbonWebService;
import com.s8.core.web.helium.http2.HTTP2_WebConfiguration;
import com.s8.core.web.manganese.ManganeseWebService;

@XML_Type(root=true, name = "S8-Xenon-server")
public class XeConfiguration {

	public XeMode mode = XeMode.STANDARD;

	@XML_SetElement(tag = "mode")
	public void setXeMode(XeMode mode) {
		this.mode = mode;
	}

	public SiliconConfiguration silicon;

	@XML_SetElement(tag = "engine")
	public void setWebConfiguration(SiliconConfiguration config) {
		this.silicon = config;
	}

	public HTTP2_WebConfiguration web;

	@XML_SetElement(tag = "web")
	public void setWebConfiguration(HTTP2_WebConfiguration config) {
		this.web = config;
	}


	public CarbonWebService.Config carbon;

	@XML_SetElement(tag = "web-sources")
	public void setService(CarbonWebService.Config config) {
		this.carbon = config;
	}
	
	public ManganeseWebService.Config manganese;

	@XML_SetElement(tag = "mail")
	public void setMgService(ManganeseWebService.Config config) {
		this.manganese = config;
	}
	
	public MgConfiguration magnesium;

	@XML_SetElement(tag = "databases")
	public void setMagnesiumService(MgConfiguration config) {
		this.magnesium = config;
	}
	
	public TeConfiguration tablesDb;

	@XML_SetElement(tag = "tables-db")
	public void setTelluriumDbConfig(TeConfiguration config) {
		this.tablesDb = config;
	}
	
	
	public CoConfiguration cobalt;

	@XML_SetElement(tag = "spaces-db")
	public void setCobaltDbConfig(CoConfiguration config) {
		this.cobalt = config;
	}
	
	
	public CuRepoDBConfiguration copper;

	@XML_SetElement(tag = "repos-db")
	public void setCopperDbConfig(CuRepoDBConfiguration config) {
		this.copper = config;
	}
	
	
	

	public String http1_redirection;

	@XML_SetElement(tag = "HTTP1.1_redirection")
	public void setHTTP1Redirection(String redirection) {
		this.http1_redirection = redirection;
	}

	public XeConfiguration() {
		super();
	}



	/**
	 * 
	 * @param pathname
	 * @return
	 * @throws XML_TypeCompilationException
	 * @throws FileNotFoundException
	 */
	public static XeConfiguration load(XML_Codebase xml_Context, String pathname) 
			throws 
			XML_TypeCompilationException, 
			FileNotFoundException {

		// retrieve configuration
		InputStream inputStream = new FileInputStream(new File(pathname));
		XeConfiguration config = null;
		try (inputStream){
			config = (XeConfiguration) xml_Context.deserialize(inputStream);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (XML_ParsingException e) {
			e.printStackTrace();
		}	
		return config;
	}

}
