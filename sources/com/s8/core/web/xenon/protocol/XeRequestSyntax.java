package com.s8.core.web.xenon.protocol;

public class XeRequestSyntax {
	
	public final static String BOOT_PAGE_PREFIX = "/page/";
	
	
	public final static String MAIN_BOOT_PAGE = "main";
	
	
	
	/**
	 * 
	 * @param pathname
	 * @return
	 */
	public static boolean isBootPage(String pathname) {
		return BOOT_PAGE_PREFIX.equals(pathname.substring(0, BOOT_PAGE_PREFIX.length()));
	}
	
	public static String getBootName(String pathname) {
		return pathname.substring(BOOT_PAGE_PREFIX.length());
	}

}
