package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "1/5 * * * * ?")
    public void processTimeOutOrder() {
        log.info("定时处理超时订单");
        // Logic to process timed out orders
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> list = orderMapper.getStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,orderTime);
        if(list != null && list.size() > 0){
            for (Orders order : list) {
                // Update order status to cancelled
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder(){
        //定时处理配送订单
        log.info("定时处理配送订单");
        List<Orders> list = orderMapper.getStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().plusMinutes(-60));
        if(list != null && list.size() > 0){
            for (Orders order : list) {
                // Update order status to cancelled
                order.setStatus(Orders.COMPLETED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }
}
