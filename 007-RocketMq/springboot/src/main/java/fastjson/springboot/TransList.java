package fastjson.springboot;

import java.util.List;

import com.alibaba.fastjson2.JSON;

public class TransList {
	public static void main(String[] args) {
		UserDao dao = new UserDao();
		List<User> list = dao.getAllUsers();
		String json = JSON.toJSONString(list);
		System.out.println(json);
	}
}
