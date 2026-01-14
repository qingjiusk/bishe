package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrdersController {
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/create")
    public Result<Orders> createOrder(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long addressId = Long.valueOf(params.get("addressId").toString());
        Integer deliveryType = Integer.valueOf(params.get("deliveryType").toString());
        Orders order = ordersService.createOrder(userId, addressId, deliveryType);
        return Result.success("下单成功", order);
    }
    
    @GetMapping("/buyer/list")
    public Result<IPage<Orders>> getBuyerOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Page<Orders> pageParam = new Page<>(page, size);
        IPage<Orders> result = ordersService.getBuyerOrders(pageParam, userId, status);
        return Result.success(result);
    }
    
    @GetMapping("/seller/list")
    public Result<IPage<Orders>> getSellerOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Page<Orders> pageParam = new Page<>(page, size);
        IPage<Orders> result = ordersService.getSellerOrders(pageParam, userId, status);
        return Result.success(result);
    }
    
    @GetMapping("/{orderNo}")
    public Result<Orders> getOrderDetail(@PathVariable String orderNo) {
        Orders order = ordersService.getOrderDetail(orderNo);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }
    
    @PutMapping("/{orderId}/confirm")
    public Result<Void> confirmReceive(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Orders order = ordersService.getById(orderId);
        if (order == null || !order.getBuyerId().equals(userId)) {
            return Result.error("无权操作");
        }
        boolean success = ordersService.confirmReceive(orderId);
        return success ? Result.success("确认收货成功") : Result.error("确认收货失败");
    }
}