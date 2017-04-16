package cn.ufunny.game.libs.dao.mybatis.proxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {
	
	@SuppressWarnings("unchecked")
	public static final <E> E createBizProxy(E target){
		ProxyBiz<E> proxyBiz = new ProxyBiz<E>(target);
		Object proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), proxyBiz);
		return (E) proxy;
	}
}