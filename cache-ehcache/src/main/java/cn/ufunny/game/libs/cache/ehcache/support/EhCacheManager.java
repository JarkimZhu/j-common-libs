package cn.ufunny.game.libs.cache.ehcache.support;

import net.sf.ehcache.CacheManager;

public class EhCacheManager {
	
	
	private CacheManager cacheManager;
	
	
	public EhCacheManager(){
	}
	
	public EhCacheManager(String filePath){
		cacheManager = CacheManager.create(this.getClass().getClassLoader().getResource(filePath));
	}


	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void shutdown() {
		cacheManager.shutdown();
	}
}
