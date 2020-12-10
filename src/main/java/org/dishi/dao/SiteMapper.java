package org.dishi.dao;

import org.apache.ibatis.annotations.*;
import org.dishi.entity.Site;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface SiteMapper {
    @Insert("INSERT INTO favorite (site, name, uid) VALUES (#{site.site}, #{site.name}, #{site.uid})")
    int addSite(@Param("site") Site site);

    @Delete("delete from favorite where id = #{id}")
    int deleteSiteById(@Param("id") Integer id);

    @Select("select * from favorite where uid = #{uid}")
    List<Site> querySiteById(@Param("uid") Integer uid);
}
