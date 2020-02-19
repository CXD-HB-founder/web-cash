/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: Order
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/13 9:52
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package entity;

import common.OrderStatus;
import lombok.Data;

import java.text.NumberFormat;
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
@Data
//订单信息
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_money;
    private OrderStatus order_status;

    public OrderStatus getStatusDesc(){
        return order_status;
    }
    //浏览订单用
    //创建list存储订单项
    public List<OrderItem> orderItems = new ArrayList<>();

    public double getTotal_money(){
        return total_money * 1.0 / 100;
    }

    public int getTotalMoneyInt(){
        return total_money;
    }

    //浏览订单用
    public double getActual_amount(){
        double num = actual_amount * 1.0 / 100 ;
        System.out.println(num);
        return num;
    }

    public int getActualAmountInt(){
        return actual_amount;
    }

    public double getDiscount(){
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        double discountMoney = this.getTotal_money() - this.getActual_amount();
        System.out.println(discountMoney);
        return Double.valueOf(numberFormat.format(discountMoney));
    }

}