/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: ReadyBuyServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/13 9:36
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import common.OrderStatus;
import entity.Account;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/13

 * @since 1.0.0

 */
@WebServlet("/pay")
public class ReadyBuyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        //获取所购买商品的Id和数量
        String goodsIdAndNum = req.getParameter("goodsIdAndNum");
        System.out.println(goodsIdAndNum+"购买商品Id和数量");
        String[] strings = goodsIdAndNum.split(",");
        List<Goods> goodsList = new ArrayList<>();
        //将每个商品分离
        for (String s : strings) {
            //分离商品信息
            String[] strings1 = s.split("-");
            //通过id获取该商品
            Goods goods = this.getGoods(Integer.valueOf(strings1[0]));
            if(goods != null){
                //商品信息存入list中
                System.out.println(goods+"购买商品信息");
                goodsList.add(goods);
                //修改商品的购买数量
                goods.setBuyGoodsNum(Integer.valueOf(strings1[1]));
            }
        }
        System.out.println("当前购买的商品:"+goodsList);
        //获取用户id通过session
        HttpSession session = req.getSession();
        Account account = (Account)session.getAttribute("user");
        //生成订单对象
        Order order = new Order();
        //设置订单编号为当前时间
        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        order.setAccount_name(account.getUsername());
        //设置时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setCreate_time(LocalDateTime.now().format(formatter));
        //折后价格
        int totalMoney = 0;
        int actualMoney = 0;
        //订单项表
        for(Goods goods : goodsList){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setGoodsId(goods.getId());
            orderItem.setGoodsName(goods.getName());
            orderItem.setGoodsIntroduce(goods.getIntroduce());
            orderItem.setGoodsNum(goods.getBuyGoodsNum());
            orderItem.setGoodsUnit(goods.getUnit());
            orderItem.setGoodsPrice(goods.getIntPrice());
            orderItem.setGoodsDiscount(goods.getDiscount());
            //添加订单项到订单里;
            order.orderItems.add(orderItem);
            //计算实际金额和总金额

            int currentMoney = goods.getBuyGoodsNum()*goods.getIntPrice();
            System.out.println(currentMoney+"当前金额");
            System.out.println(goods.getPrice()+"金额");
            totalMoney += currentMoney;
            actualMoney += currentMoney*goods.getDiscount()/100;
            System.out.println("折扣"+goods.getDiscount()/100);
        }
        order.setTotal_money(totalMoney);
        System.out.println(totalMoney+"总金额");
        order.setActual_amount(actualMoney);
        System.out.println(actualMoney+"应付金额");
        order.setOrder_status(OrderStatus.PLAYING);

        //记录下当前的order
        session.setAttribute("order",order);
        session.setAttribute("goodsList",goodsList);
        System.out.println("传前list"+goodsList);
        System.out.println("传前order"+order);
        resp.getWriter().println("<html>");
        resp.getWriter().println("<p>"+"【用户名称】"+order.getAccount_name()+"</p>");
        resp.getWriter().println("<p>"+"【订单编号】"+order.getId()+"</p>");
        resp.getWriter().println("<p>"+"【订单状态】"+order.getOrder_status().getDesc()+"</p>");
        resp.getWriter().println("<p>"+"【创建时间】"+order.getCreate_time()+"</p>");
        resp.getWriter().println("<p>"+"编号 "+"名称 "+"数量 "+"单位 "+ "单价(元) "+"</p>");
        resp.getWriter().println("<ol>");
        for(OrderItem orderItem : order.orderItems){
            resp.getWriter().println("<li>"+" "+orderItem.getGoodsName()+" " +
                    orderItem.getGoodsNum()+ " "+ orderItem.getGoodsUnit()+" "+
                    orderItem.getGoodsPrice()+"</li>");
        }
        resp.getWriter().println("</ol>");
        resp.getWriter().println("<p>"+"【总金额】：" +order.getTotal_money()+"</p>");
        resp.getWriter().println("<p>"+"【优惠金额】：" +order.getDiscount()+"</p>");
        resp.getWriter().println("<p>"+"【应支付金额】：" +order.getActual_amount()+"</p>");
        //a标签只能访问doget方法!
        resp.getWriter().println("<a href=\"buyGoodsServlet\">确认</a>");
        resp.getWriter().println("<a href=\"index.html\">取消</a>");
        resp.getWriter().println("</html");
    }
    //通过ID获取Goods信息
    private Goods getGoods(int goodsId) {
        Goods goods = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select * from goods where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, goodsId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("查到了");
                goods = this.extractGoods(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, preparedStatement, resultSet);
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