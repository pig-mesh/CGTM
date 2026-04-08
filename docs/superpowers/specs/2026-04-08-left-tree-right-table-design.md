# 左树右表代码生成模板设计

- 日期：2026-04-08
- 状态：已确认，待进入 implementation planning
- 适用仓库：`/Users/lengleng/Downloads/CGTM`

## 1. 背景

当前仓库已提供三组模板：

- 单表增删改查
- 主子表增删改查
- 树形表格增删改查

本次需要新增第四组模板，用于生成“左树右表”业务页面。页面形态为：

- 左侧：树形结构，支持完整增删改
- 右侧：主表列表和表单，支持完整增删改
- 左右联动：右表数据按左树节点过滤

该场景与现有主子表最接近，但关系方向不同。这里不是“主表聚合子表列表”，而是“主表记录持有树节点外键”。

## 2. 目标

新增一组独立模板 `左树右表增删改查`，满足以下目标：

1. 生成整套前后端代码，而不是仅生成页面。
2. 左树和右表在同一业务模块下，权限、菜单、路径风格与现有模板保持一致。
3. 左树来源于子表元信息，右表来源于主表元信息。
4. 页面首次进入时右表默认展示全部数据。
5. 选中左树节点后，右表按节点过滤。
6. 删除左树节点时，仅允许删除“没有子节点且没有关联主表数据”的空节点。

## 3. 非目标

本次设计不包含以下内容：

- 不新增第二套独立的树 Controller/Service/ServiceImpl 产物
- 不沿用主子表 `saveDeep/updateDeep` 聚合模型
- 不扩展新的复杂生成器上下文字段体系
- 不处理多级树批量拖拽、排序、懒加载等额外能力

## 4. 采用方案

采用“新增独立模板组，但仍使用同一业务模块命名空间”的方案。

核心思路：

- 新增第四组模板 `左树右表增删改查`
- 主控制器统一挂载在 `/${functionName}` 下
- 主表接口和树节点接口通过不同子路由区分
- 左树使用主子表上下文中的子表元信息，再补充树专用字段约定
- 右表使用现有单表/主表模板的字段能力

不采用“完全拆成两套资源”的原因：

- 会显著偏离当前 `config.json` 和模板目录的组织方式
- 会增加路径、权限、菜单和模板变量的额外复杂度

不采用“直接改造主子表模板”的原因：

- 当前主子表模板的语义是主表聚合子表
- 本需求的真实关系是主表引用树节点
- 强行复用 `saveDeep/updateDeep` 会让前后端职责边界变得混乱

## 5. 数据关系与元信息约定

### 5.1 关系定义

关系采用以下形式：

- 左树对应子表
- 右表对应主表
- 主表中保存树节点外键

即：

- 一个树节点可以关联多条主表记录
- 一条主表记录只属于一个树节点

### 5.2 模板变量来源

右侧主表继续使用现有主表变量：

- `fieldList`
- `formList`
- `gridList`
- `queryList`
- `pk`
- `ClassName`
- `className`

左侧树继续复用现有主子表变量：

- `childFieldList`
- `childTableName`
- `ChildClassName`
- `childClassName`

树专用额外约定：

- `parentField`：左树子表中的父节点字段
- `nameField`：左树子表中的树节点名称字段

### 5.3 跨表关联约定

这一组模板中，跨表关联采用以下固定规则：

- `mainField` 表示主表中的树节点外键字段
- 主表通过 `mainField` 关联到左树子表主键
- 左树子表主键从 `childFieldList` 中 `primaryPk = true` 的字段推导

说明：

- 当前 README 中的 `childField` 定义来自主子表聚合场景
- 在本模板组里，不再以 `childField` 作为跨表删除或聚合写入的核心字段
- 实现阶段如需保留 `childField` 兼容生成器配置，可保留变量，但生成逻辑以“主表外键 -> 子表主键”为准

该约定必须在实现中明确，否则实现者可能误把当前场景继续按“子表保存主表外键”处理。

### 5.4 关键元信息来源表

| 字段 | 来源 | 是否新增输入 | 本模板组中的含义 |
| --- | --- | --- | --- |
| `mainField` | 复用现有主子表配置 | 否 | 主表中的树节点外键字段 |
| `childFieldList` | 复用现有主子表配置 | 否 | 左树子表字段列表 |
| `ChildClassName` / `childClassName` | 复用现有主子表配置 | 否 | 左树子表实体命名 |
| `childTableName` | 复用现有主子表配置 | 否 | 左树子表表名 |
| `parentField` | 新增模板配置 | 是 | 左树子表父节点字段 |
| `nameField` | 新增模板配置 | 是 | 左树节点显示名称字段 |
| 左树子表主键 | 从 `childFieldList` 推导 | 否 | 通过 `primaryPk = true` 的字段识别 |

结论：

- 本模板组需要在生成器配置层新增两个最小输入：`parentField`、`nameField`
- `mainField` 沿用现有主子表配置，但语义固定为“主表引用树节点的外键”
- 不要求新增更复杂的上下文字段体系

## 6. 模板分组与生成产物

新增模板组名称：

- `左树右表增删改查`

建议生成产物如下。

### 6.1 后端主表产物

- `Controller`
- `Service`
- `ServiceImpl`
- `实体`
- `Mapper`
- `Mapper.xml`

### 6.2 后端树表产物

- `子实体`
- `子Mapper`

### 6.3 前端产物

- `api.ts`
- `index.vue`
- `tree-form.vue`
- `form.vue`

### 6.4 通用产物

- `权限菜单.sql`

### 6.5 文件职责

- `index.vue`：左右布局、树节点选中状态、右表查询、双弹窗联动
- `tree-form.vue`：左树节点新增/编辑
- `form.vue`：右表主记录新增/编辑
- `api.ts`：同时暴露主表和树接口

不建议将左右两种编辑能力继续合并到一个表单文件中，否则页面职责会过于拥挤。

### 6.6 `config.json` 注册要求

该仓库是 registry-driven，新增模板组后必须同步更新 `config.json`，否则模板无法出现在代码生成器里。

建议新增一组根配置：

- 分组名：`左树右表增删改查`

建议的文件映射如下：

| templateName | generatorPath | templateFile |
| --- | --- | --- |
| `Controller` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/controller/${ClassName}Controller.java` | `treeTable/Controller.java` |
| `Service` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/service/${ClassName}Service.java` | `treeTable/Service.java` |
| `ServiceImpl` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/service/impl/${ClassName}ServiceImpl.java` | `treeTable/ServiceImpl.java` |
| `实体` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/entity/${ClassName}Entity.java` | `single/实体.java` |
| `Mapper` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/mapper/${ClassName}Mapper.java` | `single/Mapper.java` |
| `Mapper.xml` | `${backendPath}/src/main/resources/mapper/${ClassName}Mapper.xml` | `single/Mapper.xml` |
| `子实体` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/entity/${ChildClassName}Entity.java` | `multiple/子实体.java` |
| `子Mapper` | `${backendPath}/src/main/java/${packagePath}/${moduleName}/mapper/${ChildClassName}Mapper.java` | `multiple/子Mapper.java` |
| `权限菜单` | `${backendPath}/menu/${functionName}_menu.sql` | `common/权限菜单.sql` |
| `api.ts` | `${frontendPath}/src/api/${moduleName}/${functionName}.ts` | `treeTable/api.ts` |
| `表格` | `${frontendPath}/src/views/${moduleName}/${functionName}/index.vue` | `treeTable/index.vue` |
| `树表单` | `${frontendPath}/src/views/${moduleName}/${functionName}/tree-form.vue` | `treeTable/tree-form.vue` |
| `主表单` | `${frontendPath}/src/views/${moduleName}/${functionName}/form.vue` | `treeTable/form.vue` |

说明：

- 新增模板目录建议命名为 `treeTable/`
- 主表实体、Mapper、Mapper.xml 可以直接复用单表模板
- 子实体、子 Mapper 可以直接复用主子表模板
- 需要新建的主要是 `Controller / Service / ServiceImpl / api.ts / index.vue / tree-form.vue / form.vue`

## 7. 后端接口设计

### 7.1 主表接口

主表接口沿用现有单表风格：

- `GET /${functionName}/page`
- `GET /${functionName}/details`
- `POST /${functionName}`
- `PUT /${functionName}`
- `DELETE /${functionName}`
- `GET /${functionName}/export`
- `POST /${functionName}/import`

### 7.2 左树接口

在同一控制器下新增树节点子路由：

- `GET /${functionName}/tree`
- `GET /${functionName}/tree/details`
- `POST /${functionName}/tree`
- `PUT /${functionName}/tree`
- `DELETE /${functionName}/tree`

### 7.3 设计原则

- 同一业务模块共用权限前缀和菜单命名风格
- 通过 `/tree` 子路由区分树节点操作和主表操作
- 避免再拆第二个 Controller，保持与现有模板目录结构一致

## 8. Service 职责设计

`Service` 和 `ServiceImpl` 不采用主子表 `saveDeep/updateDeep/removeDeep` 风格，而是明确承担三类职责：

1. 主表分页和 CRUD
2. 左树节点 CRUD 和树结构构建
3. 删除前关联校验

`ServiceImpl` 需要注入：

- 主表 `Mapper`
- 左树子表 `ChildMapper`

建议至少存在以下方法类别：

- 主表分页查询辅助方法
- 树结构构建方法
- 树节点详情查询方法
- 树节点新增/修改方法
- 树节点删除校验与删除方法

这里的重点不是方法名，而是职责边界要明确，避免把左树和右表重新做成一个聚合保存模型。

## 9. 页面交互与数据流

### 9.1 初始状态

页面初始化时：

1. 加载左树数据
2. 加载右表分页数据
3. 右表默认显示全部数据

### 9.2 树节点联动

当用户选中左树节点后：

1. 记录当前选中节点 ID
2. 将该节点 ID 写入右表查询条件中的 `mainField`
3. 重新加载右表分页数据

当用户取消选中或删除了当前选中节点后：

1. 清空当前树节点选中状态
2. 移除右表查询中的 `mainField`
3. 右表恢复“全部数据”视图

### 9.3 左树 CRUD

左树支持：

- 新增根节点
- 新增子节点
- 编辑节点
- 删除空节点

`tree-form.vue` 的字段来源为 `childFieldList`，但需要：

- 排除树表主键字段
- 排除主表外键聚合概念中的无关字段
- 保留 `parentField` 作为父级节点选择器
- 使用 `nameField` 作为默认树节点显示名称来源
- 根节点父级值统一使用 `0`，与现有树模板的根节点构建方式保持一致
- 编辑节点时禁止选择自己作为父节点
- 编辑节点时禁止选择自己的任意后代节点作为父节点，避免形成环
- 父级节点选择列表需要排除当前节点及其后代

### 9.4 右表 CRUD

右表支持：

- 分页查询
- 条件筛选
- 新增
- 编辑
- 删除

`form.vue` 的字段来源为主表 `formList`。

对于主表中的树节点外键字段 `mainField`：

- 如果当前已选树节点，则新增时默认写入当前树节点 ID
- 如果当前未选树节点，则用户必须手动选择树节点

该字段在前端表现可以是：

- 已选树节点时默认值 + 只读/禁改
- 未选树节点时树选择器

实现阶段二选一即可，但必须保证不会生成缺少树节点归属的主表记录。

## 10. 删除规则与异常处理

### 10.1 左树删除规则

删除左树节点时，必须同时校验：

1. 是否仍存在子节点：`parentField = 当前节点 ID`
2. 是否仍存在主表数据关联：`mainField = 当前节点 ID`

仅当两者都不存在时才允许删除。

### 10.2 用户选择的删除语义

用户确认采用：

- 只允许删除空节点
- 只要存在子树或主表数据都不允许删除

### 10.3 前后端处理方式

后端返回明确业务提示，例如：

- 当前节点存在子节点，不能删除
- 当前节点已关联主表数据，不能删除

前端保持与现有模板一致的错误提示方式：

- 列表加载失败提示
- 详情获取失败提示
- 新增/修改失败提示
- 删除失败提示

不在本次模板设计中扩展统一异常体系。

## 11. 前端文件职责细化

### 11.1 `index.vue`

负责：

- 左右布局
- 树节点数据加载
- 当前节点选中状态
- 右表分页查询
- 工具栏和操作按钮
- 双弹窗打开关闭
- 删除成功后的联动刷新

### 11.2 `tree-form.vue`

负责：

- 新增根节点
- 新增子节点
- 编辑树节点
- 父级节点选择

参考方向接近当前 `tree/树形表单.vue`，但实体来源切换为左树子表。

### 11.3 `form.vue`

负责：

- 主表详情回显
- 主表新增/编辑
- 树节点外键写入和校验

### 11.4 `api.ts`

应同时暴露两组接口：

- 主表：`fetchList/getObj/addObj/putObj/delObjs`
- 左树：`fetchTreeList/getTreeObj/addTreeObj/putTreeObj/delTreeObjs`

## 12. 验证范围

实现完成后，至少应验证以下行为。

### 12.1 初始化

- 左树可正常加载
- 右表默认展示全部数据

### 12.2 联动

- 选中树节点后右表按节点过滤
- 清空节点选中后右表恢复全部数据

### 12.3 左树 CRUD

- 新增根节点成功
- 新增子节点成功
- 编辑树节点成功
- 删除空节点成功
- 有子节点时删除失败
- 有主表关联数据时删除失败

### 12.4 右表 CRUD

- 已选树节点下新增主表记录时自动带入树节点 ID
- 未选树节点时新增主表记录必须手动选择树节点
- 编辑主表记录可正常回显和保存
- 删除主表记录后右表刷新正常

### 12.5 模板变量验证

- 新模板只依赖现有主子表变量和 `parentField/nameField`
- 不再引入新的复杂生成器上下文字段

## 13. 风险与约束

### 13.1 最大风险

当前仓库的主子表语义是“子表持有主表外键”，而本次场景是“主表持有树节点外键”。

如果实现阶段没有显式区分，会导致：

- 错误复用 `saveDeep/updateDeep`
- 错误使用 `childField`
- 错误生成删除逻辑

因此 implementation planning 阶段必须首先确认：

- 哪些现有主子表模板能直接复用
- 哪些变量只保留名称，不保留原语义

### 13.2 范围控制

本次设计只覆盖“标准左树右表 CRUD 模板”，不额外承诺：

- 树节点拖拽排序
- 树懒加载
- 跨节点批量移动主表数据
- 主表明细内嵌子表编辑

## 14. 交付结论

本设计确认新增一组独立模板 `左树右表增删改查`，其实现原则为：

- 结构上参考现有主子表与树形模板
- 语义上明确采用“主表外键指向树节点”的关系
- 页面上生成“左树 + 右表 + 双弹窗”的标准 CRUD 布局
- 删除规则采用“仅可删除空节点”

该设计已经满足进入 implementation planning 的条件。
