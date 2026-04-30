package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Forum;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.ForumMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl extends ServiceImpl<ForumMapper, Forum> implements ForumService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<Map<String, Object>> getForumList(Page<Forum> page, String category) {
        LambdaQueryWrapper<Forum> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Forum::getCategory, category);
        }
        wrapper.orderByDesc(Forum::getCreateTime);
        IPage<Forum> forumPage = page(page, wrapper);
        Set<Long> userIds = forumPage.getRecords().stream()
                .map(Forum::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        List<Map<String, Object>> records = forumPage.getRecords().stream().map(f -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", f.getId());
            map.put("userId", f.getUserId());
            map.put("title", f.getTitle());
            map.put("content", f.getContent());
            map.put("category", f.getCategory());
            map.put("viewCount", f.getViewCount());
            map.put("replyCount", f.getReplyCount());
            map.put("createTime", f.getCreateTime());
            map.put("updateTime", f.getUpdateTime());
            User user = userMap.get(f.getUserId());
            map.put("author", user != null ? user.getNickname() : "未知");
            return map;
        }).collect(Collectors.toList());
        IPage<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), forumPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getForumDetail(Long id) {
        Forum forum = getById(id);
        if (forum != null) {
            forum.setViewCount(forum.getViewCount() + 1);
            updateById(forum);
        }
        return baseMapper.selectForumDetailWithAuthor(id);
    }

    @Override
    @Transactional
    public boolean createForum(Long userId, String title, String content, String category) {
        Forum forum = new Forum()
                .setUserId(userId)
                .setTitle(title)
                .setContent(content)
                .setCategory(category)
                .setViewCount(0)
                .setReplyCount(0);
        return save(forum);
    }
}
