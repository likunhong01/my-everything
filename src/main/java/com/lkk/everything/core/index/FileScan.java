package com.lkk.everything.core.index;

import com.lkk.everything.core.interceptor.FileInterceptor;

public interface FileScan {
    /**
     * 遍历path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param fileInterceptor
     */
    void interceptor(FileInterceptor fileInterceptor);

//    public static void main(String[] args) {
//        FileScanImpl scan = new FileScanImpl();
//        FileInterceptor interceptor = new FilePrintInterceptor();
//        scan.interceptor(interceptor);
//
//        FileIndexInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
//
//        scan.interceptor(fileIndexInterceptor);
//
//        scan.index("C:\\文件");
//    }
}
