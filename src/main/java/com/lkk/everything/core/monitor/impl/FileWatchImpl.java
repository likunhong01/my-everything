package com.lkk.everything.core.monitor.impl;

import com.lkk.everything.core.common.FileConverThing;
import com.lkk.everything.core.common.HandlePath;
import com.lkk.everything.core.dao.FileIndexDao;
import com.lkk.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

public class FileWatchImpl implements FileWatch,FileAlterationListener {
    // 要对数据库进行处理
    private FileIndexDao fileIndexDao;

    // 要对monitor处理
    private FileAlterationMonitor monitor;


    public FileWatchImpl(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
        this.monitor  = new FileAlterationMonitor(100);
    }


    @Override
    public void onStart(FileAlterationObserver observer) {
//        observer.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {
        // 文件删除
        System.out.println("onFileCreate" + file);
        this.fileIndexDao.delete(FileConverThing.convert(file));
    }

    @Override
    public void onFileCreate(File file) {
        // 文件创建
        System.out.println("onFileCreate" + file);
        this.fileIndexDao.insert(FileConverThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver observer) {
//        observer.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        // 监控的是includePath
        for (String path : handlePath.getIncludePath()){
            FileAlterationObserver observer = new FileAlterationObserver(path, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String currentPath = pathname.getName();
                    for (String excludePath : handlePath.getExcludePath()){
                        if (excludePath.startsWith(currentPath)){
                            return false;
                        }
                    }
                    return true;
                }
            });

            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
//        this.monitor.addObserver();
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
