package org.mybatis.extend.page.dialect;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.extend.page.constant.DialectInfo;
import org.mybatis.extend.page.constant.PageConstant;
import org.mybatis.extend.page.param.Page;
import org.mybatis.extend.page.result.PageList;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Bob Jiang on 2017/3/6.
 */
public abstract class GenericDialect implements Dialect {

    public static Dialect getDialectInstance(Properties properties)
            throws IllegalAccessException, InstantiationException {
        String dialectName = properties.getProperty(PageConstant.DIALECT);
        Class dialectClass = DialectInfo.getClazzByName(dialectName);
        return (Dialect) dialectClass.newInstance();
    }

    public boolean canBePaged(MappedStatement ms, String sql, Object parameter) {
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (SqlCommandType.SELECT == sqlCommandType) {
            if (sql.trim().toUpperCase().endsWith("FOR UPDATE")) {
                throw new RuntimeException("for update语句不支持分页");
            }

            if (getPageParameter(parameter) == null) {
                return false;
            }
        } else {
            throw new RuntimeException("非select语句不支持分页");
        }
        return true;
    }

    public Page getPageParameter(Object parameter) {
        if (parameter instanceof Map) {
            Map map = (Map) parameter;
            if (map.containsKey(PageConstant.PAGE_KEY)) {
                Object pageObj = map.get(PageConstant.PAGE_KEY);

                if (pageObj != null && (pageObj instanceof Page)) {
                    return (Page) pageObj;
                }
            }
        }
        return null;
    }

    public BoundSql getCountSql(MappedStatement ms, BoundSql boundSql, Object parameter,
            Map<String, Object> additionalParameters) {
        String countSql = getCountSql(boundSql.getSql());

        BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
        for (String key : additionalParameters.keySet()) {
            countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        return countBoundSql;
    }

    public BoundSql getPageSql(MappedStatement ms, BoundSql boundSql, Object parameter,
            CacheKey pageKey, RowBounds rowBounds , Map<String, Object> additionalParameters, List<Map<String, Page.OrderType>> orders) {
        String pageSql = getPageSql(boundSql.getSql(), rowBounds, pageKey, orders);

        BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        return pageBoundSql;
    }

    public PageList buildPageList(List resultList, Page page, int totalRows) {
        System.out.println("resultList : " + resultList +" \r\n| page.getPage_num() : " + page.getPage_num() + " \r\n| page.getPage_size() : " + page.getPage_size() +" \r\ntotalRows : " + totalRows);
        return new PageList(resultList, page.getPage_num(), page.getPage_size(), totalRows);
    }

    public abstract String getPageSql(String sql, RowBounds rowBounds, CacheKey pageKey, List<Map<String, Page.OrderType>> orders);

    public abstract String getCountSql(String sql);

}
