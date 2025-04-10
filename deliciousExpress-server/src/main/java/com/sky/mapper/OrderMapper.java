package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    @Select("SELECT * FROM address_book WHERE id = #{addressBookId}")
    AddressBook getById(Long addressBookId);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("UPDATE orders SET pay_status = #{orderPaidStatus}, status = #{orderStatus}, checkout_time = #{checkoutTime} WHERE number = #{orderNumber}")
    void updateStatus(String orderNumber, Integer orderPaidStatus, Integer orderStatus, LocalDateTime checkoutTime);
}
