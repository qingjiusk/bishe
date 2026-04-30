# bishe — 校园二手交易平台（基于Spring Boot + Vue3）

用户（周嘉伟，20201234567）的本科毕业设计，南京信息工程大学计算机学院软件工程专业，指导教师张老师。论文题目《基于Spring Boot与Vue的校园二手交易平台设计与实现》。

## 项目结构

此文件夹为 git 仓库，包含系统代码。LaTeX 论文模板在上级目录 `../NUIST_Bachelor_Thesis_LaTeX_Template/`。

```
bishe/
├── README.md                       # 项目简介
├── CLAUDE.md                       # 本文件
└── 222/
    ├── README.md                   # 项目详细文档（API、功能、快速开始）
    ├── backend/                    # Spring Boot 后端
    │   ├── pom.xml                 # Maven 配置（Spring Boot 3.2.0）
    │   ├── src/main/java/com/campus/secondhand/
    │   │   ├── CampusSecondhandApplication.java  # 主启动类
    │   │   ├── common/             # 统一响应体 Result<T>、JwtUtil、异常处理
    │   │   ├── config/             # WebConfig、MyBatisPlusConfig、DruidConfig
    │   │   ├── controller/         # REST控制器（按模块分包）
    │   │   ├── entity/             # 实体类（User/Product/Order/Cart/…）
    │   │   ├── mapper/             # MyBatis Plus Mapper
    │   │   └── service/            # 业务服务层（接口+实现）
    │   ├── src/main/resources/
    │   │   ├── application.yml     # 数据库/JWT/上传路径配置
    │   │   └── sql/
    │   │       └── campus_secondhand.sql  # 建库建表脚本
    │   └── *.sql                   # 商品测试数据SQL
    └── frontend/                   # Vue3 前端
        ├── package.json            # Vue3 + Vite + Element Plus + Pinia + Axios
        ├── vite.config.js          # Vite 配置（含代理）
        ├── index.html
        └── src/
            ├── main.js             # 入口
            ├── App.vue             # 根组件
            ├── api/                # Axios 封装 + 各模块API调用
            ├── router/             # 路由配置（含导航守卫）
            ├── stores/             # Pinia 状态管理（userStore）
            └── views/              # 页面组件（用户/商品/购物车/订单/管理）
```

上级论文目录：
```
../NUIST_Bachelor_Thesis_LaTeX_Template/
├── NUIST_thesis.tex         # 论文主文件（封面+结构）
├── nuist.cls                # 文档类（已修复SimKai→KaiTi）
├── bibliography.bib         # 参考文献（20篇）
├── figs/color/              # 论文插图
└── body/
    ├── front.tex            # 摘要+目录
    ├── intro.tex            # 绪言
    ├── nuistcommand.tex     # 系统需求分析
    ├── math.tex             # 系统总体设计
    ├── figs.tex             # 系统详细实现
    ├── table.tex            # 系统测试与结果分析
    ├── code.tex             # 部署方案与安全设计
    ├── ref.tex              # 总结与展望
    ├── bib.tex              # 参考文献引用
    └── appendix.tex         # 附录
```

## 技术栈

| 层级 | 技术 | 版本/说明 |
|------|------|-----------|
| 后端框架 | Spring Boot | 3.2.0 |
| ORM | MyBatis Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 连接池 | Druid | — |
| 认证 | JWT + BCrypt | io.jsonwebtoken |
| 前端框架 | Vue 3 | Composition API |
| 构建工具 | Vite | — |
| UI组件库 | Element Plus | — |
| 状态管理 | Pinia | — |
| HTTP客户端 | Axios | 拦截器统一注入Token |

## 快速启动

### 环境要求
- JDK 17+, Node.js 16+, MySQL 8.0+, Maven 3.6+

### 后端（222/backend/）
```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_secondhand DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 执行建表脚本
mysql -u root -p campus_secondhand < src/main/resources/sql/campus_secondhand.sql

# 3. 修改 application.yml 中的数据库密码和上传路径

# 4. 启动
mvn spring-boot:run
# 运行于 http://localhost:8080/api
```

### 前端（222/frontend/）
```bash
npm install
npm run dev
# 运行于 http://localhost:3000
```

### 论文编译（上级目录 NUIST_Bachelor_Thesis_LaTeX_Template/）
```bash
export PATH="D:/texlive/texlive/2026/bin/windows:$PATH"
xelatex -synctex=1 -interaction=nonstopmode NUIST_thesis.tex
bibtex NUIST_thesis
xelatex -synctex=1 -interaction=nonstopmode NUIST_thesis.tex
xelatex -synctex=1 -interaction=nonstopmode NUIST_thesis.tex
```

## 功能模块

### 用户端
- 注册/登录（JWT认证）、个人资料维护、密码修改
- 商品浏览（首页/分类/搜索）、详情查看
- 购物车（加购/改数量/删除/清空）
- 订单（创建/查询/确认收货）
- 收藏（添加/取消/列表）
- 留言（发表/回复）、评价（评分/文本）
- 地址管理

### 管理端
- 数据看板（用户数/商品数/订单数/待审核数）
- 商品审核（通过/拒绝+记录原因）
- 订单管理（发货/确认收货）
- 用户管理

## 关键设计决策

- **业务闭环**：用户入驻→商品发布→内容审核→交易下单→订单履约→评价反馈
- **订单事务**：创建订单+写入订单项+更新商品状态+清空购物车在同一事务中
- **统一响应结构**：`Result<T>` 封装 code/message/data，前端统一拦截
- **商品双状态字段**：`status`（销售）与 `audit_status`（审核）分离
- **逻辑删除**：用户表与商品表使用 `deleted` 字段

## 默认测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户1 | test001 | 123456 |
| 用户2 | test002 | 123456 |

## 注意事项

- 图片上传路径默认为 `D:/campus-secondhand/uploads/`，按需修改
- 当前论文参考文献（20篇）已更新匹配 `bibliography.bib`
- Windows 系统楷体字体名为 `KaiTi` 非 `SimKai`，nuist.cls 已修正
- 论文中引用的 `\cite{x1}` 至 `\cite{x12}` 与 bib 文件键值对应
- VSCode LaTeX Workshop 设置已配置于用户级 `settings.json`
