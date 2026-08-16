<!--VITE PLUS START-->

# 使用 Vite+，Web 的统一工具链

本项目使用 Vite+——一个构建于 Vite、Rolldown、Vitest、tsdown、Oxlint、Oxfmt 与 Vite Task 之上的统一工具链。Vite+ 将运行时管理、包管理与前端工具封装在名为 `vp` 的全局 CLI 中。Vite+ 与 Vite 不同，它通过 `vp dev` 与 `vp build` 调用 Vite。运行 `vp help` 打印命令列表，运行 `vp <command> --help` 查看某个命令的用法。

文档本地位于 `node_modules/vite-plus/docs`，在线文档见 https://viteplus.dev/guide/。

## 内置命令与脚本

`vp <name>` 运行内置命令；`vp run <name>` 运行 `package.json` 中的脚本或 `vite.config.ts` 中的任务。脚本不能覆盖内置命令，因此 `vp dev` 与 `vp run dev` 的行为可能不同。请先查看 `package.json` 与 `vite.config.ts`；当项目定义了同名脚本或任务时，使用 `vp run <name>` 运行。

## 工具版本

运行 `vp toolchain` 查看当前 Vite+ 版本中各工具的版本与依赖关系；添加工具名可只查看图谱的一部分，例如 `vp toolchain vite`。使用 `--global` 忽略本地 `vite-plus` 包。使用 `vp why <package>` 查看包管理器中的依赖关系图。

## 审查清单

- [ ] 拉取远程变更后、开始工作前运行 `vp install`。
- [ ] 运行 `vp check` 与 `vp test` 进行格式化、lint、类型检查与测试。
- [ ] 检查 `vite.config.ts` 任务或 `package.json` 脚本中是否有必要的验证项，通过 `vp run <script>` 运行。
- [ ] 如果安装、运行时或包管理器行为异常，运行 `vp env doctor` 并在求助时附上其输出。

<!--VITE PLUS END-->

# bkbits-admin 项目说明

基于 Bun + Vite+ + Vue 3 的后台管理系统前端项目（bkbits-admin）。已实现用户、角色、权限、部门、租户、系统参数、系统字典、日志等系统管理功能的完整页面。后端接口暂无数据，全部接口由内置 mock 服务器提供数据。

## 技术栈

- **bun**（1.3.5）— 运行时与包管理器
- **viteplus**（0.2.9）— 统一前端工具链（基于 Vite 8、Rolldown、Vitest、tsdown、Oxlint、Oxfmt）
- **vue**（3.5）— 前端框架
- **vue-router**（5）+ **pinia**（4）— 路由与状态管理
- **typescript**（6）— 类型系统
- **scss** — 样式预处理器
- **antdv-next**（1.5）— UI 组件库（Ant Design Vue 下一代，ant-design-vue v4 同款 API）
- **lucide-vue-next** + **@ant-design/icons-vue** — 图标库
- **axios** — HTTP 客户端

## 接口与 mock

后端接口文档通过 Swagger 提供（共 63 个接口）：

- 资源列表：http://localhost:8088/swagger-resources
- 接口文档（adminApi 分组）：http://localhost:8088/swagger/v2?group=adminApi

当前后端接口暂无数据，项目内置 mock 服务器（`src/mock/`）：在 `vp dev` 与 `vp preview` 下拦截全部 `/api/*` 请求，以内存数据实现 Swagger 全部接口（含鉴权、分页、过滤、CRUD、文件上传）。另含 5 个前端扩展接口（Swagger 未提供、按菜单功能补充）：

| 扩展接口                       | 说明                             |
| ------------------------------ | -------------------------------- |
| `POST /api/register`           | 注册（「注册」菜单）             |
| `GET /api/role/query`          | 角色分页查询（「角色管理」列表） |
| `GET /api/log/login/query`     | 登录日志分页查询                 |
| `GET /api/log/operation/query` | 操作日志分页查询                 |
| `GET /api/notification/list`   | 通知公告（顶栏通知下拉）         |

后端就绪后，删除 `vite.config.ts` 中的 `mockServer()` 插件即可切换为真实接口（`/api` 已配置代理至 http://localhost:8088）。默认账号：`admin` / `123456`。

## API 层

`src/api/`：

- `http.ts` — axios 实例（token 注入、`Result` 解包、统一错误提示）
- `define.ts` — 接口定义四件套：
  - `defineQuery(url)` — GET 查询（query 参数），用于分页/列表接口
  - `defineGet(url)` — GET 获取，用于按编号/键查询接口
  - `definePost(url)` — POST JSON 提交（新增/修改/删除）
  - `defineMultipart(url)` — POST multipart/form-data（文件上传）
- `types.ts` — 由 Swagger 自动生成的类型（`Result<T>` / `PageResult<T>` / 全部实体与 DTO）
- `modules/` — 按业务模块组织的接口定义（auth/user/role/permission/dept/tenant/param/dict/dataPermission/file/log/notification）

统一响应包装：

```ts
interface Result<T> {
  ok: boolean;
  code: number;
  message?: string;
  data?: T;
}
type PageResult<T> = Result<{ total: number; rows: T[] }>;
```

## 目录结构

```
src/
├── api/       # HTTP 封装、define 四件套、类型、接口模块
├── mock/      # mock 服务器（Vite 插件，内存实现全部接口）
├── layouts/   # AdminLayout：侧边栏 + 顶栏 + 标签页栏 + 内容区 + 脚注
├── views/     # 页面（login/register/dashboard/profile/system/*）
├── router/    # 路由与登录守卫
├── stores/    # 状态（auth 认证 / app 主题折叠 / tabs 标签页栏）
├── utils/     # 字典映射工具
└── styles/    # 全局样式
```

## 布局

整体页面结构如下：

- **左侧边栏**
  - logo + 标题
  - 菜单（数据来自 `/api/permission/list`，按权限表动态生成；选中菜单时自动展开上级目录）
- **顶部标题栏**
  - 侧边栏收起/展开按钮
  - 面包屑
  - 搜索栏（菜单搜索）
  - 主题切换按钮（明/暗）
  - 通知下拉按钮
  - 用户信息下拉（用户名、头像、个人信息、修改密码、退出登录）
- **标签页栏**（标题栏下方）：访问过的页面生成标签页，可切换/关闭，支持「关闭其他」「关闭全部」，首页标签固定，会话内持久化
- **内容区域**
- **脚注**

## 菜单功能

全部页面已实现：

- 登录（默认账号 `admin` / `123456`）
- 注册
- 个人中心
  - 仪表盘
  - 个人信息
- 系统管理
  - 用户管理（含绑定角色、重置密码）
  - 在线用户
  - 角色管理（含绑定权限、绑定数据权限）
  - 权限管理
  - 部门管理
  - 租户管理
  - 系统参数
  - 系统字典（含字典值管理）
  - 登录日志
  - 操作日志

## 常用命令

- `vp install` — 安装依赖
- `vp dev` — 启动开发服务器（含 mock）
- `vp check`（`--fix`）— 格式化 + lint + 类型检查
- `vp build` — 生产构建
