package com.yang.dao;

import com.yang.pojo.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MiaoshaUserDao {
    /**
     * 通过Id从数据库中拿到用户信息
     * @param id 用户Id
     * @return 返回一个用户对象的详细信息
     */
    @Select("select * from miaosha_user where id = #{id}")
    MiaoshaUser getById(@Param("id") Long id);
}
