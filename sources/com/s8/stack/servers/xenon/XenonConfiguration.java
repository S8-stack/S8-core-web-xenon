package com.s8.stack.servers.xenon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.s8.arch.magnesium.service.MgConfiguration;
import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.io.xml.annotations.XML_SetElement;
import com.s8.io.xml.annotations.XML_Type;
import com.s8.io.xml.codebase.XML_Codebase;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;
import com.s8.io.xml.parser.XML_ParsingException;
import com.s8.stack.arch.helium.http2.HTTP2_WebConfiguration;
import com.s8.web.carbon.web.CarbonWebService;

@XML_Type(root=true, name = "S8-Xenon-server")
public class XenonConfiguration {


	public SiliconConfiguration silicon;

	@XML_SetElement(tag = "silicon")
	public void setWebConfiguration(SiliconConfiguration config) {
		this.silicon = config;
	}

	public HTTP2_WebConfiguration web;

	@XML_SetElement(tag = "web")
	public void setWebConfiguration(HTTP2_WebConfiguration config) {
		this.web = config;
	}


	public CarbonWebService.Config carbon;

	@XML_SetElement(tag = "carbon")
	public void setService(CarbonWebService.Config config) {
		this.carbon = config;
	}
	
	public MgConfiguration magnesium;

	@XML_SetElement(tag = "magnesium")
	public void setMagnesiumService(MgConfiguration config) {
		this.magnesium = config;
	}


	public String http1_redirection;

	@XML_SetElement(tag = "HTTP1.1_redirection")
	public void setHTTP1Redirection(String redirection) {
		this.http1_redirection = redirection;
	}

	public XenonConfiguration() {
		super();
	}



	/**
	 * 
	 * @param pathname
	 * @return
	 * @throws XML_TypeCompilationException
	 * @throws FileNotFoundException
	 */
	public static XenonConfiguration load(XML_Codebase xml_Context, String pathname) 
			throws 
			XML_TypeCompilationException, 
			FileNotFoundException {

		// retrieve configuration
		InputStream inputStream = new FileInputStream(new File(pathname));
		XenonConfiguration config = null;
		try (inputStream){
			config = (XenonConfiguration) xml_Context.deserialize(inputStream);
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
