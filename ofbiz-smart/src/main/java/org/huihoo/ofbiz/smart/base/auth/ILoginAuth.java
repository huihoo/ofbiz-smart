package org.huihoo.ofbiz.smart.base.auth;

import java.util.Map;

/**
 * 登录认证拦截接口
 * @author zhang kui
 */
public interface ILoginAuth {
	
	
	/**
	 * 此接口登录认证接口，返回ture，表示处于登录状态，false未登录状态
	 * 需要实现次接口，实现自己的登录验证方式
	 * @param ctx 接口请求参数
	 * @return
	 */
	public boolean loginAuth(Map<String,Object> ctx);
	
}
