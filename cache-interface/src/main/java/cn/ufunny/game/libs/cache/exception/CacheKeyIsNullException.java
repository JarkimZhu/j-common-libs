package cn.ufunny.game.libs.cache.exception;

public class CacheKeyIsNullException extends RuntimeException {

	/**
	 * Auto generate
	 */
	private static final long serialVersionUID = -6015584171491191075L;

	public CacheKeyIsNullException() {
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}
