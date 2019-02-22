package com.lkk.everything.core.index.impl;

import com.lkk.everything.config.EverythingConfig;
import com.lkk.everything.core.interceptor.FileInterceptor;
import com.lkk.everything.core.index.FileScan;

import java.io.File;
import java.util.LinkedList;

public class FileScanImpl implements FileScan {

    private EverythingConfig config = EverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
//        List<File> fileList = new ArrayList<>();

        if (file.isFile()){
            if (config.getExcludePath().contains(file.getParent())){
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)){
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null){
                    for (File f : files){
                        index(f.getAbsolutePath());
                    }
                }
            }
        }

        // 文件变成thing 然后写入
//        for (File f:fileList){
//            // 转化成thing 然后写入数据库
//        }
        for (FileInterceptor interceptor: this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor fileInterceptor) {
        this.interceptors.add(fileInterceptor);
    }





//    public static void main(String[] args) {
//        FileScanImpl scan = new FileScanImpl();
//        FileInterceptor interceptor = new FilePrintInterceptor();
//        scan.addFileInterceptor(interceptor);
//
//        FileIndexInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
//
//        scan.addFileInterceptor(fileIndexInterceptor);
//
//        scan.index("C:\\文件");
//    }
}
