package org.mybatis.extend.page.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bob Jiang on 2017/3/10.
 */
public class ParseCountSql {

    public static String parseSql(String sql) {
        sql = compress(sql);
        if (sql.indexOf("select distinct") == -1) {
            int index = getFirstFormIndex(sql);
            int orderIndex = sql.lastIndexOf("order by");
            orderIndex = orderIndex != -1 ? orderIndex : sql.length();

            System.out.println("\r\n come from mybatis-extend , and sql = " + sql);
            String selectBody = sql.substring(index, orderIndex);
            return "select count(1)" + selectBody;
        } else {
            System.out.println("\r\n come from mybatis-extend , sql = " + sql );
            return StringTools.append("select count(1) from (", sql, ") tmp_count");
        }
    }

    private static String compress(String sql) {
        return sql.toLowerCase().replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    private static int getFirstFormIndex(String sql) {
        String regex = "\\s+FROM\\s+";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            return matcher.start(0);
        }
        return 0;
    }

}
