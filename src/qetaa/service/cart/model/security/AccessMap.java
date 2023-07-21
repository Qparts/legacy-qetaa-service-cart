package qetaa.service.cart.model.security;

public class AccessMap {
	private String username;
	private String appSecret;
	private String code;
	private String language;

	public AccessMap() {

	}

	public AccessMap(String username, String appSecret, String code, String language) {
		this.username = username;
		this.appSecret = appSecret;
		this.code = code;
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
