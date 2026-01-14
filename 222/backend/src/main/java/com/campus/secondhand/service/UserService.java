package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.User;

public interface UserService extends IService<User> {
    
    User login(String username, String password);
    
    User register(User user);
    
    User getUserInfo(Long userId);
    
    boolean updateUserInfo(Long userId, User user);
    
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}