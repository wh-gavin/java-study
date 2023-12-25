package org.tacos.multi.db2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.tacos.multi.entity.User;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/3
 * @Description Description
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>,JpaSpecificationExecutor<User> {
}