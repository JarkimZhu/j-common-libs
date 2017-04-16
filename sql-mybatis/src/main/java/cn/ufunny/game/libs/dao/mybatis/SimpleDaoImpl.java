package cn.ufunny.game.libs.dao.mybatis;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import cn.ufunny.game.libs.dao.iface.IDao;

/**
 * Created by ryan on 2015/10/10.
 */
public class SimpleDaoImpl implements IDao {

    protected SqlSessionFactory sessionFactory;

    @Override
    public <E> E get(String selectId, Object params) {
        SqlSession session = null;
        try{
            session = sessionFactory.openSession();
            E result = session.selectOne(selectId, params);
            session.commit(true);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }
    }

    @Override
    public <E> E get(String selectId, Object params, boolean autoCommit) {
        SqlSession session = null;
        try{
            session = sessionFactory.openSession(autoCommit);
            E result = session.selectOne(selectId, params);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public <E> List<E> findList(String selectId, Object params) {
        SqlSession session = null;
        try{
            session = sessionFactory.openSession();
            List<E> list= session.selectList(selectId, params);
            session.commit(true);
            return list;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public <E> List<E> findList(String selectId, Object params,boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            List<E> list= session.selectList(selectId, params);
            return list;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public <E, T> Map<E, T> getMap(String selectId, String key, Object params) {
        SqlSession session = null;
        try{
            session = sessionFactory.openSession();
            Map<E,T> map = session.selectMap(selectId, params, key);
            session.commit(true);
            return map;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public <E, T> Map<E, T> getMap(String selectId, String key, Object params,boolean autoCommit) {
        SqlSession session = null;
        try{
            session = sessionFactory.openSession(autoCommit);
            Map<E,T> map = session.selectMap(selectId, params, key);
            return map;
        }finally {
            if(session != null){
                session.close();
                session = null;
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
            return  result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public int add(String insertId, Object params, boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            int result = session.insert(insertId, params);
            return  result;
        }finally {
            if(session != null){
                session.close();
                session = null;
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
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public int update(String updateId, Object params,boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            int result = session.update(updateId, params);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
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
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }
    }

    @Override
    public int delete(String deleteId, Object params,boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            int result = session.delete(deleteId, params);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }

    }

    @Override
    public int save(String insertId, Object params) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession();
            int result = session.insert(insertId, params);
            session.commit(true);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }
    }

    @Override
    public int save(String insertId, Object params,boolean autoCommit) {
        SqlSession session = null;
        try {
            session = sessionFactory.openSession(autoCommit);
            int result = session.insert(insertId, params);
            return result;
        }finally {
            if(session != null){
                session.close();
                session = null;
            }
        }
    }

    @Resource
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
