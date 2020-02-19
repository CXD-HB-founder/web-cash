/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: DelGoodsServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/12 11:09
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/12

 * @since 1.0.0

 */
@WebServlet("/delGoods")
public class DelGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String goodsId = req.getParameter("id");
        System.out.println(goodsId);
        int goodsIdInt = Integer.valueOf(goodsId);
        System.out.println(goodsId);
        boolean effect = this.delGoods(goodsIdInt);
        if(effect){
            System.out.println("下架成功!");
        }else{
            System.out.println("下架失败!");
        }
    }
    private boolean delGoods(int goodsIdInt){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            String sql = "delete from goods where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,goodsIdInt);
            return preparedStatement.executeUpdate()==1;

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return false;
    }
}