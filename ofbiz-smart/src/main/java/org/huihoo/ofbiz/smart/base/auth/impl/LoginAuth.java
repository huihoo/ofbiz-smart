package org.huihoo.ofbiz.smart.base.auth.impl;

import java.util.Map;

import org.huihoo.ofbiz.smart.base.auth.ILoginAuth;

/**
 * 默认登录认证接口
 * @author zhang kui
 */
public class LoginAuth implements ILoginAuth{

	/**
	 * 默认返回false,此处用作扩展，以后提供一种默认的方式
	 */
	@Override
	public boolean loginAuth(Map<String, Object> ctx) {
		
		return false;
	}

	
}
