package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/list")
    public Result<IPage<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> result = productService.getProductList(pageParam);
        return Result.success(result);
    }
    
    @GetMapping("/category/{categoryId}")
    public Result<IPage<Product>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> result = productService.getProductsByCategory(pageParam, categoryId);
        return Result.success(result);
    }
    
    @GetMapping("/search")
    public Result<IPage<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> result = productService.searchProducts(pageParam, keyword);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }
    
    @PostMapping("/publish")
    public Result<Void> publishProduct(@RequestHeader("Authorization") String token, @RequestBody Product product) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        product.setSellerId(userId);
        boolean success = productService.publishProduct(product);
        return success ? Result.success("发布成功，等待审核") : Result.error("发布失败");
    }
    
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Product product) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Product existProduct = productService.getById(id);
        if (existProduct == null || !existProduct.getSellerId().equals(userId)) {
            return Result.error("无权操作");
        }
        boolean success = productService.updateProduct(id, product);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Product existProduct = productService.getById(id);
        if (existProduct == null || !existProduct.getSellerId().equals(userId)) {
            return Result.error("无权操作");
        }
        boolean success = productService.deleteProduct(id);
        return success ? Result.success("下架成功") : Result.error("下架失败");
    }
    
    @GetMapping("/my")
    public Result<IPage<Product>> getMyProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> result = productService.getMyProducts(pageParam, userId, auditStatus);
        return Result.success(result);
    }
}