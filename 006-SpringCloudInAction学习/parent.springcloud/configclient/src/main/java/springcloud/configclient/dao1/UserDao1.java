package springcloud.configclient.dao1;

import org.springframework.data.jpa.repository.JpaRepository;

import springcloud.configclient.model.User;

public interface UserDao1 extends JpaRepository<User, Integer> {

}

