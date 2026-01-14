# 校园二手交易平台

基于 Spring Boot + Vue3 + MySQL 的校园二手交易平台，采用前后端分离架构。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Druid 数据库连接池
- JWT 身份认证
- BCrypt 密码加密

### 前端
- Vue 3
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios

## 项目结构

```
222/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/campus/secondhand/
│   │   │   │   ├── CampusSecondhandApplication.java  # 启动类
│   │   │   │   ├── common/        # 公共类
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── mapper/        # Mapper接口
│   │   │   │   └── service/       # 服务层
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── sql/
│   │   │           └── campus_secondhand.sql
│   │   └── test/
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   ├── views/          # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 功能模块

### 用户模块
- 用户注册（支持学号验证）
- 用户登录（用户名/学号 + 密码）
- 个人信息管理
- 修改密码
- 收货地址管理

### 商品模块
- 商品浏览（首页展示、分类浏览、搜索）
- 商品详情查看
- 发布商品（支持多图上传）
- 编辑/下架商品
- 商品审核（管理员）
- 商品留言与回复

### 购物车模块
- 添加商品到购物车
- 修改商品数量
- 移除商品
- 清空购物车

### 订单模块
- 创建订单
- 查看订单列表（买家/卖家）
- 确认收货
- 订单状态管理

### 收藏模块
- 添加/取消收藏
- 查看收藏列表

### 评价模块
- 对商家进行评价
- 查看商家评价

### 后台管理模块
- 商品审核
- 订单管理
- 用户管理
- 数据统计

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE campus_secondhand DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本
```bash
mysql -u root -p campus_secondhand < backend/src/main/resources/sql/campus_secondhand.sql
```

3. 修改配置文件
编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_secondhand?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

4. 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 默认账号

### 管理员
- 用户名: admin
- 密码: admin123

### 测试用户
- 用户名: test001
- 密码: 123456

- 用户名: test002
- 密码: 123456

## API文档

### 用户相关
- POST `/api/user/register` - 用户注册
- POST `/api/user/login` - 用户登录
- GET `/api/user/info` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息
- PUT `/api/user/password` - 修改密码

### 商品相关
- GET `/api/product/list` - 获取商品列表
- GET `/api/product/{id}` - 获取商品详情
- GET `/api/product/search` - 搜索商品
- POST `/api/product/publish` - 发布商品
- PUT `/api/product/{id}` - 编辑商品
- DELETE `/api/product/{id}` - 下架商品
- GET `/api/product/my` - 获取我发布的商品

### 购物车相关
- POST `/api/cart/add` - 添加到购物车
- GET `/api/cart/list` - 获取购物车列表
- PUT `/api/cart/{cartId}` - 修改数量
- DELETE `/api/cart/{cartId}` - 移除商品
- DELETE `/api/cart/clear` - 清空购物车

### 订单相关
- POST `/api/order/create` - 创建订单
- GET `/api/order/buyer/list` - 获取买家订单
- GET `/api/order/seller/list` - 获取卖家订单
- GET `/api/order/{orderNo}` - 获取订单详情
- PUT `/api/order/{orderId}/confirm` - 确认收货

### 收藏相关
- POST `/api/favorite/add/{productId}` - 添加收藏
- DELETE `/api/favorite/remove/{productId}` - 取消收藏
- GET `/api/favorite/check/{productId}` - 检查是否收藏
- GET `/api/favorite/list` - 获取收藏列表

### 留言相关
- POST `/api/comment/add/{productId}` - 发表留言
- PUT `/api/comment/reply/{commentId}` - 回复留言
- GET `/api/comment/product/{productId}` - 获取商品留言

### 地址相关
- POST `/api/address/add` - 添加地址
- PUT `/api/address/update` - 更新地址
- DELETE `/api/address/{addressId}` - 删除地址
- PUT `/api/address/default/{addressId}` - 设置默认地址
- GET `/api/address/default` - 获取默认地址
- GET `/api/address/list` - 获取地址列表

### 分类相关
- GET `/api/category/list` - 获取分类列表

### 评价相关
- POST `/api/review/add/{orderId}` - 添加评价
- GET `/api/review/seller/{sellerId}` - 获取商家评价

### 管理员相关
- GET `/api/admin/users` - 获取用户列表
- GET `/api/admin/products/audit` - 获取待审核商品
- PUT `/api/admin/product/{id}/audit` - 审核商品
- GET `/api/admin/orders` - 获取订单列表
- PUT `/api/admin/order/{id}/ship` - 发货
- PUT `/api/admin/order/{id}/confirm` - 确认收货

## 注意事项

1. 文件上传功能需要配置上传路径，默认为 `D:/campus-secondhand/uploads/`，请根据实际情况修改 `application.yml` 中的配置
2. 图片上传目前使用模拟方式，实际项目中需要集成对象存储服务（如阿里云OSS）
3. 学号验证功能需要与学校提供的接口对接
4. 短信通知功能需要接入短信服务（如阿里云短信、腾讯云短信）

## 许可证

MIT License