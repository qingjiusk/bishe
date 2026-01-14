package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Cart;
import com.campus.secondhand.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add")
    public Result<Void> addToCart(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        boolean success = cartService.addToCart(userId, productId, quantity);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @GetMapping("/list")
    public Result<IPage<Cart>> getCartList(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Page<Cart> pageParam = new Page<>(page, size);
        IPage<Cart> result = cartService.getCartList(pageParam, userId);
        return Result.success(result);
    }
    
    @PutMapping("/{cartId}")
    public Result<Void> updateCartQuantity(@PathVariable Long cartId, @RequestBody Map<String, Integer> params) {
        Integer quantity = params.get("quantity");
        boolean success = cartService.updateCartQuantity(cartId, quantity);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        boolean success = cartService.removeFromCart(cartId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean success = cartService.clearCart(userId);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }
}