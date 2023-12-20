package com.s8.core.web.xenon;

import com.s8.api.exceptions.S8BuildException;
import com.s8.core.bohr.beryllium.exception.BeBuildException;
import com.s8.core.bohr.neodymium.exceptions.NdBuildException;


/**
 * 
 * @author pierreconvert
 *
 */
public class XeCodebase {


	/**
	 * 
	 */
	public final Class<?>[] tableClasses;

	
	/**
	 * 
	 */
	public final Class<?>[] spaceClasses;
	

	
	/**
	 * 
	 */
	public final Class<?>[] repoClasses;


	/**
	 * 
	 * @param userType
	 * @param spaceTypes
	 * @param repoTypes
	 * @throws BeBuildException
	 * @throws S8BuildException
	 * @throws NdBuildException
	 */
	public XeCodebase(
			Class<?>[] rowClasses, 
			Class<?>[] spaceClasses,
			Class<?>[] repoClasses) {
		super();
		this.tableClasses = rowClasses;
		this.spaceClasses = spaceClasses;
		this.repoClasses = repoClasses;
	}

}
