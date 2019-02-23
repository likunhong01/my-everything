package com.lkk.everything.core;

import com.lkk.everything.config.EverythingConfig;
import com.lkk.everything.core.common.HandlePath;
import com.lkk.everything.core.dao.DataSourceFactory;
import com.lkk.everything.core.dao.FileIndexDao;
import com.lkk.everything.core.dao.impl.FileIndexDaoImpl;
import com.lkk.everything.core.interceptor.ThingInterceptor;
import com.lkk.everything.core.interceptor.impl.FileIndexInterceptor;
import com.lkk.everything.core.interceptor.impl.ThingClearInterceptor;
import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.Thing;
import com.lkk.everything.core.monitor.FileWatch;
import com.lkk.everything.core.monitor.impl.FileWatchImpl;
import com.lkk.everything.core.search.FileSearch;
import com.lkk.everything.core.search.impl.FileSearchImpl;
import com.lkk.everything.core.index.FileScan;
import com.lkk.everything.core.index.impl.FileScanImpl;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EverythingManager {

    private static volatile EverythingManager manager;

    private FileSearch fileSearch;

    private FileScan fileScan;

    private ExecutorService executorService;

    private ThingClearInterceptor thingClearInterceptor;

    private Thread backgroundClearThread;

    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    /**
     * 文件监控
     */
    private FileWatch fileWatch;




    private EverythingManager(){
        this.initComponent();
    }

    private void initComponent(){
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

//        initOrResetDatabase();
        // 检查数据库
        checkDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
        // 发布代码的时候是不需要的
//        this.fileScan = new FileScanImpl();
//        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));


        /**
         * 处理删除文件
         */
//        thing的拦截器
        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread Thing Clear");
        this.backgroundClearThread.setDaemon(true);


        // 文件监控对象
        this.fileWatch = new FileWatchImpl(fileIndexDao);
    }


    /**
     * 有bug，要在第一次进入初始化
     */
    private void checkDatabase() {
        DataSourceFactory.initDatabase();
//        String filename = EverythingConfig.getInstance().getH2IndexPath() + ".mv.db";
//        File dbFile = new File(filename);
//        if (!dbFile.exists()){
//            DataSourceFactory.initDatabase();
//        }


    }

    public EverythingManager(FileSearch fileSearch, FileScan fileScan) {
        this.fileSearch = fileSearch;
        this.fileScan = fileScan;
    }

    public static EverythingManager getInstance() {
        if (manager == null) {
            synchronized(EverythingManager.class) {
                if (manager == null) {
                    manager = new EverythingManager();
                }
            }
        }
        return manager;
    }


    /**
     * 检索
     */
    public List<Thing> search(Condition condition){
        //NOTICE 扩展
        // 用string流的方式，如果文件不存在，剔除掉,jdk1.8以上
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            if ( f.exists()){
                return true;
            }else{
                // 删除，用生产者消费者模型，放到一个队列，让另一个去删除
                thingClearInterceptor.apply(thing);
                return false;
            }
        }).collect(Collectors.toList());
    }


    /**
     * 索引
     */
    public void buildIndex(){
        // 初始化数据库
        DataSourceFactory.initDatabase();

        Set<String> directories = EverythingConfig.getInstance().getIncludePath();
        if (this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size(),
                    new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(3);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                            return thread;
                        }
                    });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());



        System.out.println("Build index start...");

        for (String path: directories){
//            this.fileScan.index(path);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    EverythingManager.this.fileScan.index(path);
//                }
//            }).start();
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    EverythingManager.this.fileScan.index(path);
                    countDownLatch.countDown();//减1
                }
            });

            /**
             * 阻塞，直到任务完成，值0
             */
            try {
                countDownLatch.await();//等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Build index complete");
        }
    }


    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread(){
//        if (this.backgroundClearThreadStatus.get()){
//            System.out.println("不能重复启动清理线程");
//        } else {
//            this.backgroundClearThread.start();
//            this.backgroundClearThreadStatus.set(true);
//        }

        if (this.backgroundClearThreadStatus.compareAndSet(false,true)){
            this.backgroundClearThread.start();
        } else {
            System.out.println("不能重复启动清理线程");
        }

    }

    /**
     * 启动文件系统监听
     */
    public void startFileSystemMonitor(){
        EverythingConfig config = EverythingConfig.getInstance();
        HandlePath handlePath = new HandlePath();
        handlePath.setIncludePath(config.getIncludePath());
        handlePath.setExcludePath(config.getExcludePath());

        this.fileWatch.monitor(handlePath);

        new Thread(new Runnable() {
            @Override
            public void run() {
                fileWatch.start();
            }
        });
    }
}
