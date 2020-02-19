/**
 * Copyright (C), 2015-2020, XXX有限公司
 * <p>
 * FileName: Goods
 * <p>
 * Author:   HASEE
 * <p>
 * Date:     2020/2/12 10:20
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

 * @create 2020/2/12

 * @since 1.0.0

 */
@Data
public class Goods {
    private Integer id;
    private String name;
    private String introduce;
    private Integer stock;
    private String unit;
    private Integer price;
    private Integer discount;

    private Integer buyGoodsNum;

    public double getPrice(){
        return price*1.0/100;
    }
    public int getIntPrice(){
        return price;
    }
}