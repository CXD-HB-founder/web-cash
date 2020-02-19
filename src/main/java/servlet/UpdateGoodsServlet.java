/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: UpdateGoodsServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/12 11:35
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/12

 * @since 1.0.0

 */
@WebServlet("/updategoods")
public class UpdateGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html: charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        System.out.println("进入dopost");
        String goodsStringId = req.getParameter("goodsID");
        int goodsId = Integer.valueOf(goodsStringId);
        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        int stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");

        String price = req.getParameter("price");
        double doublePrice = Double.valueOf(price);
        int realPrice = new Double(doublePrice * 100).intValue();

        int discount = Integer.parseInt(req.getParameter("discount"));

        Goods goods = this.getGoods(goodsId);
        System.out.println("goodsif判断");
        if(goods == null){
            System.out.println("没有该商品!");
            resp.sendRedirect("index.html");
        }else {
            goods.setName(name);
            goods.setIntroduce(introduce);
            goods.setStock(stock);
            goods.setUnit(unit);
            goods.setPrice(Integer.valueOf(price));
            goods.setDiscount(discount);

            //把查询的商品进行更新，随后对数据库操作更新goods
            boolean effect = this.modifyGoods(goods);
            if(effect){
                System.out.println("更新成功!");
                resp.sendRedirect("goodsbrowse.html");
            }else {
                System.out.println("更新失败!");
                resp.sendRedirect("index.html");
            }
        }
    }

    private boolean modifyGoods(Goods goods) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        boolean effect = false;
        try{
            String sql = " update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id=? ";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            System.out.println(goods);
            preparedStatement.setString(1, goods.getName());
            preparedStatement.setString(2, goods.getIntroduce());
            preparedStatement.setInt(3, goods.getStock());
            preparedStatement.setString(4, goods.getUnit());
            preparedStatement.setInt(5,goods.getIntPrice());
            preparedStatement.setInt(6,goods.getDiscount());
            preparedStatement.setInt(7,goods.getId());
            System.out.println("开始查询");
            System.out.println(preparedStatement.executeUpdate()+"查询结果");
            effect = (preparedStatement.executeUpdate() == 1);
            System.out.println(effect);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection, preparedStatement, null);
        }
        return effect;
    }

    private Goods getGoods(int goodsId) {
        Goods goods = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            String sql = "select * from goods where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, goodsId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                System.out.println("查到了");
                goods = this.extractGoods(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return goods;
    }

    private Goods extractGoods(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        System.out.println("返回删除商品信息");
        return goods;
    }
}