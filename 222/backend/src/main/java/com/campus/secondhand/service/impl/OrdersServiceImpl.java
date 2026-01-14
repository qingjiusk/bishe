package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.*;
import com.campus.secondhand.mapper.*;
import com.campus.secondhand.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    @Transactional
    public Orders createOrder(Long userId, Long addressId, Integer deliveryType) {
        QueryWrapper<Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        List<Cart> cartList = cartMapper.selectList(cartWrapper);
        
        if (cartList.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new RuntimeException("收货地址不存在");
        }
        
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1 || product.getAuditStatus() != 1) {
                throw new RuntimeException("商品不存在或已下架");
            }
            
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        BigDecimal deliveryFee = deliveryType == 2 ? new BigDecimal("5.00") : BigDecimal.ZERO;
        totalAmount = totalAmount.add(deliveryFee);
        
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setBuyerId(userId);
        order.setTotalAmount(totalAmount);
        order.setDeliveryType(deliveryType);
        order.setDeliveryFee(deliveryFee);
        order.setStatus(1);
        order.setAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setPaymentMethod("线下支付");
        save(order);
        
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getThumbnail());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItemMapper.insert(orderItem);
            
            order.setSellerId(product.getSellerId());
            updateById(order);
            
            product.setStatus(2);
            productMapper.updateById(product);
        }
        
        cartMapper.delete(cartWrapper);
        
        return order;
    }
    
    @Override
    public IPage<Orders> getBuyerOrders(Page<Orders> page, Long buyerId, Integer status) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("buyer_id", buyerId);
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }
    
    @Override
    public IPage<Orders> getSellerOrders(Page<Orders> page, Long sellerId, Integer status) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId);
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }
    
    @Override
    public Orders getOrderDetail(String orderNo) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        return getOne(wrapper);
    }
    
    @Override
    public boolean confirmReceive(Long orderId) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        return updateById(order);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(status);
        if (status == 2) {
            order.setShipTime(LocalDateTime.now());
        }
        return updateById(order);
    }
}