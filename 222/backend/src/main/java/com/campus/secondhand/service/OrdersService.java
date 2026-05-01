package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.entity.OrderItem;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    
    Orders createOrder(Long userId, Long addressId, Integer deliveryType, String addressText);
    
    IPage<Orders> getBuyerOrders(Page<Orders> page, Long buyerId, Integer status);
    
    IPage<Orders> getSellerOrders(Page<Orders> page, Long sellerId, Integer status);
    
    Orders getOrderDetail(String orderNo);
    
    boolean confirmReceive(Long orderId);
    
    boolean updateOrderStatus(Long orderId, Integer status);

    boolean requestRefund(Long orderId, Long userId, String reason);

    IPage<Orders> getRefundOrders(Page<Orders> page);

    boolean approveRefund(Long orderId);

    boolean rejectRefund(Long orderId, String reply);
}