package cn.ufunny.game.libs.dao.mybatis.proxy;

public class ProxyException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 350251900878633900L;
	
	public ProxyException(){
		super();
	}
	
	public ProxyException(String message){
		super(message);
	}
	
	public ProxyException(Throwable t){
		super(t);
	}
	
	public ProxyException(String message, Throwable t){
		super(message, t);
	}
}
