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
