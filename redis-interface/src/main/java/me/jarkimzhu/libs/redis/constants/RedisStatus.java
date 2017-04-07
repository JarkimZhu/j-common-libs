package me.jarkimzhu.libs.redis.constants;

public enum RedisStatus {
	
	connection_is_null (-1000L , "链接错误");
	
	private Long code ;
	private String value;
	
	private RedisStatus(Long code , String value){
		this.code = code;
		this.value = value;
	}

	public Long code() {
		return code;
	}

	public String value() {
		return value;
	}
	
	

}
