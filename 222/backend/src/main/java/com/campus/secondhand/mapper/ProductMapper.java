package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select("SELECT p.*, u.nickname as seller_name, u.phone as seller_phone " +
            "FROM product p " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.audit_status = 1 AND p.status = 1 " +
            "ORDER BY p.create_time DESC")
    IPage<Product> selectProductListWithSeller(Page<Product> page);
    
    @Select("SELECT p.*, u.nickname as seller_name, u.phone as seller_phone " +
            "FROM product p " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.audit_status = 1 AND p.status = 1 AND p.category_id = #{categoryId} " +
            "ORDER BY p.create_time DESC")
    IPage<Product> selectProductsByCategory(Page<Product> page, @Param("categoryId") Integer categoryId);
    
    @Select("SELECT p.*, u.nickname as seller_name, u.phone as seller_phone " +
            "FROM product p " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.audit_status = 1 AND p.status = 1 AND p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY p.create_time DESC")
    IPage<Product> searchProducts(Page<Product> page, @Param("keyword") String keyword);
    
    @Select("SELECT p.*, u.nickname as seller_name, u.phone as seller_phone " +
            "FROM product p " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.id = #{id}")
    Product selectProductDetailWithSeller(@Param("id") Long id);
}