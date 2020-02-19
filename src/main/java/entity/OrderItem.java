/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: OrderItem
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/13 10:23
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <XD>          <time>          <1.1>          <javaDamo>
 */
package entity;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author HASEE

 * @create 2020/2/13

 * @since 1.0.0

 */
@Data
public class OrderItem {
    private Integer id;
    private String orderId;
    private Integer goodsId;
    private String goodsName;
    private String goodsIntroduce;
    private Integer goodsNum;
    private String goodsUnit;
    private Integer goodsPrice;
    private Integer goodsDiscount;

    public double getGoodsPrice(){
        return goodsPrice*1.0/100;
    }
    public int getIntGoodsPrice(){
        return goodsPrice;
    }
}