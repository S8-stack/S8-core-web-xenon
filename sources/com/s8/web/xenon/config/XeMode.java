package com.s8.web.xenon.config;

public enum XeMode {

	/**
	 * Dynamic mode. Logged-in required.
	 */
	STANDARD(
			true,	/* db required */
			true), /* loggin-required */

	STATIC_FRONT_SERVER(
			false,	/* db required */
			false), /* loggin-required */

	DEBUG_LAMBDA(
			false,	/* db required */
			false); /* loggin-required */


	/**
	 * is db required
	 */
	public final boolean isDbSetupRequired;

	/**
	 * 
	 */
	public final boolean isLogginRequired;

	
	private XeMode(boolean isDbSetupRequired, boolean isLogginRequired) {
		this.isDbSetupRequired = isDbSetupRequired;
		this.isLogginRequired = isLogginRequired;
	}


}
