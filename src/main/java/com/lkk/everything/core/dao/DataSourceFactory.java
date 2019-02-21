package com.lkk.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;


import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {
    private static volatile DruidDataSource dataSource;
    private DataSourceFactory(){

    }
    public static DataSource dataSource(){
        if (dataSource == null){
            synchronized (DataSourceFactory.class){
                if (dataSource == null){
                    // 实例化
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
//                    url,username, password
                    // 采用的是h2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    // 获取当前工程路径
                    String workDir = System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "everything");
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() {
        // 获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        // 获取sql语句
        try(InputStream in =
                    DataSourceFactory.class.getClassLoader().getResourceAsStream("everything.sql")){
            if (in == null){
                throw new RuntimeException("没有读到初始化的数据");
            }

            StringBuilder sqlString = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line = null;
                while ((line = reader.readLine()) != null){
                    if (line.startsWith("--")){
                        sqlString.append(line);
                    }
                }
            }

            // 得到了sql，获取数据库连接和名称执行sql：
            String sql = sqlString.toString();
            // JDBC步骤：
            // 获取数据库的连接
            Connection connection = dataSource.getConnection();
            // 创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            // 执行sql语句
            statement.execute();
            // 关闭
            connection.close();
            statement.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        DataSourceFactory.initDatabase();
//    }
}
