/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: OrderStatus
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/13 9:54
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package common;

import entity.Order;
import lombok.Getter;
import lombok.ToString;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/13

 * @since 1.0.0

 */
@Getter
@ToString
//枚举订单信息
public enum OrderStatus {
    PLAYING(1, "待支付"),OK(2,"支付完成");
    private int flg;
    private String desc;

    //构造方法
    OrderStatus(int flg, String desc){
        this.flg = flg;
        this.desc = desc;
    }

    //通过flg获取对象
    public static OrderStatus valueif(int flg){
        for (OrderStatus orderStatus : OrderStatus.values()){
            if(orderStatus.flg == flg){
                return orderStatus;
            }
        }
        throw new RuntimeException("无此订单状态");
    }
}