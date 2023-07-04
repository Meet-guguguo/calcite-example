### 一 schema和table
#### 1. catalog中包含了schema，用于提供元数据信息，供验证和生成逻辑计划使用

| 结构       | 描述                                                                    |
| ---------- | ----------------------------------------------------------------------- |
| catalog    | 定义了元数据信息和namespace                                             |
| schema     | 同sql中的数据库,一个schema可以包括多个子schema                                     |
| table      | 同sql中的table                                                          |
| adapters   | 整合不同数据源以实现通用访问<br>由model，schema和schema factory组成 |

#### 2. catalog和rootschema