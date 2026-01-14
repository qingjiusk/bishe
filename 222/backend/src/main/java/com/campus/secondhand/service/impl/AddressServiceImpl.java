package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Address;
import com.campus.secondhand.mapper.AddressMapper;
import com.campus.secondhand.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    @Override
    public boolean addAddress(Address address) {
        return save(address);
    }
    
    @Override
    public boolean updateAddress(Address address) {
        return updateById(address);
    }
    
    @Override
    public boolean deleteAddress(Long addressId) {
        return removeById(addressId);
    }
    
    @Override
    public boolean setDefaultAddress(Long userId, Long addressId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_default", 1);
        
        Address oldDefault = getOne(wrapper);
        if (oldDefault != null) {
            oldDefault.setIsDefault(0);
            updateById(oldDefault);
        }
        
        Address newDefault = new Address();
        newDefault.setId(addressId);
        newDefault.setIsDefault(1);
        return updateById(newDefault);
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_default", 1);
        return getOne(wrapper);
    }
    
    @Override
    public List<Address> getUserAddresses(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("is_default");
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }
}