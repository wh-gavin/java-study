package springboot.redis.lock.bean;

import java.io.Serializable;

public class UserVo implements Serializable { 

	private String email;

	private String name;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserVo(String email, String name) {
		this.email = email;
		this.name = name;
	}

	public UserVo() {
	}

	@Override
	public String toString() {
		return "UserVo{" + "email='" + email + '\'' + ", name='" + name + '\'' + '}';
	}
}