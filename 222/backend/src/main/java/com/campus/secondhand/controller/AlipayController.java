package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.JwtUtil;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.entity.PayRecord;
import com.campus.secondhand.mapper.PayRecordMapper;
import com.campus.secondhand.service.AlipayService;
import com.campus.secondhand.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private PayRecordMapper payRecordMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 发起支付，返回支付表单HTML
     */
    @PostMapping("/pay")
    public Result<Map<String, String>> pay(@RequestHeader("Authorization") String token,
                                            @RequestBody Map<String, String> params) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String orderNo = params.get("orderNo");
        if (orderNo == null || orderNo.isEmpty()) {
            return Result.error("订单号不能为空");
        }

        Orders order = ordersService.getOrderDetail(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            return Result.error("无权操作此订单");
        }

        String form = alipayService.pagePay(
                order.getOrderNo(),
                order.getTotalAmount(),
                "校园二手交易平台-订单" + order.getOrderNo()
        );

        if (form == null) {
            return Result.error("支付请求失败，请稍后重试");
        }

        Map<String, String> result = new HashMap<>();
        result.put("payForm", form);
        return Result.success(result);
    }

    /**
     * 查询本地支付状态
     */
    @GetMapping("/query/{orderNo}")
    public Result<Map<String, Object>> queryPayStatus(@PathVariable String orderNo) {
        LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayRecord::getOrderNo, orderNo);
        PayRecord record = payRecordMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (record != null && record.getPayStatus() == 1) {
            result.put("paid", true);
            result.put("payNo", record.getPayNo());
            result.put("payTime", record.getPayTime() != null ? record.getPayTime().toString() : "");
        } else {
            result.put("paid", false);
        }
        return Result.success(result);
    }

    /**
     * 支付宝同步返回处理（用户支付后浏览器跳转）
     */
    @PostMapping("/return-handler")
    public Result<Map<String, Object>> handleReturn(@RequestBody Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");

        if (orderNo == null) {
            return Result.error("缺少订单号");
        }

        LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayRecord::getOrderNo, orderNo);
        PayRecord record = payRecordMapper.selectOne(wrapper);

        if (record == null) {
            Orders order = ordersService.getOrderDetail(orderNo);
            if (order == null) {
                return Result.error("订单不存在");
            }
            record = new PayRecord();
            record.setOrderNo(orderNo);
            record.setPayNo(tradeNo);
            record.setBuyerId(order.getBuyerId());
            record.setTotalAmount(order.getTotalAmount());
            record.setPayStatus(1);
            record.setPayTime(java.time.LocalDateTime.now());
            payRecordMapper.insert(record);

            // Update order payment method
            ordersService.updateOrderStatus(order.getId(), order.getStatus());
        } else if (record.getPayStatus() != 1) {
            record.setPayStatus(1);
            record.setPayNo(tradeNo);
            record.setPayTime(java.time.LocalDateTime.now());
            payRecordMapper.updateById(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paid", record.getPayStatus() == 1);
        return Result.success(result);
    }

    /**
     * 支付宝异步通知（无需认证，由支付宝服务器回调）
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));
        return alipayService.handleNotify(params);
    }
}
