package com.lkk.everything.core.dao.impl;

import com.lkk.everything.core.dao.DataSourceFactory;
import com.lkk.everything.core.dao.FileIndexDao;
import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.FileType;
import com.lkk.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing){
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // 1.建立连接
            connection = dataSource.getConnection();
            // 2.准备sql语句
            String sql = "delete from file_index where PATH like '" + thing.getPath() +"%'";
            // 3.准备命令
            statement = connection.prepareStatement(sql);
            // 4.设置参数1234
            statement.setString(1,thing.getPath());
            // 5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // 1.建立连接
            connection = dataSource.getConnection();
            // 2.准备sql语句
            String sql = "insert into file_index(name, path, depth, file_type) values (?,?,?,?)";
            // 3.准备命令
            statement = connection.prepareStatement(sql);
            // 4.设置参数1234
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepth());
            statement.setString(4,thing.getFileType().name());

            // 5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Thing> things = new ArrayList<>();
        try {
            // 1.建立连接
            connection = dataSource.getConnection();
            // 2.准备sql语句
//            String sql = "select name, path, depth, file_type from file_index";


            // 做条件查询
            // name: like
            // fileType: =
            // limit：limit offset
            // orderbyAsc：order by
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select name, path, depth, file_type from file_index");
            // name匹配：前模糊，后模糊，前后模糊
            sqlBuilder.append(" where")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%'");
            // 文件类型
            if (condition.getFileType() != null){
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase())
                        .append("' ");
            }
            // limit order必选
            if (condition.getOrderByAsc() != null) {

                sqlBuilder.append(" order by depth ")
                        .append(condition.getOrderByAsc() ? "asc" : "desc");
            }
            if (condition.getLimit() != null) {
                sqlBuilder.append(" limit ")
                        .append(condition.getLimit())
                        .append(" offset 0 ");
            }


//            // orderby
//            sqlBuilder.append(" order by depth ")
//                    .append(condition.getOrederByAsc() ? "asc" : "desc");
//            // limit
//            sqlBuilder.append(" limit ")
//                    .append(condition.getLimit())
//                    .append(" offset 0");

//            System.out.println(sqlBuilder);

            // 3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            // 4.执行命令
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                // 吧数据库中的行记录变成java中的thing对象
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                thing.setFileType(FileType.lookupByName(resultSet.getString("file_type")));
                things.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(resultSet,statement,connection);
        }
        return things;

    }


//    解决大量代码重复问题：重构
    private void releaseResource(ResultSet resultSet, PreparedStatement statement,
                                 Connection connection){
        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



//    public static void main(String[] args) throws SQLException {
//
//
//        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
////        DataSourceFactory.initDatabase();
//
////        Thing thing = new Thing();
////        thing.setName("简历2.ppt");
////        thing.setPath("D:\\a\\test\\简历2.ppt");
////        thing.setDepth(2);
////        thing.setFileType(FileType.DOC);
////
////        fileIndexDao.insert(thing);
//
//
//        Condition condition = new Condition();
//        condition.setName("");
//        condition.setLimit(10000);
//        condition.setOrderByAsc(true);
//
//        List<Thing> things = fileIndexDao.search(condition);
//        int count = 0;
//        for(Thing t : things){
//            System.out.println(t);
//            count++;
//        }
//        System.out.println(count);
//    }
}
