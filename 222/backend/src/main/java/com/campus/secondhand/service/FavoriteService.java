package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    
    boolean addFavorite(Long userId, Long productId);
    
    boolean removeFavorite(Long userId, Long productId);
    
    boolean isFavorite(Long userId, Long productId);
    
    IPage<Favorite> getFavoriteList(Page<Favorite> page, Long userId);
}