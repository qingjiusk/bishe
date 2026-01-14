package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.mapper.ReviewMapper;
import com.campus.secondhand.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    
    @Override
    public boolean addReview(Long orderId, Long buyerId, Long sellerId, Integer rating, String content) {
        Review review = new Review();
        review.setOrderId(orderId);
        review.setBuyerId(buyerId);
        review.setSellerId(sellerId);
        review.setRating(rating);
        review.setContent(content);
        return save(review);
    }
    
    @Override
    public IPage<Review> getSellerReviews(Page<Review> page, Long sellerId) {
        return lambdaQuery()
                .eq(Review::getSellerId, sellerId)
                .orderByDesc(Review::getCreateTime)
                .page(page);
    }
}