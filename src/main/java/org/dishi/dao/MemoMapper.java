package org.dishi.dao;

import org.apache.ibatis.annotations.Mapper;
import org.dishi.entity.Memo;
import org.springframework.stereotype.Component;
import java.util.List;

@Mapper
@Component
public interface MemoMapper {
    int deleteByPrimaryKey(Integer mid);

    int insert(Memo record);

    int insertSelective(Memo record);

    Memo selectByPrimaryKey(Integer mid);

    int updateByPrimaryKeySelective(Memo record);

    int updateByPrimaryKeyWithBLOBs(Memo record);

    int updateByPrimaryKey(Memo record);

    List<Memo> queryAllMemo(Integer uid);
}