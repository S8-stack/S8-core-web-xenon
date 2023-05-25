package com.s8.stack.servers.xenon;

import com.s8.arch.fluor.S8User;
import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.beryllium.object.BeObject;


@S8ObjectType(name = "base-s8-user")
public class XeUser extends BeObject implements S8User {
	
	
	@S8Field(name = "display-name")
	public String displayName;
	
	@S8Field(name = "password")
	public String password;
	
	@S8Field(name = "personal-space-id")
	public String personalSpaceId;
	
	
	/**
	 * User id = email address
	 * 
	 * @param id
	 */
	public XeUser(String id) {
		super(id);
	}
	
	
	public String getUsername() {
		return S8_key;
	}
	
	
	public String getPassword() {
		return password;
	}


	@Override
	public String getPersonalSpaceId() {
		return personalSpaceId;
	}
}
