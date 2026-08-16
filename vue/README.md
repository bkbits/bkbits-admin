# bkbits-admin 前端

基于 Bun + Vite+ + Vue 3 + antdv-next 的后台管理系统前端，实现用户、角色、权限、部门、租户、参数、字典、日志等系统管理功能。

## 技术栈

- bun（运行时与包管理）、viteplus（统一工具链）
- vue3 + typescript + scss
- antdv-next（UI）、lucide-vue-next / @ant-design/icons-vue（图标）
- axios + pinia + vue-router

## 接口说明

后端接口文档（Swagger）：`http://localhost:8088/swagger/v2?group=adminApi`（共 63 个接口）。

当前后端接口暂无数据，项目内置 **mock 服务器**（`src/mock/`），在 `vp dev` / `vp preview` 下拦截全部 `/api/*` 请求并以内存数据实现后端全部接口，另含少量前端扩展接口（后端 Swagger 未提供、按文档菜单补充）：

| 扩展接口                       | 说明                                      |
| ------------------------------ | ----------------------------------------- |
| `POST /api/register`           | 注册（AGENTS.md「注册」菜单）             |
| `GET /api/role/query`          | 角色分页查询（AGENTS.md「角色管理」列表） |
| `GET /api/log/login/query`     | 登录日志分页查询                          |
| `GET /api/log/operation/query` | 操作日志分页查询                          |
| `GET /api/notification/list`   | 通知公告（顶栏通知下拉）                  |

后端就绪后，删除 `vite.config.ts` 中的 `mockServer()` 插件即可切换为真实接口（`/api` 已配置代理到 `http://localhost:8088`）。

## API 层约定

`src/api/`：

- `http.ts`：axios 实例（token 注入 / 统一错误提示 / Result 解包）
- `define.ts`：接口定义四件套
  - `defineQuery(url)` —— GET 查询（query 参数），用于分页/列表接口
  - `defineGet(url)` —— GET 获取，用于按编号/键查询
  - `definePost(url)` —— POST JSON 提交（新增/修改/删除）
  - `defineMultipart(url)` —— POST multipart/form-data（文件上传）
- `types.ts`：由 Swagger 自动生成的类型（`Result<T>` / `PageResult<T>` / 全部实体与 DTO）
- `modules/`：按业务模块组织的接口（auth/user/role/permission/dept/tenant/param/dict/dataPermission/file/log/notification）

统一响应包装：

```ts
interface Result<T> {
  ok: boolean;
  code: number;
  message: string;
  data: T;
}
type PageResult<T> = Result<{ total: number; rows: T[] }>;
```

## 页面结构

- 布局：左侧边栏（logo + 动态菜单，菜单数据来自 `/api/permission/list`）、顶栏（折叠/面包屑/菜单搜索/主题切换/通知/用户下拉）、内容区、脚注
- 登录 / 注册
- 个人中心：仪表盘、个人信息
- 系统管理：用户管理、在线用户、角色管理（含绑定权限/数据权限）、权限管理、部门管理、租户管理、系统参数、系统字典（含字典值）、登录日志、操作日志

## 命令

```bash
vp install   # 安装依赖
vp dev       # 启动开发服务器（含 mock）
vp check     # 格式化 + lint + 类型检查
vp build     # 生产构建
```

默认账号：`admin` / `123456`（mock 数据）
