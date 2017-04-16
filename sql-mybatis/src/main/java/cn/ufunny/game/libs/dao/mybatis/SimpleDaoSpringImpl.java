package cn.ufunny.game.libs.dao.mybatis;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import cn.ufunny.game.libs.dao.iface.IDao;

public class SimpleDaoSpringImpl implements IDao {

	protected SqlSessionFactory sessionFactory;
	
	@Override
	public <E> E get(String selectId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.selectOne(selectId, params);
	}

	@Override
	public <E> E get(String selectId, Object params, boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.selectOne(selectId, params);
	}

	@Override
	public <E> List<E> findList(String selectId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.selectList(selectId, params);
	}

	@Override
	public <E> List<E> findList(String selectId, Object params,boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.selectList(selectId, params);
	}

	@Override
	public <E, T> Map<E, T> getMap(String selectId, String key, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.selectMap(selectId, params, key);
	}

	@Override
	public <E, T> Map<E, T> getMap(String selectId, String key, Object params,boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.selectMap(selectId, params, key);
	}

	@Override
	public int add(String insertId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.insert(insertId, params);
	}

	@Override
	public int add(String insertId, Object params, boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.insert(insertId, params);
	}


	@Override
	public int update(String updateId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.update(updateId, params);
	}

	@Override
	public int update(String updateId, Object params,boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.update(updateId, params);
	}

	@Override
	public int delete(String deleteId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.delete(deleteId, params);
	}

	@Override
	public int delete(String deleteId, Object params,boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.delete(deleteId, params);
	}
	
	@Override
	public int save(String insertId, Object params) {
		SqlSession session = sessionFactory.openSession();
		return session.insert(insertId, params);
	}

	@Override
	public int save(String insertId, Object params,boolean autoCommit) {
		SqlSession session = sessionFactory.openSession(autoCommit);
		return session.insert(insertId, params);
	}

	@Resource
	public void setSessionFactory(SqlSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
