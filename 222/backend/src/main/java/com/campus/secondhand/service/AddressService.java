package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    
    boolean addAddress(Address address);
    
    boolean updateAddress(Address address);
    
    boolean deleteAddress(Long addressId);
    
    boolean setDefaultAddress(Long userId, Long addressId);
    
    Address getDefaultAddress(Long userId);
    
    List<Address> getUserAddresses(Long userId);
}