package com.s8.core.web.xenon;

import com.s8.api.exceptions.S8BuildException;
import com.s8.core.bohr.beryllium.codebase.BeCodebase;
import com.s8.core.bohr.beryllium.exception.BeBuildException;
import com.s8.core.bohr.lithium.codebase.LiCodebase;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
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
	public final BeCodebase userCodebase;

	
	/**
	 * 
	 */
	public final LiCodebase spaceCodebase;
	

	
	/**
	 * 
	 */
	public final NdCodebase repoCodebase;


	/**
	 * 
	 * @param userType
	 * @param spaceTypes
	 * @param repoTypes
	 * @throws BeBuildException
	 * @throws S8BuildException
	 * @throws NdBuildException
	 */
	public XeCodebase(Class<?> userType, 
			Class<?>[] spaceTypes,
			Class<?>[] repoTypes) 
			throws 
			BeBuildException, 
			S8BuildException, 
			NdBuildException {
		super();
		this.userCodebase = BeCodebase.from(userType);
		this.spaceCodebase = LiCodebase.from(spaceTypes);
		this.repoCodebase = NdCodebase.from(repoTypes);
	}

}
