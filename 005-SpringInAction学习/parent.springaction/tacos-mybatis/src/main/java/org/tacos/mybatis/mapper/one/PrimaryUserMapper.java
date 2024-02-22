package org.tacos.mybatis.mapper.one;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.tacos.mybatis.pojo.User;

@Component
@Mapper
public interface PrimaryUserMapper {
    List<User> findAll();
}
