# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

CGTM（代码生成模板市场）是基于 Apache Velocity 的代码生成模板仓库，为 PIGX 框架提供后端（Java/Spring Boot）和前端（Vue 3/TypeScript）的 CRUD 代码生成模板。本仓库不包含可构建的应用代码，仅包含 Velocity 模板文件（`.java`, `.vue`, `.ts`, `.xml`, `.sql`）。

许可证：AGPL 3.0，仅对 PIGX 用户免费。

## 仓库结构

```
single/       # 单表增删改查模板（Controller, Service, Mapper, Entity, 表格.vue, 表单.vue）
multiple/     # 主子表增删改查模板（主子Controller, 主子Service, 主/子实体, 主子表格/表单.vue）
common/       # 共享模板（api.ts, 权限菜单.sql, i18n模板, vform）
config.json   # 模板组配置 — 定义模板名称、源文件路径和生成输出路径的映射
```

## 无构建/测试命令

本仓库是纯模板仓库，没有 package.json、pom.xml 或任何构建系统。无需运行 build、lint 或 test 命令。修改后直接 git commit 即可。

## 模板引擎：Velocity 语法

- 变量：`$ClassName`、`${field.attrName}`
- 指令：`#foreach ($field in $fieldList)` ... `#end`、`#if($condition)` ... `#end`、`#set($var = value)`
- 注释：`##` 行注释
- 工具对象：`$str`（字符串工具）、`$dateTool`（日期工具）、`$math`（数学工具）

## 关键上下文变量

模板在代码生成时可使用以下变量：

**路径与模块：** `backendPath`, `frontendPath`, `packagePath`, `moduleName`, `ModuleName`（首字母大写）, `functionName`, `FunctionName`（首字母大写）

**类与表：** `ClassName`/`className`, `tableName`, `tableComment`, `pk`（主键字段）

**包与版本：** `package`（包名）, `version`（版本）, `importList`（导入列表）, `author`（作者）, `datetime`（生成日期时间）, `date`（生成日期）

**样式与布局：** `formLayout`（表单布局，1=单列/2=双列）, `style`（模板组样式）, `dbType`（数据库类型）

**字段列表：** `fieldList`（全部字段）、`primaryList`（主键字段列表）、`formList`（表单字段列表）、`gridList`（表格列字段列表）、`queryList`（查询字段列表）

**字段属性：** `fieldName`（SQL列名）、`fieldType`（SQL字段类型）、`attrName`/`attrType`（Java属性名/类型）、`fieldComment`（字段注释）、`packageName`（属性包名）、`sort`（排序）、`primaryPk`（是否主键）、`autoFill`（自动填充，INSERT/INSERT_UPDATE/UPDATE）、`baseField`（是否基类字段）、`formItem`（是否表单项）、`formRequired`（表单必填）、`formType`（表单类型，text/textarea/select/radio/checkbox/date/datetime/daterange/datetimerange/number/upload-file/upload-img/editor）、`formValidator`（表单校验器）、`gridItem`（是否列表项）、`gridSort`（列表可排序）、`queryItem`（是否查询项）、`queryType`（查询方式，=/like/!=/>/</>=/<=等）、`queryFormType`（查询表单类型，select/date/daterange/datetime/datetimerange）、`fieldDict`（字典类型）、`hidden`（是否隐藏，用于@Schema）、`dsName`（数据源名）

**主子表专用：** `childFieldList`、`childTableName`、`mainField`、`childField`、`ChildClassName`/`childClassName`

**环境标志：** `isSpringBoot3`（Spring Boot 3.x 兼容）、`isTenant`（多租户支持）、`opensource`（开源版，影响Excel插件import路径）

## 架构模式

### 后端（Java）生成层次
Controller（REST + 权限 + 审计日志）→ Service 接口 → ServiceImpl → Mapper（MyBatis-Plus）→ Entity（Active Record 模式）

关键特性：
- Entity 继承 `Model<T>`，使用 `@TableId(ASSIGN_ID)`、`@TableField(fill=...)`、`@TableLogic`
- Controller 使用 `@PreAuthorize("@pms.hasPermission(...)")`、`@SysLog`、`@ResponseExcel`/`@RequestExcel`
- 主子表 ServiceImpl 使用 `MPJDeepService` 处理关联查询，`@Transactional` 管理事务
- 多租户通过 `@TenantTable` 注解条件启用

### 前端（Vue 3）生成结构
表格组件（index.vue，数据展示+查询+导出）+ 表单组件（form.vue，Dialog 新增/编辑）+ API 客户端（api.ts）

关键特性：
- `<script setup lang="ts">` + Element Plus 组件
- 自定义 hooks：`useTable`、`useDict`、`useMessage`
- 权限指令：`v-auth`
- 支持字典绑定（select/radio/checkbox）、文件上传、富文本编辑器

## config.json 结构

config.json 定义两个模板组，每项包含：
- `templateName`：模板显示名称
- `generatorPath`：生成输出路径（使用 `${backendPath}`、`${packagePath}` 等变量）
- `templateFile`：模板源文件相对路径

修改或新增模板时，必须同步更新 config.json 中对应的映射关系。

## 编辑模板注意事项

- Velocity 指令（`#if`、`#foreach` 等）必须正确闭合 `#end`
- `$field.xxx` 引用的属性必须是上下文变量中存在的字段属性
- 注意 `isSpringBoot3` 条件分支：Spring Boot 2.x 和 3.x 的 import 路径不同（如 `javax.validation` vs `jakarta.validation`）
- 主子表模板中 `childFieldList` 和 `fieldList` 是不同的列表，分别对应子表和主表字段
- 文件名支持中文（如 `表格.vue`、`主子表单.vue`），这是项目约定，不要重命名
