package cn.ufunny.game.libs.cache.support.save;

import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.iface.ISaver;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by ryan on 2015/10/24.
 */
public abstract class AbstractSaver<V> implements ISaver<V> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSaveJob.class);


    protected static final int SAVE_BATCH_COUNT = 1000;

    protected volatile boolean finished = true;


    @Override
    public void save() {
        logger.debug("Start run with {}", this.getClass().getName());
        finished = false;
        try {
            List<ICacheObject<V>> changedDatas = findChangeData();
            if(changedDatas == null){
                return;
            }
            int allSize = changedDatas.size();
            int batchCount = allSize % SAVE_BATCH_COUNT == 0 ? allSize / SAVE_BATCH_COUNT : allSize / SAVE_BATCH_COUNT + 1;
            for (int i = 0; i < batchCount; i++) {
                boolean success = false;
                List<ICacheObject<V>> subList = null;
                try {
                    int startIndex = i * SAVE_BATCH_COUNT;
                    int endIndex = (i + 1) * SAVE_BATCH_COUNT < allSize ? (i + 1) * SAVE_BATCH_COUNT : allSize;
                    subList = changedDatas.subList(startIndex, endIndex);
                    for(ICacheObject<?> cacheObject : changedDatas){
                        cacheObject.settle();
                    }
                    List<V> toSave = findAllToSave(subList);
                    success = saveBatch(toSave);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if(!success){
                        if(subList != null){
                            logger.warn("save job not success..........................");
                            for(ICacheObject<?> cacheObject : subList){
                                cacheObject.unsettle();
                            }
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

    protected abstract boolean saveBatch(List<V> vs);


    protected List<V> findAllToSave(List<ICacheObject<V>> subList){
        List<V> vs = Lists.newArrayListWithCapacity(subList.size());
        for(ICacheObject<V> cacheObject : subList){
            V v = cacheObject.getData();
            if(v != null){
                vs.add(v);
            }
        }
        return vs;
    }


    @Override
    public boolean hasFinished() {
        return finished;
    }

}
