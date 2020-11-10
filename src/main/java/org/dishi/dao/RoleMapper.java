package org.dishi.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dishi.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RoleMapper {

    @Insert("INSERT INTO roles_user (rid, uid) VALUES (#{role}, #{uid})")
    int addRole(@Param("role") Integer role, @Param("uid") Integer uid);

    @Select("SELECT r.* FROM roles r,roles_user ru WHERE r.id=ru.rid AND ru.uid=#{uid}")
    List<Role> getRolesByUid(@Param("uid") Integer uid);
}
