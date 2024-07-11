#!/bin/bash

# 数据库配置信息
DB_HOST="localhost"
DB_USER="root"
DB_PASS="root"
DB_NAME="pigxx_boot"


# 查询SQL
QUERY="SELECT template_name, template_code FROM gen_template"

# 创建存储模板文件的目录
mkdir -p ./temps

# 执行查询并处理结果
mysql -h $DB_HOST -u $DB_USER -p$DB_PASS -D $DB_NAME -e "$QUERY" -B -N | while IFS=$'\t' read -r template_name template_code; do
    # 创建以template_name命名的文件，并将转义后的template_code写入文件
    echo -e "$template_code" > "./temps/$template_name"
done
