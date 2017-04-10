package cn.ufunny.game.libs.cache.ehcache.watcher;

import cn.ufunny.game.libs.cache.ehcache.support.EhCacheManager;
import cn.ufunny.game.libs.cache.iface.watcher.ICacheValueFilter;
import cn.ufunny.game.libs.cache.iface.watcher.ICacheWatcher;
import cn.ufunny.game.libs.utils.common.CommonUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JarkimZhu
 *
 */
public class EhCacheWatcher implements ICacheWatcher {

	private static final Logger logger = LoggerFactory.getLogger(EhCacheManager.class);
	
	@Resource
	private EhCacheManager ehCacheManager;

	@Override
	public String[] findAllCacheName() {
        return ehCacheManager.getCacheManager().getCacheNames();
	}

	@Override
	public void watch(String cacheName) {
		Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
		List<?> keys = cache.getKeys();
		if(!CommonUtils.isBlank(keys)) {
			for (Object obj : keys) {
				Element el = cache.get(obj);
				logger.trace("{} has {} : {}", cacheName, el.getObjectKey(), el.getObjectValue());
			}
		} else {
			logger.trace("{} has nothing !", cacheName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> void watch(String cacheName, ICacheValueFilter<K, V> filter) {
		if(filter == null) {
			watch(cacheName);
			return;
		}
		Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
		List<?> keys = cache.getKeys();
		if(!CommonUtils.isBlank(keys)) {
			for (Object key : keys) {
				Element el = cache.get(key);
				if(filter.accept((K)key, (V)el.getObjectValue())) {
					logger.trace("{} has {} : {}", cacheName, el.getObjectKey(), el.getObjectValue());
				}
			}
		} else {
			logger.trace("{} has nothing !", cacheName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> findAll(String cacheName) {
		Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
		List<?> keys = cache.getKeys();
		final HashMap<K, V> result = new HashMap<>(keys.size());
		if(!CommonUtils.isBlank(keys)) {
			for (Object obj : keys) {
				Element el = cache.get(obj);
				result.put((K)obj, (V)el.getObjectValue());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> findAll(String cacheName, ICacheValueFilter<K, V> filter) {
		if(filter == null) {
			return findAll(cacheName);
		}
		Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
		List<?> keys = cache.getKeys();
		final HashMap<K, V> result = new HashMap<>(keys.size());
		if(!CommonUtils.isBlank(keys)) {
			for (Object obj : keys) {
				Element el = cache.get(obj);
				if(filter.accept((K)obj, (V)el.getObjectValue())) {
					result.put((K)obj, (V)el.getObjectValue());					
				}
			}
		}
		return result;
	}

	public void setEhCacheManager(EhCacheManager ehCacheManager) {
		this.ehCacheManager = ehCacheManager;
	}
}
