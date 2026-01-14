package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Review;

public interface ReviewService extends IService<Review> {
    
    boolean addReview(Long orderId, Long buyerId, Long sellerId, Integer rating, String content);
    
    IPage<Review> getSellerReviews(Page<Review> page, Long sellerId);
}