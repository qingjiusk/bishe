package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Forum;

import java.util.Map;

public interface ForumService extends IService<Forum> {

    IPage<Map<String, Object>> getForumList(Page<Forum> page, String category);

    Map<String, Object> getForumDetail(Long id);

    boolean createForum(Long userId, String title, String content, String category);
}
