package com.example.jack.phonelistener.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jack.phonelistener.bean.Calllog;
import com.example.jack.phonelistener.dao.CalllogDao;
import com.example.jack.phonelistener.helper.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 17-7-3.
 */

public class CalllogDaoImpl implements CalllogDao {

    SQLiteHelper helper;
    SQLiteDatabase db;

    public CalllogDaoImpl(Context context) {
        helper = new SQLiteHelper(context);
    }

    @Override
    public long insert(Calllog calllog) {
        db = helper.getReadableDatabase();
//        Calllog calllogLatest = queryLatest();
//        if (calllog.getPhone().equals(calllogLatest.getPhone()) && (calllog.getCreateTime()-calllogLatest.getCreateTime()) < 1000) {
//            return 0;
//        }

        Calllog calllogLatest = new Calllog();
        Cursor cursor = db.query("t_calllog", null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            long id = cursor.getLong(0);
            String phone = cursor.getString(1);
            long createTime = cursor.getLong(2);
            long duration = cursor.getLong(3);
            String file = cursor.getString(4);
            calllogLatest = new Calllog(phone, createTime, duration, file);
            calllogLatest.setId(id);
        }

        if (calllog.getPhone().equals(calllogLatest.getPhone())
                && (calllog.getFile().equals(calllogLatest.getFile()))
                && (calllog.getCreateTime()-calllogLatest.getCreateTime()) < 2000) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put("phone", calllog.getPhone());
        values.put("createTime", calllog.getCreateTime());
        values.put("duration", calllog.getDuration());
        values.put("file", calllog.getFile());
        long rowId = db.insert("t_calllog", null, values);

        db.close();
        return rowId;
    }

    @Override
    public long delete(long id) {
        db = helper.getReadableDatabase();
        int row_id = db.delete("t_calllog", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return row_id;
    }

    @Override
    public long update(long id, Calllog calllog) {
        return 0;
    }

    @Override
    public Calllog query(long id) {
        Calllog calllog = null;
        db = helper.getReadableDatabase();

        Cursor cursor = db.query("t_calllog", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            String phone = cursor.getString(1);
            long createTime = cursor.getLong(2);
            long duration = cursor.getLong(3);
            String file = cursor.getString(4);
            calllog = new Calllog(phone, createTime, duration, file);
            calllog.setId(id);
        }

        db.close();
        return calllog;
    }

    @Override
    public Calllog query(String phone) {
        Calllog calllog = null;
        db = helper.getReadableDatabase();

        Cursor cursor = db.query("t_calllog", null, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
//            String phone = cursor.getString(1);
            long createTime = cursor.getLong(2);
            long duration = cursor.getLong(3);
            String file = cursor.getString(4);
            calllog = new Calllog(phone, createTime, duration, file);
        }

        db.close();
        return calllog;
    }

    @Override
    public Calllog queryLatest() {
        Calllog calllog = null;
        db = helper.getReadableDatabase();

        Cursor cursor = db.query("t_calllog", null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            long id = cursor.getLong(0);
            String phone = cursor.getString(1);
            long createTime = cursor.getLong(2);
            long duration = cursor.getLong(3);
            String file = cursor.getString(4);
            calllog = new Calllog(phone, createTime, duration, file);
            calllog.setId(id);
        }

        db.close();
        return calllog;
    }

    @Override
    public List<Calllog> query() {
        db = helper.getReadableDatabase();
        List<Calllog> calllogList = new ArrayList<>();

        Cursor cursor = db.query("t_calllog", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String phone = cursor.getString(1);
            long createTime = cursor.getLong(2);
            long duration = cursor.getLong(3);
            String file = cursor.getString(4);
            Calllog calllog = new Calllog(phone, createTime, duration, file);
            calllog.setId(id);
            calllogList.add(calllog);
        }

        db.close();
        return calllogList;
    }
}
