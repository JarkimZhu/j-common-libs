package cn.ufunny.game.libs.dao.mybatis.proxy;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionFactory.class);
	
	private static SqlSessionFactory sqlSessionFactory;
	private static ThreadLocal<ProxyHolder<SqlSession>> threadHolder;
	
	static{
		try {
			String resource="mybatis.xml";
			Reader reader=Resources.getResourceAsReader(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			threadHolder = new ThreadLocal<ProxyHolder<SqlSession>>();
		} catch (IOException e) {
			logger.error("#IOException happened in initialising the SessionFactory", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public static SqlSession getSqlSession() throws ProxyException{
		SqlSession session = threadHolder.get().getHold();
		if(session == null){
			throw new ProxyException("SqlSession not init ! please use ProxyBiz to call SqlSession !");
		}
		return session;
	}
	
	public static ProxyHolder<SqlSession> openSqlSession(Method method){
		return openSqlSession(method, ExecutorType.BATCH);
	}
	
	public static ProxyHolder<SqlSession> openSqlSession(Method method, ExecutorType executorType){
		ProxyHolder<SqlSession> holder = threadHolder.get();
		if(holder == null){
			holder = new ProxyHolder<SqlSession>();
			SqlSession session = sqlSessionFactory.openSession(executorType, false);
			holder.setMethod(method);
			holder.setHold(session);
			threadHolder.set(holder);
		}
		return holder;
	}
	
	public static void closeSqlSession(SqlSession session){
		threadHolder.set(null);
		try {
			session.close();
		} catch (Exception e) {
			logger.error("closeSqlSession", e);
		}
	}
}
