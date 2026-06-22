# energy-carbon-platform

世通AI+能碳管理平台 - 后端项目

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | Java应用开发框架 |
| MyBatis Plus | 3.5.5 | ORM框架，简化数据库操作 |
| Spring Security | 2.7.x | 安全认证与授权框架 |
| JWT (jjwt) | 0.11.5 | JSON Web Token实现 |
| MySQL | 8.0.33 | 关系型数据库 |
| Redis | - | 缓存与会话存储 |
| Fastjson2 | 2.0.43 | JSON序列化/反序列化 |
| Knife4j | 4.4.0 | OpenAPI 3接口文档（基于Swagger） |
| Lombok | - | 简化Java代码 |
| Maven | >= 3.6 | 项目构建工具 |

---

## 模块划分

项目采用 **Maven多模块** 架构，共7个模块：

| 模块名 | 说明 | 职责 |
|--------|------|------|
| `energy-carbon-admin` | 启动模块 | 聚合所有业务模块，包含应用启动类、全局配置、数据库初始化脚本、代码生成器 |
| `energy-carbon-common` | 公共模块 | 工具类、常量定义、全局异常处理、通用实体、结果封装、分页封装 |
| `energy-carbon-system` | 系统管理模块 | 用户管理、角色管理、菜单管理、部门/用能单元管理、字典管理、操作日志、AI服务（知识库、报告生成、服务预约） |
| `energy-carbon-monitor` | 在线监测模块 | 实时数据采集、历史数据查询、设备对标、时间对标、计量抄表、状态监测、能效分析、能流分析 |
| `energy-carbon-alarm` | 报警管理模块 | 报警阈值配置、报警记录、报警处理流程 |
| `energy-carbon-carbon` | 碳排放管理模块 | 碳排放核算、排放因子库、产品碳足迹、CBAM核算、供应链管理、碳资产管理、碳核查支撑、碳核算建模、碳足迹建模 |
| `energy-carbon-report` | 统计报表模块 | 能耗统计表、能耗汇总表、节能量统计表、用能成本统计表、指标录入、产量录入 |

### 模块依赖关系

```
energy-carbon-admin (启动模块)
    ├── energy-carbon-common (公共模块)
    ├── energy-carbon-system (系统管理)
    ├── energy-carbon-monitor (在线监测)
    ├── energy-carbon-alarm (报警管理)
    ├── energy-carbon-carbon (碳排放管理)
    └── energy-carbon-report (统计报表)

所有业务模块均依赖 energy-carbon-common
```

---

## 目录结构

```
energy-carbon-platform/
├── pom.xml                              # 父POM，统一管理依赖版本
├── .gitignore
│
├── energy-carbon-admin/                 # 启动模块
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/seatone/admin/
│       │   │       ├── EnergyCarbonApplication.java    # 应用启动类
│       │   │       └── generator/
│       │   │           └── CodeGenerator.java           # MyBatis Plus代码生成器
│       │   └── resources/
│       │       ├── application.yml      # 应用主配置
│       │       ├── logback-spring.xml   # 日志配置
│       │       └── db/
│       │           ├── schema.sql       # 数据库表结构初始化脚本
│       │           └── data.sql         # 基础数据初始化脚本
│       └── test/                        # 单元测试
│
├── energy-carbon-common/                # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/seatone/common/
│       ├── constant/                    # 常量定义
│       ├── exception/                   # 全局异常
│       ├── result/                      # 统一响应结果封装
│       ├── page/                        # 分页封装
│       └── util/                        # 工具类
│
├── energy-carbon-system/                # 系统管理模块
│   ├── pom.xml
│   └── src/main/java/com/seatone/system/
│       ├── controller/                  # 控制器层
│       ├── service/                     # 服务层
│       ├── service/impl/                # 服务实现层
│       ├── mapper/                      # 数据访问层
│       ├── entity/                      # 实体类
│       ├── dto/                         # 数据传输对象
│       ├── vo/                          # 视图对象
│       ├── config/                      # 模块配置（CORS、Security）
│       ├── security/                    # JWT认证过滤器
│       └── cache/                       # 缓存服务
│
├── energy-carbon-monitor/               # 在线监测模块
│   ├── pom.xml
│   └── src/main/java/com/seatone/monitor/
│       ├── controller/
│       ├── service/
│       ├── mapper/
│       └── entity/
│
├── energy-carbon-alarm/                 # 报警管理模块
│   ├── pom.xml
│   └── src/main/java/com/seatone/alarm/
│       ├── controller/
│       ├── service/
│       ├── mapper/
│       └── entity/
│
├── energy-carbon-carbon/                # 碳排放管理模块
│   ├── pom.xml
│   └── src/main/java/com/seatone/carbon/
│       ├── controller/
│       ├── service/
│       ├── mapper/
│       └── entity/
│
└── energy-carbon-report/                # 统计报表模块
    ├── pom.xml
    └── src/main/java/com/seatone/report/
        ├── controller/
        ├── service/
        ├── mapper/
        └── entity/
```

---

## 开发环境搭建

### 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 1.8 |
| Maven | >= 3.6 |
| MySQL | 8.0.x |
| Redis | >= 5.0 |

### 安装依赖并编译

```bash
cd energy-carbon-platform
mvn clean install
```

### 初始化数据库

```bash
# 1. 创建数据库
create database energy_carbon character set utf8mb4 collate utf8mb4_unicode_ci;

# 2. 执行表结构初始化脚本
mysql -u root -p energy_carbon < energy-carbon-admin/src/main/resources/db/schema.sql

# 3. 执行基础数据初始化脚本
mysql -u root -p energy_carbon < energy-carbon-admin/src/main/resources/db/data.sql
```

### 启动应用

```bash
# 方式一：使用Maven插件启动
mvn spring-boot:run -pl energy-carbon-admin

# 方式二：先打包再运行
mvn clean package
java -jar energy-carbon-admin/target/energy-carbon-admin-1.0.0.jar
```

应用默认运行在 `http://localhost:8080`

---

## 编译命令

| 命令 | 说明 |
|------|------|
| `mvn clean install` | 清理并编译安装所有模块到本地仓库 |
| `mvn clean package` | 清理并打包（生成可执行JAR） |
| `mvn spring-boot:run -pl energy-carbon-admin` | 启动应用 |
| `mvn test` | 运行所有单元测试 |
| `mvn test -pl energy-carbon-admin` | 运行指定模块的单元测试 |
| `mvn clean install -DskipTests` | 跳过测试编译安装 |

---

## 数据库配置

数据库配置位于 `energy-carbon-admin/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/energy_carbon?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
```

### 主要数据表

| 表名 | 说明 |
|------|------|
| `sys_user` | 系统用户表 |
| `sys_role` | 角色表 |
| `sys_menu` | 菜单/权限表 |
| `sys_dept` | 部门/用能单元表 |
| `sys_role_menu` | 角色菜单关联表 |
| `sys_user_role` | 用户角色关联表 |
| `sys_oper_log` | 操作日志表 |
| `sys_dict_type` / `sys_dict_data` | 字典类型/数据表 |
| `energy_device` | 用能设备表 |
| `energy_meter` | 计量器具表 |
| `energy_type` | 能源品种表 |
| `monitor_realtime_data` | 实时数据表 |
| `monitor_history_data` | 历史数据表 |
| `alarm_threshold` | 报警阈值表 |
| `alarm_record` | 报警记录表 |
| `carbon_accounting` | 碳排放核算表 |
| `carbon_emission_factor` | 排放因子表 |
| `carbon_footprint` | 产品碳足迹表 |
| `cbam_declaration` | CBAM申报表 |
| `supply_chain_carbon` | 供应链碳足迹表 |
| `carbon_asset` | 碳资产表 |
| `report_energy_summary` | 能耗汇总表 |
| `ai_service_record` | AI服务预约记录表 |
| `ai_knowledge_doc` | AI知识库文档表 |
| `ai_report_task` | AI报告生成任务表 |

### MyBatis Plus 配置

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.seatone.**.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

---

## 接口文档访问地址

项目集成 **Knife4j** 生成OpenAPI 3接口文档。

启动应用后，可通过以下地址访问：

- **Knife4j增强文档**：`http://localhost:8080/doc.html`
- **Swagger UI**：`http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**：`http://localhost:8080/v3/api-docs`

### 接口规范

- 统一响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- 分页响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

## 代码规范

### 包命名规范

```
com.seatone.{模块名}.{层}
```

例如：
- `com.seatone.system.controller` - 系统管理模块控制器
- `com.seatone.monitor.service` - 在线监测模块服务层
- `com.seatone.carbon.mapper` - 碳排放管理模块数据访问层

### 类命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 实体类 | `表名对应驼峰` | `SysUser`, `EnergyMeter` |
| DTO | `业务操作+DTO` | `LoginDTO`, `UserDTO` |
| VO | `业务操作+VO` | `UserVO`, `MenuVO` |
| Controller | `模块名+Controller` | `SysUserController` |
| Service | `模块名+Service` / `Impl` | `SysUserService`, `SysUserServiceImpl` |
| Mapper | `模块名+Mapper` | `SysUserMapper` |

### 编码规范

1. **使用Lombok简化代码**：实体类使用 `@Data`、`@Builder` 等注解
2. **统一使用Wrapper条件构造器**：优先使用MyBatis Plus的 `QueryWrapper` / `LambdaQueryWrapper`
3. **Service层返回统一结果**：Controller层统一返回 `Result<T>` 封装对象
4. **异常统一处理**：业务异常使用自定义异常类，全局异常处理器统一捕获
5. **日志规范**：使用SLF4J/Logback，避免使用 `System.out.println`

### 示例代码

```java
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUser> getUserPage(UserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getUsername()), SysUser::getUsername, query.getUsername())
               .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
               .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        save(user);
    }
}
```

---

## 单元测试运行

### 测试框架

- **JUnit 5** (Spring Boot 2.7默认集成)
- **Spring Boot Test** - 提供 `@SpringBootTest` 等注解
- **AssertJ** - 流式断言库

### 测试目录结构

```
energy-carbon-admin/src/test/java/com/seatone/
├── BaseTest.java                        # 测试基类
├── system/service/
│   ├── SysUserServiceTest.java
│   ├── SysRoleServiceTest.java
│   └── SysMenuServiceTest.java
├── alarm/service/
│   └── AlarmThresholdServiceTest.java
├── carbon/service/
│   └── CarbonAccountingServiceTest.java
└── monitor/service/
    └── AnalysisConsumptionServiceTest.java
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定模块测试
mvn test -pl energy-carbon-admin

# 运行指定测试类
mvn test -pl energy-carbon-admin -Dtest=SysUserServiceTest

# 运行指定测试方法
mvn test -pl energy-carbon-admin -Dtest=SysUserServiceTest#testCreateUser
```

### 测试基类示例

```java
@SpringBootTest(classes = EnergyCarbonApplication.class)
@AutoConfigureMockMvc
@Transactional
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
```

### Service层测试示例

```java
public class SysUserServiceTest extends BaseTest {

    @Autowired
    private SysUserService userService;

    @Test
    @DisplayName("创建用户-成功")
    void testCreateUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setNickname("测试用户");

        assertThatNoException().isThrownBy(() -> userService.createUser(dto));

        SysUser user = userService.getOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "testuser")
        );
        assertThat(user).isNotNull();
        assertThat(user.getNickname()).isEqualTo("测试用户");
    }
}
```

---

## 安全配置

### JWT配置

```yaml
jwt:
  secret: seatone-energy-carbon-platform-secret-key-2026
  access-expire: 7200000        # Access Token有效期：2小时（毫秒）
  refresh-expire: 604800000     # Refresh Token有效期：7天（毫秒）
```

### 安全过滤器链

`SecurityConfig` 中配置：
- 登录/注册接口允许匿名访问
- 静态资源允许匿名访问
- 其他请求需要JWT认证
- 启用CORS跨域支持

### 权限控制

- **菜单权限**：基于 `sys_menu` 表的 `perms` 字段，格式如 `system:user:list`
- **数据权限**：通过AOP拦截，基于部门层级控制数据可见范围

---

## 日志配置

日志配置位于 `energy-carbon-admin/src/main/resources/logback-spring.xml`。

默认日志级别：
- `com.seatone`：DEBUG
- `org.springframework.security`：DEBUG
- 其他：INFO

日志输出位置：
- 控制台：彩色格式化输出
- 文件：`logs/energy-carbon-platform.log`（按日期滚动）

---

## 代码生成器

项目内置 MyBatis Plus 代码生成器，位于：

```
energy-carbon-admin/src/main/java/com/seatone/admin/generator/CodeGenerator.java
```

使用方法：

```bash
# 1. 修改CodeGenerator中的数据库连接信息和表名
# 2. 运行代码生成器
mvn exec:java -pl energy-carbon-admin -Dexec.mainClass="com.seatone.admin.generator.CodeGenerator"
```

生成内容包括：
- Entity 实体类
- Mapper 接口
- Mapper XML 文件
- Service 接口及实现类
- Controller 控制器

---

**文档版本**：V1.0  
**最后更新**：2026-06-22
