package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add/{productId}")
    public Result<Void> addFavorite(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean success = favoriteService.addFavorite(userId, productId);
        return success ? Result.success("收藏成功") : Result.error("收藏失败");
    }
    
    @DeleteMapping("/remove/{productId}")
    public Result<Void> removeFavorite(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean success = favoriteService.removeFavorite(userId, productId);
        return success ? Result.success("取消收藏成功") : Result.error("取消收藏失败");
    }
    
    @GetMapping("/check/{productId}")
    public Result<Boolean> isFavorite(@RequestHeader("Authorization") String token, @PathVariable Long productId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean isFav = favoriteService.isFavorite(userId, productId);
        return Result.success(isFav);
    }
    
    @GetMapping("/list")
    public Result<IPage<Favorite>> getFavoriteList(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Page<Favorite> pageParam = new Page<>(page, size);
        IPage<Favorite> result = favoriteService.getFavoriteList(pageParam, userId);
        return Result.success(result);
    }
}