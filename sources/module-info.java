/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.web.xenon {
	
	/* <xenon> */
	
	
	exports com.s8.core.web.xenon;
	exports com.s8.core.web.xenon.boot;
	exports com.s8.core.web.xenon.config;
	exports com.s8.core.web.xenon.sessions;

	exports com.s8.core.web.xenon.flow;
	exports com.s8.core.web.xenon.protocol;
	
	
	/* </xenon> */

	requires transitive com.s8.api;
	
	requires transitive com.s8.core.io.bytes;
	requires transitive com.s8.core.io.xml;
	
	requires transitive com.s8.core.arch.silicon;
	requires transitive com.s8.core.arch.titanium;
	
	
	requires transitive com.s8.core.web.helium;
	requires transitive com.s8.core.web.carbon;
	requires transitive com.s8.core.web.manganese;
	
	
	requires transitive com.s8.core.bohr.atom;
	requires transitive com.s8.core.bohr.neon;
	requires transitive com.s8.core.bohr.beryllium;
	requires transitive com.s8.core.bohr.lithium;
	requires transitive com.s8.core.bohr.neodymium;
	
	
	requires transitive com.s8.core.db.tellurium;
	requires transitive com.s8.core.db.cobalt;
	requires transitive com.s8.core.db.copper;
	
	
}