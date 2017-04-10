package cn.ufunny.game.libs.cache.exception;

/**
 * @author JarkimZhu
 *
 */
public class NoCacheableException extends RuntimeException {

	/**
	 * Auto generate
	 */
	private static final long serialVersionUID = -6015584171491191075L;

	public NoCacheableException() {
	}
	
	public <T> NoCacheableException(Class<T> clazz) {
		super(clazz.getName() + " doses not have Cacheable capability");
	}
	

	public NoCacheableException(Object obj) {
		super(obj.getClass().getName() + " does not have Cacheable capability");
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}
