package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add/{orderId}")
    public Result<Void> addReview(@RequestHeader("Authorization") String token, @PathVariable Long orderId, @RequestBody Map<String, Object> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long sellerId = Long.valueOf(params.get("sellerId").toString());
        Integer rating = Integer.valueOf(params.get("rating").toString());
        String content = (String) params.get("content");
        boolean success = reviewService.addReview(orderId, userId, sellerId, rating, content);
        return success ? Result.success("评价成功") : Result.error("评价失败");
    }
    
    @GetMapping("/seller/{sellerId}")
    public Result<IPage<Review>> getSellerReviews(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        IPage<Review> result = reviewService.getSellerReviews(pageParam, sellerId);
        return Result.success(result);
    }
}