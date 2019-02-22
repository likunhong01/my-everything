package com.lkk.everything.core.search.impl;

import com.lkk.everything.core.dao.FileIndexDao;
import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.Thing;
import com.lkk.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;


public class FileSearchImpl implements FileSearch {
    // 需要一个数据源
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public List<Thing> search(Condition condition) {
        if (condition == null){
            return new ArrayList<>();
        }
        return this.fileIndexDao.search(condition);
    }
}
