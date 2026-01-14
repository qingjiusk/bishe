package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.OrdersService;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrdersService ordersService;
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        System.out.println("========== 开始查询统计数据 ==========");
        long userCount = userService.count();
        System.out.println("用户数量: " + userCount);

        long productCount = productService.count();
        System.out.println("商品数量: " + productCount);

        long orderCount = ordersService.count();
        System.out.println("订单数量: " + orderCount);

        long pendingCount = productService.lambdaQuery()
                .eq(Product::getAuditStatus, 0)
                .count();
        System.out.println("待审核数量: " + pendingCount);
        System.out.println("========== 查询结束 ==========");

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("userCount", userCount);
        stats.put("productCount", productCount);
        stats.put("orderCount", orderCount);
        stats.put("pendingCount", pendingCount);

        return Result.success(stats);
    }

    @GetMapping("/test-db")
    public Result<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            // 测试查询所有用户（包括已删除）
            List<User> allUsers = userService.lambdaQuery().select().list();
            result.put("allUsersCount", allUsers.size());

            // 测试查询未删除用户
            List<User> activeUsers = userService.lambdaQuery().eq(User::getDeleted, 0).list();
            result.put("activeUsersCount", activeUsers.size());

            // 测试查询所有商品（包括已删除）
            List<Product> allProducts = productService.lambdaQuery().select().list();
            result.put("allProductsCount", allProducts.size());

            // 测试查询未删除商品
            List<Product> activeProducts = productService.lambdaQuery().eq(Product::getDeleted, 0).list();
            result.put("activeProductsCount", activeProducts.size());

            // 测试查询所有订单（订单表没有 deleted 字段）
            List<Orders> allOrders = ordersService.lambdaQuery().select().list();
            result.put("allOrdersCount", allOrders.size());

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return Result.error("数据库测试失败: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }
    
    @GetMapping("/products/audit")
    public Result<IPage<Product>> getAuditProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> result = productService.lambdaQuery()
                .eq(Product::getAuditStatus, 0)
                .orderByDesc(Product::getCreateTime)
                .page(pageParam);
        return Result.success(result);
    }
    
    @PutMapping("/product/{id}/audit")
    public Result<Void> auditProduct(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer auditStatus = Integer.valueOf(params.get("auditStatus").toString());
        String auditReason = (String) params.get("auditReason");
        boolean success = productService.auditProduct(id, auditStatus, auditReason);
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }
    
    @GetMapping("/orders")
    public Result<IPage<Orders>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Orders> pageParam = new Page<>(page, size);
        IPage<Orders> result = ordersService.lambdaQuery()
                .orderByDesc(Orders::getCreateTime)
                .page(pageParam);
        return Result.success(result);
    }
    
    @PutMapping("/order/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id) {
        boolean success = ordersService.updateOrderStatus(id, 2);
        return success ? Result.success("发货成功") : Result.error("发货失败");
    }
    
    @PutMapping("/order/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        boolean success = ordersService.confirmReceive(id);
        return success ? Result.success("确认成功") : Result.error("确认失败");
    }
}