package com.lkk.everything.core.interceptor.impl;

import com.lkk.everything.core.common.FileConverThing;
import com.lkk.everything.core.dao.FileIndexDao;
import com.lkk.everything.core.interceptor.FileInterceptor;
import com.lkk.everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor{

    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConverThing.convert(file);
//        System.out.println(thing);
        fileIndexDao.insert(thing); // 插入数据库
    }
}
