package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Address;
import com.campus.secondhand.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add")
    public Result<Void> addAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        address.setUserId(userId);
        address.setIsDefault(0);
        boolean success = addressService.addAddress(address);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping("/update")
    public Result<Void> updateAddress(@RequestBody Address address) {
        boolean success = addressService.updateAddress(address);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        boolean success = addressService.deleteAddress(addressId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @PutMapping("/default/{addressId}")
    public Result<Void> setDefaultAddress(@RequestHeader("Authorization") String token, @PathVariable Long addressId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean success = addressService.setDefaultAddress(userId, addressId);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }
    
    @GetMapping("/default")
    public Result<Address> getDefaultAddress(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Address address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
    
    @GetMapping("/list")
    public Result<List<Address>> getUserAddresses(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Address> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }
}