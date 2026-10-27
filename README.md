## 组织字段支持（Vue / React）

组织字段支持用户、部门、角色、岗位，表单可选多选或单选。字段单列存 `[{"id":"1","name":"张三"}]`（`List<OrgRef>` + `JacksonTypeHandler`），列表直接显示名称，查询按名称模糊匹配，不参与 Excel 导入导出。

配置值、组件用法、接口及后续调整约定见 [组织选择与名称展示约定](docs/org-selection.md)。`config.json` 同时提供 Vue / React 单表、主子表模板组。

## React 分支使用

本分支提供 `React单表增删改查` 和 `React主子表增删改查` 两组模板，目标前端为 `pigx-ui-react`。Java、Mapper 和菜单 SQL 沿用原模板，前端输出 API、`index.tsx` 和 `form.tsx`。两组共享的 React 模板通过既有 `ChildClassName` / `childFieldList` 区分主子表，不增加生成器上下文变量。

1. 将本分支发布到在线模板源的 `react` 分支后，将后端配置 `pigx.codegen.branch` 设为 `react`。默认值已同步修改；外部配置若指定其他分支，需对应调整。
2. 在模板管理中加载新版本，选择对应的 React 模板组。版本号使用独立的 `V20261011-react-3`，避免复用已经入库的 Vue 模板。后续更新模板内容时须递增 `VERSION`。
3. 生成时将前端目录设为 `pigx-ui-react`。确认主键、表单项、列表项、查询项及字段字典；主子表还需设置主表关联字段、子表关联字段及子表自己的主键。
4. 将生成页面注册到管理端的 `src/layout/router-view/workspace.tsx` 页面分发中。现有管理端不会自动扫描新页面：使用 `lazy(() => import('@/views/模块/功能/index'))`，在通用模块分发之前按菜单 `componentPath` 匹配，放入 `Suspense` 渲染。菜单路径仍为 `/模块/功能/index`。
5. 审核生成的菜单 SQL 并设置实际父菜单、租户，再通过项目既有流程导入和授权。

生成页面包含分页、查询/重置、刷新、勾选、批量删除、详情回填、新增及编辑，权限键沿用 `${moduleName}_${functionName}_add/edit/del`。使用项目现有 `api`、`RecordsTable`、`TableToolbar`、`ManagedForm` 和 shadcn 组件，继承主题与 RTL 行为。

表单支持文本、多行、数字、日期、日期时间、字典单选/多选、图片、文件和富文本；布尔字段使用 Switch。必填与生成器提供的格式校验规则保留，主表唯一性校验使用 `/details`；编辑时沿用原模板跳过去重的行为。配置字典类型时使用字典控件；未配置字典时使用文本输入，多选值用逗号分隔，避免请求空字典而阻塞保存。Long 和 BigDecimal 输入保留字符串，避免前端精度丢失。

主子表通过 `${childClassName}List` 保存明细。新明细先保存在表单中；已入库明细删除沿用 `/child` 接口，需删除权限并二次确认，删除立即生效，关闭表单不会撤销。主子表均按各自元数据主键生成，删除主记录时按配置的主表关联字段清理明细。当前模板面向各有一个主键的一主一子关系。

验收时应在实际业务数据源复核 CRUD、菜单授权、上传、字典与浅色/深色及窄屏布局；模板渲染和 TypeScript 检查不能替代这些运行态检查。

## 代码生成原理

当开发者选中某张表进行代码生成时，如下图所示，首先会注入 PIGX 定义好的 Velocity 模板（开发平台 > 模板管理）。然后查询此表的相关元信息（字段、注释）并注入。最终使用 Velocity 渲染出相关文本。

<img src="https://minio.pigx.top/oss/202306/1685858464.jpg" alt="代码生成原理示意图" width="100%" />

## Velocity 模板基础

<Tip title="学习资源">
参考 <a href="https://juejin.cn/post/7034112895277498404" target="_blank">Velocity 基本语法说明</a> 了解模板语法。
</Tip>

Entity 实体生成的模板代码：

```java
public class ${ClassName}Entity extends Model<${ClassName}Entity> {

#foreach ($field in $fieldList)
#if($field.primaryPk)
    @TableId(type = IdType.ASSIGN_ID)
#end
    @Schema(description="$comment"#if($field.hidden),hidden=$field.hidden#end)
    private $field.attrType $field.attrName;
#end
}
```

<img src="https://minio.pigx.top/oss/202306/1685859452.png" alt="模板示例" width="100%" />

## 上下文元信息

如下属性可在 Velocity 模板中直接使用相关语法进行取值。

### 模板基本属性

| Key            | Description        |
| -------------- | ------------------ |
| dbType         | 数据库类型         |
| package        | 包名               |
| packagePath    | 包路径             |
| version        | 版本               |
| moduleName     | 模块名             |
| ModuleName     | 模块名首字母大写   |
| functionName   | 功能名             |
| FunctionName   | 功能名首字母大写   |
| formLayout     | 表单布局           |
| style          | 样式，对应的模板组 |
| author         | 作者               |
| datetime       | 当前日期和时间     |
| date           | 当前日期           |
| importList     | 导入列表           |
| tableName      | 数据库表名         |
| tableComment   | 数据库表注释       |
| className      | 类名的小写形式     |
| ClassName      | 类名               |
| fieldList      | 字段列表           |
| backendPath    | 后端路径           |
| frontendPath   | 前端路径           |
| childFieldList | 子表字段列表       |
| childTableName | 子表名             |
| mainField      | 主表关联字段名称   |
| childField     | 子表关联字段名称   |
| ChildClassName | 子类名首字母大写   |
| childClassName | 子类名的小写形式   |
| primaryList    | 主键字段列表       |
| formList       | 表单字段列表       |
| gridList       | 表格字段列表       |
| queryList      | 查询字段列表       |
| pk             | 主键字段           |

### 模板字段属性

| Key           | Description    |
| ------------- | -------------- |
| dsName        | 数据源名       |
| tableName     | 表名称         |
| fieldName     | SQL 字段名称   |
| fieldType     | SQL 字段类型   |
| attrName      | Java 属性名    |
| attrType      | Java 属性类型  |
| fieldComment  | 字段说明       |
| sort          | 排序           |
| packageName   | 属性包名       |
| autoFill      | 自动填充       |
| primaryPk     | 是否为主键     |
| baseField     | 是否为基类字段 |
| formItem      | 是否为表单项   |
| formRequired  | 表单必填       |
| formType      | 表单类型       |
| formValidator | 表单效验       |
| gridItem      | 是否为列表项   |
| gridSort      | 列表排序       |
| queryItem     | 是否为查询项   |
| queryType     | 查询方式       |
| queryFormType | 查询表单类型   |
| fieldDict     | 字段字典类型   |

### 环境判断属性

| Key           | Description        |
| ------------- | ------------------ |
| isSpringBoot3 | 是否是 springboot3 |
| isTenant      | 是否支持多租户     |

## 左树右表模板约定

新增模板组：`左树右表增删改查`

该模板组用于生成“左侧树节点 + 右侧主表”的标准 CRUD 页面，关系方向为：

- 左侧树使用子表元信息
- 右侧列表和表单使用主表元信息
- 左树内部层级关系使用 `parentField`
- 主表通过 `childField` 指向左树节点主键

### 变量来源

右侧主表继续使用现有主表变量：

- `fieldList`
- `formList`
- `gridList`
- `queryList`
- `pk`
- `ClassName`
- `className`

左侧树继续复用主子表变量：

- `childFieldList`
- `childTableName`
- `ChildClassName`
- `childClassName`

### 新增约定

左树子表还需要额外提供两个字段约定：

- `parentField`：树节点父级字段
- `nameField`：树节点显示名称字段

主表与左树关联继续复用现有变量：

- `childField`：主表中关联左树节点的外键字段

### 页面行为

- 页面初始化时，左树加载全部节点，右表默认展示全部数据
- 选中左树节点后，右表按 `childField` 过滤
- 删除左树节点时，只允许删除没有子节点且没有关联主表数据的空节点
