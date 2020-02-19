/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: buyGoodsServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/13 16:02
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import common.OrderStatus;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/13

 * @since 1.0.0

 */
@WebServlet("/buyGoodsServlet")
public class buyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
        resp.sendRedirect("goodsbrowse.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        Order order = (Order)session.getAttribute("order");
        List<Goods> goodsList = (List<Goods>)session.getAttribute("goodsList");
        //提交订单
        order.setOrder_status(OrderStatus.OK);
        boolean effect = this.commitOrder(order);
        //当插入成功时遍历货物，修改库存信息;
        if(effect){
            for(Goods goods : goodsList){
                boolean isUpdate = updateAfterBuy(goods,goods.getBuyGoodsNum());
                if(isUpdate){
                    System.out.println("更新库存成功!");
                }else {
                    System.out.println("更新库存失败!");
                }
            }
        }
    }
    //更新的其实就是goods表
    private boolean updateAfterBuy(Goods goods, Integer goodsBuyNum) {
        Connection connection = null;
        PreparedStatement  preparedStatement = null;
        boolean effect = false;
        try{
            String sql = "update goods set stock=? where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            if(goods.getStock() >= goodsBuyNum) {
                preparedStatement.setInt(1, goods.getStock() - goodsBuyNum);
            }else {
                preparedStatement.setInt(1, 0);
            }
            preparedStatement.setInt(2, goods.getId());
            if(preparedStatement.executeUpdate()==1){
                effect = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return effect;
    }

    private boolean commitOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            String insertOrder = "insert into `order`(id,account_id,create_time," +
                    "finish_time,actual_amount,total_money,order_status," +
                    "account_name) values (?,?,now(),now(),?,?,?,?)";
            String insertOrderItem = "insert into order_item(order_id, goods_id, goods_name, " +
                    "goods_introduce, goods_num, goods_unit, " +
                    "goods_price, goods_discount) values (?,?,?,?,?,?,?,?)";
            connection = DBUtil.getConnection(false);
            preparedStatement = connection.prepareStatement(insertOrder);
            preparedStatement.setString(1, order.getId());
            preparedStatement.setInt(2, order.getAccount_id());
            preparedStatement.setInt(3, order.getActualAmountInt());
            preparedStatement.setInt(4, order.getTotalMoneyInt());
            preparedStatement.setInt(5, order.getOrder_status().getFlg());
            preparedStatement.setString(6, order.getAccount_name());

            if(preparedStatement.executeUpdate() == 0){
                throw new RuntimeException("插入订单失败");
            }
            preparedStatement = connection.prepareStatement(insertOrderItem);

            for (OrderItem orderItem : order.orderItems){
                preparedStatement.setString(1, order.getId());
                preparedStatement.setInt(2, orderItem.getGoodsId());
                preparedStatement.setString(3, orderItem.getGoodsName());
                preparedStatement.setString(4, orderItem.getGoodsIntroduce());
                preparedStatement.setInt(5, orderItem.getGoodsNum());
                preparedStatement.setString(6, orderItem.getGoodsUnit());
                preparedStatement.setInt(7, orderItem.getIntGoodsPrice());
                preparedStatement.setInt(8, orderItem.getGoodsDiscount());

                //将每一项缓存
                preparedStatement.addBatch();
            }
            int[] effects = preparedStatement.executeBatch();
            for(int n : effects){
                if(n == 0){
                    throw new RuntimeException("插入订单项失败");
                }
            }
            //手动提交
            connection.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if(connection != null){
                try {
                    connection.rollback();
                }catch (SQLException ex){
                    e.printStackTrace();
                }
            }
            return false;
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return true;
    }
}