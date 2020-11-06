package org.dishi.service.impl;

import org.dishi.dao.MemoMapper;
import org.dishi.entity.Memo;
import org.dishi.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemoServiceImpl implements MemoService {

    @Autowired
    MemoMapper memoMapper;

    @Override
    public List<Memo> queryAllMemo(Integer uid) {
        return memoMapper.queryAllMemo(uid);
    }

    @Override
    @Transactional
    public Integer deleteMemo(Integer mid) {
        return memoMapper.deleteByPrimaryKey(mid);
    }

    @Override
    @Transactional
    public Integer updateMemo(Memo memo) {
        return memoMapper.updateByPrimaryKeySelective(memo);
    }

    @Override
    @Transactional
    public Integer addMemo(Memo memo) {
        return memoMapper.insert(memo);
    }
}
