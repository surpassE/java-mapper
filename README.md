# java-mapper
数据库表反射成实体类<br/>
通过配置Config.properties将指定的数据库表反射成实体类、xml中的insert、select、update语句<br/>

# 配置详细说明
全局配置<br/>
[global]
url = jdbc:mysql://127.0.0.1:8097/java_stu
driver = com.mysql.jdbc.Driver
user_name = develop
pwd = yrSuper001
run_sec = db_mysql

#用于设置数据库与java类型的映射关系，基于COC原则
#[db_mysql]
#tinyint=java.lang.Boolean
