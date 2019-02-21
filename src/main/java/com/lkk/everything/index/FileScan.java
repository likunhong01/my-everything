package com.lkk.everything.index;

import com.lkk.everything.core.dao.DataSourceFactory;
import com.lkk.everything.core.dao.impl.FileIndexDaoImpl;
import com.lkk.everything.core.interceptor.FileInterceptor;
import com.lkk.everything.core.interceptor.impl.FileIndexInterceptor;
import com.lkk.everything.core.interceptor.impl.FilePrintInterceptor;
import com.lkk.everything.core.model.Thing;
import com.lkk.everything.index.impl.FileScanImpl;

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

    public static void main(String[] args) {
        FileScanImpl scan = new FileScanImpl();
        FileInterceptor interceptor = new FilePrintInterceptor();
        scan.interceptor(interceptor);

        FileIndexInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));

        scan.interceptor(fileIndexInterceptor);

        scan.index("C:\\文件");
    }
}
