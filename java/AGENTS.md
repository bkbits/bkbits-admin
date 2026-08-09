# bkbits 管理系统

- 仓库使用腾讯仓库
- 顶层pom.xml给予solon 3.x最新版本的parent
- 全局使用最新版本 lombok，scope使用provided
- 创建子模块，层次结构按下列说明创建
- jdk使用21版本
- 文件编码使用UTF-8
- 使用mapstruct进行dto转换
- 添加以下处理器

```xml
<annotationProcessorPaths>
<!-- Lombok 必须在 MapStruct 之前 -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
    </path>
    <!-- Lombok 与 MapStruct 绑定器 -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>${lombok-mapstruct.version}</version>
    </path>
    <path>
        <groupId>com.easy-query</groupId>
        <artifactId>sql-processor</artifactId>
        <version>${easy-query.version}</version>
    </path>
    <path>
        <groupId>org.noear</groupId>
        <artifactId>solon-configuration-processor</artifactId>
        <version>${solon.version}</version>
    </path>
</annotationProcessorPaths>
```

- 添加以下插件统一所有核心模块版本号(版本号使用 `${revision}` )

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>${flatten-maven-plugin.version}</version>
    <configuration>
        <updatePomFile>true</updatePomFile>
        <flattenMode>resolveCiFriendliesOnly</flattenMode>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten.clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 子模块

包前缀为 com.bkbits，根据模块名添加包路径，比如bkbits-core为 com.bkbits.core

- bkbits-framework 基础框架聚合（parent），不包含任何代码，只聚合子模块
  - bkbits-base 基础框架依赖聚合（pom），不包含任何代码，供其他工程一键引入全部基础模块
  - bkbits-core 核心基础库，包含一些自定义配置等
  - bkbits-utils 工具类实现
  - bkbits-auth 认证配置，依赖 `sa-token-solon-plugin`
  - bkbits-validation 验证配置，依赖 `solon-security-validation`
  - bkbits-security 安全配置，依赖 `solon-security-web`
  - bkbits-json JSON配置，依赖 `solon-serialization-jackson3`
  - bkbits-cache cache配置，依赖 `solon-cache-jedis`
  - bkbits-logging 日志配置，依赖 `solon-logging-logback`
  - bkbits-orm orm配置，以来 `com.easy-query`组的 `sql-solon-plugin`, `sql-processor`
  - bkbits-datasource datasource配置，依赖 `com.zaxxer` 组的 `HikariCP`
  - bkbits-docs 文档配置，依赖 `solon-docs-openapi3`与 `solon-openapi2-knife4j`
  - bkbits-scheduling 定时任务配置，依赖 `solon-scheduling-quartz`
  - bkbits-health 健康检查配置，依赖 `solon-health-detector`
  - bkbits-upload 上传配置
- bkbits-tests 测试模块，所有api测试均放在此处
- bkbits-dbo 数据对象模块，所有实体类、枚举常量应该放在此处
- bkbits-admin 后台业务模块
- bkbits-starter 启动类所在模块

---

# 直接依赖清单

> 记录所有直接依赖（不含传递依赖）的名称与版本号，随 pom 变更同步更新。

## 版本基线（顶层 pom properties）

| 属性 | 版本 |
| --- | --- |
| revision | 0.0.1-SNAPSHOT |
| solon.version（solon-parent） | 3.10.7 |
| lombok.version | 1.18.46 |
| mapstruct.version | 1.6.3 |
| lombok-mapstruct.version | 0.2.0 |
| easy-query.version | 3.2.14 |
| sa-token.version | 1.45.0 |
| hikaricp.version | 7.1.0 |
| flatten-maven-plugin.version | 1.8.0 |

插件版本：`maven-compiler-plugin` 3.14.0（继承 solon-parent），`flatten-maven-plugin` 1.8.0。

## 全局依赖（顶层 pom，所有模块继承）

| groupId | artifactId | 版本 | scope |
| --- | --- | --- | --- |
| org.projectlombok | lombok | 1.18.46 | provided |
| org.mapstruct | mapstruct | 1.6.3 | compile |
| org.mapstruct | mapstruct-processor | 1.6.3 | provided |
| org.projectlombok | lombok-mapstruct-binding | 0.2.0 | provided |
| com.easy-query | sql-processor | 3.2.14 | provided |

> `mapstruct-processor`、`lombok-mapstruct-binding`、`sql-processor` 与 `annotationProcessorPaths` 保持一致，声明为 provided 以兼容 IDEA。

## 注解处理器（annotationProcessorPaths）

| groupId | artifactId | 版本 |
| --- | --- | --- |
| org.projectlombok | lombok | 1.18.46 |
| org.mapstruct | mapstruct-processor | 1.6.3 |
| org.projectlombok | lombok-mapstruct-binding | 0.2.0 |
| com.easy-query | sql-processor | 3.2.14 |
| org.noear | solon-configuration-processor | 3.10.7 | 为 @BindProps 生成配置元信息 |

## 各模块直接依赖

| 模块 | groupId:artifactId | 版本 | 备注 |
| --- | --- | --- | --- |
| bkbits-core | org.noear:solon | 3.10.7 | |
| bkbits-core | org.noear:solon-configuration-processor | 3.10.7 | provided，为 @BindProps 生成配置元信息 |
| bkbits-auth | org.noear:solon | 3.10.7 | |
| bkbits-auth | cn.dev33:sa-token-solon-plugin | 1.45.0 | |
| bkbits-auth | org.noear:solon-configuration-processor | 3.10.7 | provided，为 @BindProps 生成配置元信息 |
| bkbits-validation | org.noear:solon-security-validation | 3.10.7 | |
| bkbits-security | org.noear:solon-security-web | 3.10.7 | |
| bkbits-json | org.noear:solon-serialization-jackson3 | 3.10.7 | |
| bkbits-cache | org.noear:solon-cache-jedis | 3.10.7 | |
| bkbits-logging | org.noear:solon-logging-logback | 3.10.7 | |
| bkbits-orm | org.noear:solon | 3.10.7 | |
| bkbits-orm | com.easy-query:sql-solon-plugin | 3.2.14 | |
| bkbits-datasource | org.noear:solon-data | 3.10.7 | |
| bkbits-datasource | com.zaxxer:HikariCP | 7.1.0 | |
| bkbits-docs | org.noear:solon-docs-openapi3 | 3.10.7 | |
| bkbits-docs | org.noear:solon-openapi2-knife4j | 3.10.7 | |
| bkbits-scheduling | org.noear:solon-scheduling-quartz | 3.10.7 | |
| bkbits-health | org.noear:solon-health-detector | 3.10.7 | |
| bkbits-upload | org.noear:solon-web | 3.10.7 | |
| bkbits-upload | com.bkbits:bkbits-core | 0.0.1-SNAPSHOT | |
| bkbits-upload | com.bkbits:bkbits-auth | 0.0.1-SNAPSHOT | 取登录用户作 createBy |
| bkbits-upload | com.bkbits:bkbits-orm | 0.0.1-SNAPSHOT | easy-query 操作 |
| bkbits-upload | org.noear:solon-docs-openapi2 | 3.10.7 | swagger2 文档注解 |
| bkbits-admin | com.bkbits:bkbits-dbo 等 14 个内部模块 | 0.0.1-SNAPSHOT | 见 admin pom |
| bkbits-admin | org.noear:solon-web | 3.10.7 | |
| bkbits-tests | com.bkbits:bkbits-admin | 0.0.1-SNAPSHOT | |
| bkbits-tests | org.noear:solon-test-junit5 | 3.10.7 | test |
| bkbits-starter | com.bkbits:bkbits-admin | 0.0.1-SNAPSHOT | |
| bkbits-starter | com.bkbits:bkbits-dbo | 0.0.1-SNAPSHOT | |
| bkbits-starter | org.noear:solon-web | 3.10.7 | |
| bkbits-starter | org.noear:solon-server-jdkhttp | 3.10.7 | |

## 版本管理说明

- solon 生态（org.noear 组）版本统一由 `solon-parent` 3.10.7 的 dependencyManagement 管理
- `sa-token-solon-plugin`、`HikariCP`、`sql-solon-plugin`、`sql-processor` 由顶层 pom dependencyManagement 统一管理
- `sql-processor` 不再在 bkbits-orm 单独声明，由全局 provided 提供
- 内部模块版本统一使用 `${revision}`，由 flatten-maven-plugin 解析
- `solon-validation` 在 solon 3.x 已更名 `solon-security-validation`
