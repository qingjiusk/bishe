package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    
    @Override
    public boolean addFavorite(Long userId, Long productId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        
        if (count(wrapper) > 0) {
            return true;
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        return save(favorite);
    }
    
    @Override
    public boolean removeFavorite(Long userId, Long productId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        return remove(wrapper);
    }
    
    @Override
    public boolean isFavorite(Long userId, Long productId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        return count(wrapper) > 0;
    }
    
    @Override
    public IPage<Favorite> getFavoriteList(Page<Favorite> page, Long userId) {
        return baseMapper.selectFavoriteListWithProduct(page, userId);
    }
}