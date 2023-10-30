package com.s8.core.web.xenon.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.s8.core.io.xml.codebase.XML_Codebase;
import com.s8.core.io.xml.handler.type.XML_TypeCompilationException;

public class XSD_Generator {

	public static void main(String[] args) throws XML_TypeCompilationException, IOException {

		XML_Codebase lexicon = XML_Codebase.from(XeConfiguration.class);
		Writer writer = new FileWriter(new File("output/schema.xsd"));
		lexicon.xsd_writeSchema(writer);
		writer.close();
	}

}
