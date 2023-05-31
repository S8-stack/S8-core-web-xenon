package com.s8.stack.servers.xenon;

import com.s8.arch.magnesium.databases.space.store.MgSpaceInitializer;
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
	public final BeCodebase userCodebase;

	
	/**
	 * 
	 */
	public final LiCodebase spaceCodebase;
	
	
	public final MgSpaceInitializer spaceInitializer;

	
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
	 * @throws LiBuildException
	 * @throws NdBuildException
	 */
	public XeCodebase(Class<?> userType, 
			Class<?>[] spaceTypes, MgSpaceInitializer spaceInitializer,
			Class<?>[] repoTypes) 
			throws 
			BeBuildException, 
			LiBuildException, 
			NdBuildException {
		super();
		this.userCodebase = BeCodebase.from(userType);
		this.spaceCodebase = LiCodebase.from(spaceTypes);
		this.spaceInitializer = spaceInitializer;
		this.repoCodebase = NdCodebase.from(repoTypes);
	}

}
