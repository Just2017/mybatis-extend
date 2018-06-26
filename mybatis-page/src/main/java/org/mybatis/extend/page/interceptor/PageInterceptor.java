package org.mybatis.extend.page.interceptor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.extend.page.dialect.Dialect;
import org.mybatis.extend.page.cache.Cache;
import org.mybatis.extend.page.cache.SimpleCache;
import org.mybatis.extend.page.dialect.GenericDialect;
import org.mybatis.extend.page.param.Page;
import org.mybatis.extend.page.result.PageList;
import org.mybatis.extend.page.util.MSUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 核心拦截器
 *
 * Created by Bob Jiang on 2017/3/6.
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor {

    private Dialect dialect;
    private Field parametersField;
    private Cache<CacheKey, MappedStatement> msCache;

    public Object intercept(Invocation invocation) throws Throwable {
        initDialect();
        Object[] args = invocation.getArgs();

        MappedStatement ms          = (MappedStatement) args[0];
        Object parameter            = args[1];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor           = (Executor) invocation.getTarget();
        BoundSql boundSql           = ms.getBoundSql(parameter);

        if (dialect.canBePaged(ms, boundSql.getSql(), parameter)) {

            Map<String, Object> additionalParameters = (Map<String, Object>) parametersField.get(boundSql);
            int totalRows = queryCount(executor, ms, parameter, resultHandler, boundSql, additionalParameters);

            if (totalRows > 0) {
                return queryPage(executor, ms, parameter, resultHandler, boundSql, additionalParameters, totalRows);
            } else {
                Page page = dialect.getPageParameter(parameter);
                page.setPage_num(1);
                return dialect.buildPageList(new ArrayList(),new Page(), totalRows);
            }
        }
        return invocation.proceed();
    }

    private int queryCount(Executor executor, MappedStatement ms, Object parameter,
            ResultHandler resultHandler, BoundSql boundSql,
            Map<String, Object> additionalParameters) throws SQLException {

        CacheKey countKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, boundSql);
        countKey.update("_count");
        MappedStatement countMs = msCache.get(countKey);
        if (countMs == null) {
            countMs = MSUtils.newCountMappedStatement(ms);
            msCache.put(countKey, countMs);
        }

        BoundSql countSql = dialect.getCountSql(ms, boundSql, parameter, additionalParameters);

        List countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countSql);
        // 如果时进行分组了，返回分组的大小
        if(countResultList.size() == 0) countResultList.add(0);
        else if(countResultList.size() > 1) {
            int temp = countResultList.size();
            countResultList.clear();
            countResultList.add(temp);
        }
        Integer count = (Integer) countResultList.get(0);
        return count.intValue();
    }

    private PageList queryPage(Executor executor, MappedStatement ms, Object parameter,
            ResultHandler resultHandler, BoundSql boundSql,
            Map<String, Object> additionalParameters, int totalRows) throws SQLException {

        Page page = dialect.getPageParameter(parameter);
        RowBounds pageRowBounds = page.toRowBounds();
        List<Map<String, Page.OrderType>> orders = page.getOrders();

        CacheKey cacheKey = executor.createCacheKey(ms, parameter, pageRowBounds, boundSql);

        BoundSql pageBoundSql = dialect.getPageSql(ms, boundSql, parameter, cacheKey, pageRowBounds, additionalParameters, orders);
        List resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);

        return dialect.buildPageList(resultList, page, totalRows);
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        try {
            dialect = GenericDialect.getDialectInstance(properties);

            parametersField = BoundSql.class.getDeclaredField("additionalParameters");
            parametersField.setAccessible(true);

            msCache = new SimpleCache<CacheKey, MappedStatement>(properties, "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDialect() {
        if (dialect == null) {
            setProperties(new Properties());
        }
    }

}
