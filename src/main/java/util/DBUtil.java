/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: DBUtil
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/11 10:31
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/11

 * @since 1.0.0

 */

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/cash";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "root";

    private static volatile DataSource DATASOURCE;

    private static DataSource getDATASOURCE(){
        if(DATASOURCE == null){
            synchronized (DBUtil.class){
                if(DATASOURCE == null){
                    DATASOURCE = new MysqlDataSource();
                    ((MysqlDataSource)DATASOURCE).setURL(URL);
                    ((MysqlDataSource)DATASOURCE).setUser(USERNAME);
                    ((MysqlDataSource)DATASOURCE).setPassword(PASSWORD);
                }
            }
        }
        return DATASOURCE;
    }

    public static Connection getConnection(boolean autoCommit){
        try {
            Connection connection = getDATASOURCE().getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接失败");
        }
    }

    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            if(resultSet != null) {
                resultSet.close();
            }
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库释放资源异常");
        }
    }

    public static void main(String[] args) {
        double a = 1.01;
        int b = (int)a;
        System.out.println(b);
    }
}