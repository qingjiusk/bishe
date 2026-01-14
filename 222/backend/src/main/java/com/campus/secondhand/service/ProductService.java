package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Product;

public interface ProductService extends IService<Product> {
    
    IPage<Product> getProductList(Page<Product> page);
    
    IPage<Product> getProductsByCategory(Page<Product> page, Integer categoryId);
    
    IPage<Product> searchProducts(Page<Product> page, String keyword);
    
    Product getProductDetail(Long id);
    
    boolean publishProduct(Product product);
    
    boolean updateProduct(Long productId, Product product);
    
    boolean deleteProduct(Long productId);
    
    boolean auditProduct(Long productId, Integer auditStatus, String auditReason);
    
    IPage<Product> getMyProducts(Page<Product> page, Long sellerId, Integer auditStatus);
}