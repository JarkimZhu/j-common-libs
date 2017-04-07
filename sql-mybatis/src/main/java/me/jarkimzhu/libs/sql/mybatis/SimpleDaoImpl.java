package me.jarkimzhu.libs.sql.mybatis;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import me.jarkimzhu.libs.pagination.Query;
import me.jarkimzhu.libs.sql.ISqlDao;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


/**
 * @author JarkimZhu
 *         Created on 2015/10/10.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class SimpleDaoImpl implements ISqlDao {

    protected SqlSessionFactory sessionFactory;

    @Override
    public <E> E get(String selectId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            E result = session.selectOne(selectId, params);
            session.commit(true);
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public <E> List<E> query(String selectId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            List<E> list = session.selectList(selectId, params);
            session.commit(true);
            return list;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public <E> List<E> query(String selectId, Object params, int start, int limit) {
        return null;
    }

    @Override
    public <E, T> Map<E, T> getMap(String selectId, String key, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            Map<E, T> map = session.selectMap(selectId, params, key);
            session.commit(true);
            return map;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public int add(String insertId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            int result = session.insert(insertId, params);
            session.commit(true);
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public int add(String insertId, Object params, boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            return session.insert(insertId, params);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public int update(String updateId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            int result = session.update(updateId, params);
            session.commit(true);
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public int update(String updateId, Object params, boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            return session.update(updateId, params);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public int delete(String deleteId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            int result = session.delete(deleteId, params);
            session.commit(true);
            return result;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public int delete(String deleteId, Object params, boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            return session.delete(deleteId, params);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public int save(String insertId, Object params) {
        return add(insertId, params);
    }

    @Override
    public int save(String insertId, Object params, boolean autoCommit) {
        return add(insertId, params, autoCommit);
    }

    @Resource
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <E> List<E> query(String selectId, Query params) {
        // TODO Auto-generated method stub
        return null;
    }


}
