package com.s8.stack.servers.xenon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.s8.io.xml.handler.XML_Lexicon;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;

public class XSD_Generator {

	public static void main(String[] args) throws XML_TypeCompilationException, IOException {

		XML_Lexicon lexicon = new XML_Lexicon(XenonConfiguration.class);
		Writer writer = new FileWriter(new File("output/schema.xsd"));
		lexicon.xsd_writeSchema(writer);
		writer.close();
	}

}
