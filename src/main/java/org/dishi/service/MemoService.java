package org.dishi.service;

import org.dishi.entity.Memo;

import java.util.List;

public interface MemoService {
    List<Memo> queryAllMemo(Integer uid);

    Integer deleteMemo(Integer mid);

    Integer updateMemo(Memo memo);

    Integer addMemo(Memo memo);

    Memo select(Integer mid);
}
