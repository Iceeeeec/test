# QQ互联登录Demo

这是一个基于Spring Boot的QQ互联第三方登录Demo，展示了如何集成QQ OAuth2.0登录功能。

## 功能特性

- ✅ QQ OAuth2.0授权登录
- ✅ 获取QQ用户基本信息（昵称、头像、性别、地区等）
- ✅ Session管理
- ✅ 现代化响应式UI设计
- ✅ 错误处理和用户友好的提示

## 技术栈

- **后端**: Spring Boot 3.0.2, Java 17
- **前端**: Thymeleaf, HTML5, CSS3, JavaScript
- **HTTP客户端**: Apache HttpClient
- **JSON处理**: Jackson

## 快速开始

### 1. 申请QQ互联应用

1. 访问 [QQ互联开发者平台](https://connect.qq.com/)
2. 注册并创建应用
3. 获取 `App ID` 和 `App Key`
4. 设置回调地址为: `http://localhost:8080/qq/callback`

### 2. 配置应用参数

编辑 `src/main/resources/application.properties` 文件：

```properties
# QQ互联配置（替换为您的真实配置）
qq.app.id=你的AppID
qq.app.key=你的AppKey
qq.redirect.uri=http://localhost:8080/qq/callback
```

### 3. 运行应用

```bash
# 使用Maven运行
mvn spring-boot:run

# 或者编译后运行
mvn clean package
java -jar target/qqDemo-0.0.1-SNAPSHOT.jar
```

### 4. 访问应用

打开浏览器访问: [http://localhost:8080](http://localhost:8080)

## 项目结构

```
src/main/java/com/qqdemo/
├── QqDemoApplication.java          # 主启动类
├── demos/
│   ├── controller/
│   │   ├── HomeController.java     # 首页控制器
│   │   └── QQLoginController.java  # QQ登录控制器
│   └── utils/
│       └── QQConnectUtils.java     # QQ互联工具类
└── resources/
    ├── application.properties      # 应用配置
    └── templates/                  # Thymeleaf模板
        ├── index.html             # 首页
        ├── success.html           # 登录成功页
        └── error.html             # 错误页面
```

## 核心流程

### OAuth2.0认证流程

1. **用户点击QQ登录按钮**
   - 跳转到QQ授权页面
   - 用户同意授权后回调到应用

2. **获取授权码 (code)**
   - QQ回调应用并携带授权码

3. **通过授权码获取访问令牌 (access_token)**
   - 应用使用授权码向QQ服务器请求访问令牌

4. **获取用户OpenID**
   - 使用访问令牌获取用户的唯一标识OpenID

5. **获取用户信息**
   - 使用访问令牌和OpenID获取用户基本信息

### API接口说明

| 路径 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 首页，显示登录状态 |
| `/qq/login` | GET | 发起QQ登录授权 |
| `/qq/callback` | GET | QQ登录回调处理 |
| `/qq/logout` | GET | 退出登录 |

## QQ互联API说明

本Demo使用了以下QQ互联API：

1. **授权登录**
   - URL: `https://graph.qq.com/oauth2.0/authorize`
   - 用于获取用户授权

2. **获取Access Token**
   - URL: `https://graph.qq.com/oauth2.0/token`
   - 用于通过授权码获取访问令牌

3. **获取用户OpenID**
   - URL: `https://graph.qq.com/oauth2.0/me`
   - 用于获取用户唯一标识

4. **获取用户信息**
   - URL: `https://graph.qq.com/user/get_user_info`
   - 用于获取用户基本信息

## 常见问题

### Q: 登录时出现"redirect_uri_mismatch"错误？
A: 请检查QQ开发者平台中配置的回调地址是否与应用中配置的一致。

### Q: 登录时出现"invalid_client"错误？
A: 请检查App ID和App Key是否正确配置。

### Q: 无法获取用户信息？
A: 请确认应用有获取用户信息的权限，并且access_token有效。

### Q: 本地测试如何配置回调地址？
A: 可以使用localhost或127.0.0.1，但生产环境需要使用真实域名。

## 安全注意事项

1. **保护敏感信息**: App Key等敏感信息不要提交到版本控制系统
2. **HTTPS**: 生产环境建议使用HTTPS
3. **状态验证**: 建议在授权时使用state参数防止CSRF攻击
4. **Token管理**: 妥善管理access_token，避免泄露

## 扩展功能

可以基于此Demo扩展以下功能：

- [ ] 用户信息数据库存储
- [ ] 多种第三方登录集成（微信、微博等）
- [ ] JWT Token认证
- [ ] 用户权限管理
- [ ] 登录日志记录

## 参考资料

- [QQ互联官方文档](https://wiki.connect.qq.com/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Thymeleaf官方文档](https://www.thymeleaf.org/)

## 许可证

本项目仅供学习和参考使用。 