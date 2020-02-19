/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: RegisterServlet
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
package servlet;

import org.apache.commons.codec.digest.DigestUtils;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/11

 * @since 1.0.0

 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");


        Connection collection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            String sql = "insert into account(username,password) values(?,?)";
            collection = DBUtil.getConnection(true);
            preparedStatement = collection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, username);
            //preparedStatement.setString(2, password);
            preparedStatement.setString(2, DigestUtils.md5Hex(password));
            preparedStatement.executeUpdate();
            resp.sendRedirect("login.html");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(collection, preparedStatement, resultSet);
        }
    }
}