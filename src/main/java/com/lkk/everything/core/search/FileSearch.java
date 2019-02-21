package com.lkk.everything.core.search;

import com.lkk.everything.core.dao.DataSourceFactory;
import com.lkk.everything.core.dao.impl.FileIndexDaoImpl;
import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.Thing;
import com.lkk.everything.core.search.impl.FileSearchImpl;

import java.util.List;


public interface FileSearch {
    /**
     * 根据condition条件进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> serch(Condition condition);

//    public static void main(String[] args) {
//        FileSearch fileSearch = new FileSearchImpl(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
//        List<Thing> list = fileSearch.serch(new Condition());
//        System.out.println(list);
//    }
}
