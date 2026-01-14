package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Comment;
import com.campus.secondhand.mapper.CommentMapper;
import com.campus.secondhand.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Override
    public boolean addComment(Long productId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setProductId(productId);
        comment.setUserId(userId);
        comment.setContent(content);
        return save(comment);
    }
    
    @Override
    public boolean replyComment(Long commentId, String reply) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setReply(reply);
        comment.setReplyTime(LocalDateTime.now());
        return updateById(comment);
    }
    
    @Override
    public IPage<Comment> getProductComments(Page<Comment> page, Long productId) {
        return lambdaQuery()
                .eq(Comment::getProductId, productId)
                .orderByDesc(Comment::getCreateTime)
                .page(page);
    }
}