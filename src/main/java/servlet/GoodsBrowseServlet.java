/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: GoodsBrowseServlet
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/12 9:48
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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

 * @create 2020/2/12

 * @since 1.0.0

 */
@WebServlet("/browseGoods")
public class GoodsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Goods> list = new ArrayList<>();
        try{
            String sql = "select id,name,introduce,stock,unit,price,discount" +
                    " from goods";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Goods goods = this.extractGoods(resultSet);
                if(goods != null){
                    list.add(goods);
                }
            }
            System.out.println(list);
            //将list装换位Json然后发送给前段进行解析
            //可以方便的将模型对象转化为Json
            ObjectMapper mapper = new ObjectMapper();
            //将响应包推送给浏览器
            PrintWriter pw = resp.getWriter();
            //jiang Json字符转填充到writer中
            mapper.writeValue(pw,list);

            Writer writer = resp.getWriter();
            writer.write(pw.toString());
            System.out.println("over=====================");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection, preparedStatement, resultSet);
        }
    }
    public Goods extractGoods(ResultSet resultSet)throws SQLException{
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        return goods;
    }
}