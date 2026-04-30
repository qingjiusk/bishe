package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Forum;
import com.campus.secondhand.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> getForumList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String category) {
        Page<Forum> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> result = forumService.getForumList(pageParam, category);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getForumDetail(@PathVariable Long id) {
        Map<String, Object> detail = forumService.getForumDetail(id);
        return detail != null ? Result.success(detail) : Result.error("帖子不存在");
    }

    @PostMapping("/create")
    public Result<Void> createForum(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, String> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String title = params.get("title");
        String content = params.get("content");
        String category = params.getOrDefault("category", "交流");
        boolean success = forumService.createForum(userId, title, content, category);
        return success ? Result.success("发帖成功") : Result.error("发帖失败");
    }
}
