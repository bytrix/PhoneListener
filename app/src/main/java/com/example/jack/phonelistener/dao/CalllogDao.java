package com.example.jack.phonelistener.dao;

import com.example.jack.phonelistener.bean.Calllog;

import java.util.List;

/**
 * Created by jack on 17-7-3.
 */

public interface CalllogDao {

    long insert(Calllog calllog);
    long delete(long id);
    long update(long id, Calllog calllog);
    Calllog query(long id);
    Calllog query(String phone);
    Calllog queryLatest();
    List<Calllog> query();
}
