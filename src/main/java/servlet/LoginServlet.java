/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: LoginServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/11 11:32
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import entity.Account;
import org.apache.commons.codec.digest.DigestUtils;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
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
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select id,username,password from account where username=?" +
                    " and password=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            //preparedStatement.setString(2,password);
            preparedStatement.setString(2, DigestUtils.md5Hex(password));
            resultSet = preparedStatement.executeQuery();
//            try {
//                System.out.println(resultSet.getString("password")6
//                e.printStackTrace();
//            }
            Account account = new Account();
            if(resultSet.next()){
                account.setId(resultSet.getInt("id"));
                account.setUsername(resultSet.getString("username"));
                account.setPassword(resultSet.getString("password"));
            }

            Writer writer = resp.getWriter();

            if(account.getId() == null){
                //System.out.println("用户名或密码错误");
                writer.write("<h2>用户名或密码错误: "+username+"</h2>");
            }else {
                HttpSession session = req.getSession();
                session.setAttribute("user", account);
                resp.sendRedirect("index.html");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

        }
    }
}