package springcloud.configclient.dao2;

import org.springframework.data.jpa.repository.JpaRepository;

import springcloud.configclient.model.User;

public interface UserDao2 extends JpaRepository<User, Integer> {

}

