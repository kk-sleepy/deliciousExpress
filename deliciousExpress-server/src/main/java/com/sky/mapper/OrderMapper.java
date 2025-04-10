package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    @Select("SELECT * FROM address_book WHERE id = #{addressBookId}")
    AddressBook getById(Long addressBookId);
}
