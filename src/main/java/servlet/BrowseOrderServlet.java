/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: BrowseOrderServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/14 9:22
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.OrderStatus;
import entity.Account;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/14

 * @since 1.0.0

 */
@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        //获取用户信息
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");
        //创建订单列表
        List<Order> orderList = this.queryOrder(account.getId());
        if(orderList == null){
            System.out.println("订单列表为空");
        }else {
            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter pw = resp.getWriter();
            objectMapper.writeValue(pw,orderList);
            Writer writer = resp.getWriter();
            writer.write(pw.toString());
        }
    }

    private List<Order> queryOrder(Integer id) {
        List<Order> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            String sql = this.getSql("@query_order_by_account");
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();

            Order order = null;

            while (resultSet.next()){
                if(order == null){
                    order = new Order();
                    order = this.extractOrder(order,resultSet);
                    list.add(order);
                }
                String orderId = resultSet.getString("order_id");
                if(!orderId.equals(order.getId())){
                    order = new Order();
                    order = this.extractOrder(order,resultSet);
                    list.add(order);
                }
                OrderItem orderItem = this.extractOrderItem(resultSet);
                order.orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private OrderItem extractOrderItem(ResultSet resultSet) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(resultSet.getInt("item_id"));
        orderItem.setOrderId(resultSet.getString("order_id"));
        orderItem.setGoodsId(resultSet.getInt("goods_Id"));
        orderItem.setGoodsName(resultSet.getString("goods_Name"));
        orderItem.setGoodsIntroduce(resultSet.getString("goods_introduce"));
        orderItem.setGoodsNum(resultSet.getInt("goods_num"));
        orderItem.setGoodsUnit(resultSet.getString("goods_unit"));
        orderItem.setGoodsPrice(resultSet.getInt("goods_price"));
        orderItem.setGoodsDiscount(resultSet.getInt("goods_discount"));

        return orderItem;
    }

    private Order extractOrder(Order order, ResultSet resultSet) throws SQLException {
        order.setId(resultSet.getString("order_id"));
        order.setAccount_id(resultSet.getInt("account_id"));
        order.setAccount_name(resultSet.getString("account_name"));
        order.setCreate_time(resultSet.getString("create_time"));
        order.setFinish_time(resultSet.getString("finish_time"));
        order.setActual_amount(resultSet.getInt("actual_amount"));
        order.setTotal_money(resultSet.getInt("total_money"));
        order.setOrder_status(OrderStatus.valueif(resultSet.getInt("order_status")));
        return order;
    }

    private String getSql(String sqlname){
        InputStream in = this.getClass().getClassLoader().
                getResourceAsStream("script/"+sqlname.substring(1)+".sql");
        if(in == null){
            throw  new RuntimeException("加载sql文件出错");
        }else {
            InputStreamReader isr = new InputStreamReader(in);

            BufferedReader reader = new BufferedReader(isr);
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null){
                    sb.append(" ").append(line);
                }
                System.out.println(sb.toString());
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("转sql字符串异常");
            }
        }
    }

}