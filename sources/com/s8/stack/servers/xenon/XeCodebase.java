package com.s8.stack.servers.xenon;

import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.exceptions.LiBuildException;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.exceptions.NdBuildException;


/**
 * 
 * @author pierreconvert
 *
 */
public class XeCodebase {


	/**
	 * 
	 */
	public final BeCodebase user;

	
	/**
	 * 
	 */
	public final LiCodebase space;

	
	/**
	 * 
	 */
	public final NdCodebase repo;


	/**
	 * 
	 * @param userType
	 * @param spaceTypes
	 * @param repoTypes
	 * @throws BeBuildException
	 * @throws LiBuildException
	 * @throws NdBuildException
	 */
	public XeCodebase(Class<?> userType, Class<?>[] spaceTypes, Class<?>[] repoTypes) 
			throws 
			BeBuildException, 
			LiBuildException, 
			NdBuildException {
		super();
		this.user = BeCodebase.from(userType);
		this.space = LiCodebase.from(spaceTypes);
		this.repo = NdCodebase.from(repoTypes);
	}

}
