package org.tacos.mybatis.mapper1;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.tacos.multi.entity.User;


@Component("userMapper1")
@Mapper
public interface UserMapper {

    @Select("select * from user order by id desc limit 1 ")
    public User getNewstOne();


}