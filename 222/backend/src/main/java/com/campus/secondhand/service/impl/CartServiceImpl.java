package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Cart;
import com.campus.secondhand.mapper.CartMapper;
import com.campus.secondhand.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    
    @Override
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        Cart existCart = getOne(wrapper);
        
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + quantity);
            return updateById(existCart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            return save(cart);
        }
    }
    
    @Override
    public IPage<Cart> getCartList(Page<Cart> page, Long userId) {
        return baseMapper.selectCartListWithProduct(page, userId);
    }
    
    @Override
    public boolean updateCartQuantity(Long cartId, Integer quantity) {
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setQuantity(quantity);
        return updateById(cart);
    }
    
    @Override
    public boolean removeFromCart(Long cartId) {
        return removeById(cartId);
    }
    
    @Override
    public boolean clearCart(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return remove(wrapper);
    }
}