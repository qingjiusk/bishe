package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.entity.OrderItem;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    
    Orders createOrder(Long userId, Long addressId, Integer deliveryType);
    
    IPage<Orders> getBuyerOrders(Page<Orders> page, Long buyerId, Integer status);
    
    IPage<Orders> getSellerOrders(Page<Orders> page, Long sellerId, Integer status);
    
    Orders getOrderDetail(String orderNo);
    
    boolean confirmReceive(Long orderId);
    
    boolean updateOrderStatus(Long orderId, Integer status);
}