package com.lkk.everything.core.dao;

import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.Thing;

import java.sql.SQLException;
import java.util.List;


/**
 * 业务层访问数据库的CURD
 */
public interface FileIndexDao {
    /**
     * 插入
     * @param thing
     */
    void insert(Thing thing);

    /**
     * 删除thing
     * @param thing
     */
    void delete(Thing thing);

    /**
     * 检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);
}
