package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public IPage<Product> getProductList(Page<Product> page) {
        return baseMapper.selectProductListWithSeller(page);
    }
    
    @Override
    public IPage<Product> getProductsByCategory(Page<Product> page, Integer categoryId) {
        return baseMapper.selectProductsByCategory(page, categoryId);
    }
    
    @Override
    public IPage<Product> searchProducts(Page<Product> page, String keyword) {
        return baseMapper.searchProducts(page, keyword);
    }
    
    @Override
    public Product getProductDetail(Long id) {
        Product product = baseMapper.selectProductDetailWithSeller(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() + 1);
            updateById(product);
        }
        return product;
    }
    
    @Override
    public boolean publishProduct(Product product) {
        product.setStatus(1);
        product.setAuditStatus(0);
        product.setViewCount(0);
        product.setUnreadComment(0);
        return save(product);
    }
    
    @Override
    public boolean updateProduct(Long productId, Product product) {
        product.setId(productId);
        return updateById(product);
    }
    
    @Override
    public boolean deleteProduct(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(0);
        return updateById(product);
    }
    
    @Override
    public boolean auditProduct(Long productId, Integer auditStatus, String auditReason) {
        Product product = new Product();
        product.setId(productId);
        product.setAuditStatus(auditStatus);
        product.setAuditReason(auditReason);
        return updateById(product);
    }
    
    @Override
    public IPage<Product> getMyProducts(Page<Product> page, Long sellerId, Integer auditStatus) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId);
        if (auditStatus != null) {
            wrapper.eq("audit_status", auditStatus);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }
}