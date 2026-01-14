package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            String token = jwtUtil.generateToken(registeredUser.getId(), registeredUser.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", registeredUser);
            
            return Result.success("注册成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success("登录成功", data);
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserInfo(userId);
        user.setPassword(null);
        return Result.success(user);
    }
    
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody User user) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean success = userService.updateUserInfo(userId, user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        try {
            boolean success = userService.updatePassword(userId, oldPassword, newPassword);
            return success ? Result.success("密码修改成功") : Result.error("密码修改失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}