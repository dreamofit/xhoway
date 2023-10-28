# xhoway 说明文档
## 项目用途
    使用该项目可以快速创建另外一个项目

## 使用方法
    运行Main.java程序，输入你的项目名，创建新的应用
    ServiceInit可以是新应用的启动类

## 技术准备
    后端服务拟采用java，其中jdk版本基于17进行开发，使用springboot框架，数据库使用mybatis，使用dubbo进行分布式开发。

## 项目框架
    xhoway-core: 数据库核心层
    xhoway-processor： 处理器层，各服务核心处理逻辑
    xhoway-util: 工具库
    xhoway-api: dubbo api
    xhoway-access:服务接入层，包括RPC和HTTP接入

## 环境变量
    SERVICE_ADDR 应用所在主机ip地址
    MYSQL_PASS mysql中root用户密码


