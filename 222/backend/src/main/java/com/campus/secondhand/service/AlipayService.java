package com.campus.secondhand.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.config.AlipayConfig;
import com.campus.secondhand.entity.Orders;
import com.campus.secondhand.entity.PayRecord;
import com.campus.secondhand.mapper.PayRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AlipayService {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private PayRecordMapper payRecordMapper;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private PrivateKey getPrivateKey() throws Exception {
        if (privateKey == null) {
            byte[] keyBytes = Base64.getDecoder().decode(alipayConfig.getPrivateKey());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        }
        return privateKey;
    }

    private PublicKey getPublicKey() throws Exception {
        if (publicKey == null) {
            byte[] keyBytes = Base64.getDecoder().decode(alipayConfig.getAlipayPublicKey());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        }
        return publicKey;
    }

    /**
     * 生成支付宝电脑网站支付HTML表单
     */
    public String pagePay(String orderNo, BigDecimal amount, String subject) {
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("app_id", alipayConfig.getAppId());
            params.put("method", "alipay.trade.page.pay");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            params.put("version", "1.0");
            params.put("notify_url", alipayConfig.getNotifyUrl());
            params.put("return_url", alipayConfig.getReturnUrl());

            Map<String, Object> bizContent = new LinkedHashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", amount.toString());
            bizContent.put("subject", subject);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            params.put("biz_content", JSON.toJSONString(bizContent));

            String signContent = buildSignContent(params);
            String sign = sign(signContent);
            params.put("sign", sign);

            return buildFormHtml(params);
        } catch (Exception e) {
            log.error("生成支付表单异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 处理支付宝异步通知
     */
    @Transactional
    public String handleNotify(Map<String, String> params) {
        log.info("收到支付宝通知: {}", JSON.toJSONString(params));

        String sign = params.remove("sign");
        String signContent = buildSignContent(params);

        try {
            if (!verify(signContent, sign)) {
                log.error("支付宝通知签名验证失败");
                return "fail";
            }
        } catch (Exception e) {
            log.error("签名验证异常: {}", e.getMessage(), e);
            return "fail";
        }

        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");

        LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayRecord::getOrderNo, orderNo);
        PayRecord existRecord = payRecordMapper.selectOne(wrapper);

        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            if (existRecord != null && existRecord.getPayStatus() == 1) {
                return "success";
            }

            Orders order = ordersService.getOrderDetail(orderNo);
            if (order == null) return "fail";

            if (existRecord == null) {
                existRecord = new PayRecord();
            }
            existRecord.setOrderNo(orderNo);
            existRecord.setPayNo(tradeNo);
            existRecord.setBuyerId(order.getBuyerId());
            existRecord.setTotalAmount(new BigDecimal(totalAmount));
            existRecord.setPayStatus(1);
            existRecord.setPayTime(LocalDateTime.now());

            if (existRecord.getId() != null) {
                payRecordMapper.updateById(existRecord);
            } else {
                payRecordMapper.insert(existRecord);
            }

            log.info("订单支付成功: {}", orderNo);
            return "success";
        }
        return "fail";
    }

    private String buildSignContent(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private String sign(String content) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(getPrivateKey());
        sig.update(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sig.sign());
    }

    private boolean verify(String content, String sign) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(getPublicKey());
        sig.update(content.getBytes(StandardCharsets.UTF_8));
        return sig.verify(Base64.getDecoder().decode(sign));
    }

    private String buildFormHtml(Map<String, String> params) {
        StringBuilder html = new StringBuilder();
        html.append("<form id='alipay-form' name='alipay-form' action='")
           .append(alipayConfig.getGateway()).append("' method='post'>\n");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            html.append("  <input type='hidden' name='").append(entry.getKey())
                .append("' value='").append(escapeHtml(entry.getValue())).append("'/>\n");
        }
        html.append("</form>\n");
        html.append("<script>document.getElementById('alipay-form').submit();</script>");
        return html.toString();
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("\"", "&quot;")
                .replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }
}
