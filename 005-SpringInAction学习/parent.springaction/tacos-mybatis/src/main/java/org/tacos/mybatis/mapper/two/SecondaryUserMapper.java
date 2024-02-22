package org.tacos.mybatis.mapper.two;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.tacos.mybatis.pojo.User;

@Component
@Mapper
public interface SecondaryUserMapper {
    List<User> findAll();
}
