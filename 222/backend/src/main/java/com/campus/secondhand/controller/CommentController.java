package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Comment;
import com.campus.secondhand.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add/{productId}")
    public Result<Void> addComment(@RequestHeader("Authorization") String token, @PathVariable Long productId, @RequestBody Map<String, String> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String content = params.get("content");
        boolean success = commentService.addComment(productId, userId, content);
        return success ? Result.success("留言成功") : Result.error("留言失败");
    }
    
    @PutMapping("/reply/{commentId}")
    public Result<Void> replyComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId, @RequestBody Map<String, String> params) {
        String reply = params.get("reply");
        boolean success = commentService.replyComment(commentId, reply);
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }
    
    @GetMapping("/product/{productId}")
    public Result<IPage<Comment>> getProductComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> result = commentService.getProductComments(pageParam, productId);
        return Result.success(result);
    }
}