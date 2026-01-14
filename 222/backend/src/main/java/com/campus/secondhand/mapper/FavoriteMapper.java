package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
    @Select("SELECT f.*, p.name as product_name, p.price as product_price, p.thumbnail as product_image " +
            "FROM favorite f " +
            "LEFT JOIN product p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    IPage<Favorite> selectFavoriteListWithProduct(Page<Favorite> page, @Param("userId") Long userId);
}