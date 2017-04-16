package cn.ufunny.game.libs.dao.mybatis.proxy;

import java.lang.reflect.Method;

public class ProxyHolder<E> {
	private Method method;
	private E hold;
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public E getHold() {
		return hold;
	}
	public void setHold(E hold) {
		this.hold = hold;
	}
}
