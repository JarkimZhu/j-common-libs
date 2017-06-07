/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.sql.mybatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyBiz<E> implements InvocationHandler {

	private static final Logger logger = LoggerFactory.getLogger(ProxyBiz.class);
	
	private E target;
	
	public ProxyBiz(E target){
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		ProxyHolder<SqlSession> proxyHolder;
		if(method.getName().startsWith("call")) {
			proxyHolder = SessionFactory.openSqlSession(method, ExecutorType.SIMPLE);
		} else {
			proxyHolder = SessionFactory.openSqlSession(method);
		}
		SqlSession session = proxyHolder.getHold();
		try {
			Object returnValue = method.invoke(target, args);
			if(method == proxyHolder.getMethod()){
				session.commit();
			}
			return returnValue;
		} catch (Exception e) {
			logger.error("Invoke " + target.getClass() + "." + method.getName() + " has some error !", e);
			session.rollback();
			throw e;
		} finally {
			if(method == proxyHolder.getMethod()){
				SessionFactory.closeSqlSession(session);
			}
		}
	}
}
