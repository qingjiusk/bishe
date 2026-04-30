package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Forum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ForumMapper extends BaseMapper<Forum> {

    @Select("SELECT f.*, u.nickname as author " +
            "FROM forum f " +
            "LEFT JOIN user u ON f.user_id = u.id " +
            "ORDER BY f.create_time DESC")
    IPage<Map<String, Object>> selectAllForumWithAuthor(Page<Map<String, Object>> page);

    @Select("SELECT f.*, u.nickname as author " +
            "FROM forum f " +
            "LEFT JOIN user u ON f.user_id = u.id " +
            "WHERE f.category = #{category} " +
            "ORDER BY f.create_time DESC")
    IPage<Map<String, Object>> selectForumListWithAuthor(Page<Map<String, Object>> page, @Param("category") String category);

    @Select("SELECT f.*, u.nickname as author " +
            "FROM forum f " +
            "LEFT JOIN user u ON f.user_id = u.id " +
            "WHERE f.id = #{id}")
    Map<String, Object> selectForumDetailWithAuthor(@Param("id") Long id);
}
