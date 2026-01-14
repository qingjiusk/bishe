package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Comment;

public interface CommentService extends IService<Comment> {
    
    boolean addComment(Long productId, Long userId, String content);
    
    boolean replyComment(Long commentId, String reply);
    
    IPage<Comment> getProductComments(Page<Comment> page, Long productId);
}