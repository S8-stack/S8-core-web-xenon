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
	
	exports com.s8.core.web.xenon.config;
	exports com.s8.core.web.xenon.sessions;

	exports com.s8.core.web.xenon.flow;
	
	
	/* </xenon> */

	requires transitive com.s8.api;
	
	requires transitive com.s8.core.io.bytes;
	requires transitive com.s8.core.io.bohr.atom;
	requires transitive com.s8.core.io.bohr.neon;
	requires transitive com.s8.core.io.xml;
	requires transitive com.s8.core.arch.silicon;
	
	requires transitive com.s8.core.io.csv;
	requires transitive com.s8.core.web.helium;
	requires transitive com.s8.core.web.carbon;
	requires transitive com.s8.core.io.bohr.beryllium;
	requires transitive com.s8.core.io.bohr.lithium;
	requires transitive com.s8.core.io.bohr.neodymium;
	requires transitive com.s8.core.arch.magnesium;
	
}