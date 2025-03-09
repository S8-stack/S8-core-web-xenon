package com.s8.core.web.xenon.boot;

import java.nio.charset.StandardCharsets;

import com.s8.api.S8BootFunc;
import com.s8.core.io.bytes.linked.LinkedBytes;



class XeBootHandler {

	
	public final S8BootFunc boot;
	
	private byte[] page;

	
	public XeBootHandler(S8BootFunc boot) {
		super();
		this.boot = boot;
	}
	
	
	protected synchronized LinkedBytes generatePage(String title) {
		
		if(page == null) {
			String name = boot.name;
			
			StringBuilder builder = new StringBuilder();
			
			
			builder.append("<!DOCTYPE html>\n");
			builder.append("<html>\n");
			
			
			/* <head> */
			builder.append("<head>\n");
			builder.append("<meta charset=\"UTF-8\">\n");
			builder.append("<title>" + title + "</title>\n");
			
			builder.append("<link rel=\"stylesheet\" href=\"/style.css\">\n");
			
			
			builder.append("<script type=\"module\">\n");
			builder.append("\timport { launch } from '/S8-core-web-xenon/XeLauncher.js';\n");
			builder.append("\twindow.launch = launch;\n");
			builder.append("</script>\n");
			
			builder.append("</head>\n");
			/* </head> */
			

			/* <body> */
			builder.append("<body onload=\"launch('" + name + "')\">\n");
			builder.append("</body>\n");
			/* </body> */
			
		
			builder.append("</html>\n");
			
			String page_HTML = builder.toString();
			page = page_HTML.getBytes(StandardCharsets.UTF_8);		
		}
		
	
		return new LinkedBytes(page);
	}
	
}
