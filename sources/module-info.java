/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.web.xenon {
	
	/* <xenon> */
	exports com.s8.stack.servers.xenon;
	/* </xenon> */

	
	
	requires transitive com.s8.io.bytes;
	requires transitive com.s8.io.bohr.neon;
	requires transitive com.s8.io.xml;
	requires transitive com.s8.arch.silicon;
	
	requires transitive com.s8.io.csv;
	requires transitive com.s8.web.helium;
	requires transitive com.s8.web.carbon;
	requires com.s8.io.bohr.atom;
}