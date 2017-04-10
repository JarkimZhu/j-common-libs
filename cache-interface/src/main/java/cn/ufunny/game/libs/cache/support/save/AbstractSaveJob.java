package cn.ufunny.game.libs.cache.support.save;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.iface.ISaveJob;
import cn.ufunny.game.libs.dao.iface.IDao;

public abstract class AbstractSaveJob<E> implements ISaveJob<E> {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractSaveJob.class);
	
	protected static final int SAVE_BATCH_COUNT = 1000;
	
	@Resource
	protected IDao dao;
	protected volatile boolean finished = true;
	protected List<ICacheObject<?>> changedDatas;
	
	@Override
	public void run() {
		logger.debug("Start run with {}", this.getClass().getName());
		finished = false;
		try {
			int allSize = changedDatas.size();
			int batchCount = allSize % SAVE_BATCH_COUNT == 0 ? allSize / SAVE_BATCH_COUNT : allSize / SAVE_BATCH_COUNT + 1;
			for (int i = 0; i < batchCount; i++) {
				boolean success = false;
				try {
					int startIndex = i * SAVE_BATCH_COUNT;
					int endIndex = (i + 1) * SAVE_BATCH_COUNT < allSize ? (i + 1) * SAVE_BATCH_COUNT : allSize;
					List<ICacheObject<?>> subList = changedDatas.subList(startIndex, endIndex);
					for(ICacheObject<?> cacheObject : changedDatas){
						cacheObject.settle();
					}
					List<E> toSave = findAllToSave(subList);
					saveBatch(toSave);
					success = true;
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				} finally {
					if(!success){
						for(ICacheObject<?> cacheObject : changedDatas){
							cacheObject.unsettle();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			logger.debug("End run with {}", this.getClass().getName());
			finished = true;
		}
	}
	
	protected abstract void saveBatch(List<E> subList);
	
	@SuppressWarnings("unchecked")
	protected List<E> findAllToSave(List<ICacheObject<?>> subList) {
		ArrayList<E> toSave = new ArrayList<>(subList.size());
		for (ICacheObject<?> iCacheObject : subList) {
			Object object = iCacheObject.getData();
			if(object != null){
				toSave.add((E)object);
			}
		}
		return toSave;
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public <V> void setChangedDatas(List<ICacheObject<V>> changedDatas) {
		this.changedDatas = new ArrayList<>(changedDatas.size());
		this.changedDatas.addAll(changedDatas);
	}
	
	public void setDao(IDao dao) {
		this.dao = dao;
	}
}
