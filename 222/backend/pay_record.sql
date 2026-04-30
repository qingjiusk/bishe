CREATE TABLE IF NOT EXISTS pay_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL,
    pay_no VARCHAR(64) NOT NULL COMMENT '支付宝交易号',
    buyer_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_status INT DEFAULT 0 COMMENT '0=未支付 1=已支付 2=已关闭',
    pay_time DATETIME COMMENT '支付时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_pay_no (pay_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
