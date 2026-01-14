package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    
    boolean addToCart(Long userId, Long productId, Integer quantity);
    
    IPage<Cart> getCartList(Page<Cart> page, Long userId);
    
    boolean updateCartQuantity(Long cartId, Integer quantity);
    
    boolean removeFromCart(Long cartId);
    
    boolean clearCart(Long userId);
}