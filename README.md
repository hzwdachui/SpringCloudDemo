# 目标

搭建一个分布式服务，eureka 负责服务注册与发现，gateway 负责网关

利用 feign 实现 server1 调用 server2



client 请求 gateway，请求访问 server1

gateway 查询 eureka 转发请求到 server1，server1 通过 feign 请求 server2



架构

- eureka 服务注册

- service 服务 

- feign：`Feign`是一个`http`请求调用的轻量级框架

  - 以`Java`接口**注解的方式**调用`Http`请求

  - 集成 Ribbon 提供负载均衡功能，集成 Hystrix 提供熔断功能

    https://blog.csdn.net/fyk844645164/article/details/100138805



# 环境配置

本项目基于 x64 macos

Java1.8



## 安装 java

oracle 官网下载：https://www.oracle.com/java/technologies/downloads/#java8-mac

查看是否安装成功

```
/usr/libexec/java_home -V
```





## 安装 maven

### **安装**

```
brew install maven
```

### **maven 换源**

设置 setting.xml 里的源

参考

```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
        <mirror>
            <id>uk</id>
            <mirrorOf>central</mirrorOf>
            <name>Human Readable Name for this Mirror.</name>
            <url>http://uk.maven.org/maven2/</url>
        </mirror>
        <mirror>
            <id>CN</id>
            <name>OSChina Central</name>
            <url>http://maven.oschina.net/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
        <mirror>
            <id>nexus</id>
            <name>internal nexus repository</name>
            <url>http://repo.maven.apache.org/maven2</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```



setting.xml 的位置

- 方法一

​                 ![img](assets/quickstart/MTY4ODg1NzQxOTM1OTIxMA_128238_TGgqv8OnJAAkXOJc_1649843474.png)        

- 方法二

mac 的位置

```
/usr/local/opt/maven/libexec/conf/setting.xml
```



# 创建项目

## 创建空项目

IDEA 创建一个空项目

File -> New -> Project

选择 Empty Project

![image-20220413232525827](assets/quickstart/image-20220413232525827.png)



## 创建 Eureka 模块

### 创建项目

通过 springboot 模版创建：https://start.spring.io/

- 包管理选择 maven
- 依赖选择 Eureka Server

<img src="assets/quickstart/image-20220413232214703.png" alt="image-20220413232214703" style="zoom:67%;" />



### 添加配置

导入 maven

![image-20220413232809339](assets/quickstart/image-20220413232809339.png)

安装依赖

![image-20220413232832860](assets/quickstart/image-20220413232832860.png)



在 ./src/main/resources 下创建 application.yml 文件

```yaml
server:
  port: 8700 # 端口自己决定

# 指定当前eureka客户端的注册地址，也就是eureka服务的提供方，当前配置的服务的注册服务方
eureka:
  client:
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
    register-with-eureka: false #自身 不在向eureka注册
    fetch-registry: false #启动时禁用client的注册
  instance:
    hostname: localhost

#指定应用名称
spring:
  application:
    name: demo-eureka-server
```



### 修改启动类

给启动类增加注解

```java
@EnableEurekaServer //当前使用eureka的server
```



### 启动项目

启动项目

访问：http://localhost:8700/



## 创建 Gateway 模块

### 创建项目

通过 springboot 模版创建：https://start.spring.io/

- 包管理选择 maven
- 依赖选择 Gateway 和 Eureka Server



<img src="assets/quickstart/image-20220413213219413.png" alt="image-20220413213219413" style="zoom:67%;" />



### 添加配置

和 eureka 一样导入并安装 maven 依赖

在 ./src/main/resources 下创建 application.yml 文件（创建项目时候的 application.properties 可以不用了）

```yaml
server:
  port: 8600

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://${eureka.instance.hostname}:8700/eureka	# 根据自己创建的 eureka 配置
  instance:
    hostname: localhost
    appname: demo-gateway-server

spring:
  main:
    web-application-type: reactive	# 解决 eureka 和 gateway 依赖包冲突的问题
  application:
    name: demo-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lowerCaseServiceId: true
```



### 启动项目

启动后访问 http://localhost:8700/ 能看到项目被注册



## 创建业务模块

### 创建 server2

- Server2 是**被调用者**



#### 创建项目

File -> New -> Module

<img src="assets/quickstart/image-20220413233938219.png" alt="image-20220413233938219" style="zoom:67%;" />



#### 添加配置

pom.xml（在原来的基础上加这些）

```xml
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.7.RELEASE</version>
  </parent>

  <dependencies>
    <!--引入springcloud的euekea server依赖-->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
  </dependencies>

  <!--指定下载源和使用springcloud的版本-->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Edgware.SR5</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```

导入并安装 maven 依赖



application.yml

```yaml
server:
  port: 8702 # 服务提供方

# 指定当前eureka客户端的注册地址,
eureka:
  client:
    service-url:
      defaultZone: http://${eureka.instance.hostname}:8700/eureka
  instance:
    hostname: localhost
    appname: demo-server2

#当前服务名称
spring:
  application:
    name: demo-server2
```



#### 修改启动类

```java
// 增加这两个注解
@EnableDiscoveryClient    // 代表自己是一个服务提供方
@EnableEurekaClient
```



#### 开发服务

随便什么服务就好，本 demo 中就是打印了一句话



#### 启动项目



### 创建 server1

- Server1 是**调用者**

#### 创建项目

File -> New -> Module

<img src="assets/quickstart/image-20220413233938219.png" alt="image-20220413233938219" style="zoom:67%;" />



#### 添加配置

pom.xml（在原来的基础上加这些）

```xml
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.7.RELEASE</version>
  </parent>

  <dependencies>
    <!--引入springcloud的euekea server依赖-->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
  </dependencies>

  <!--指定下载源和使用springcloud的版本-->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Edgware.SR5</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```

导入并安装 maven 依赖



application.yml

```yaml
server:
  port: 8701 # 服务提供方

# 指定当前eureka客户端的注册地址,
eureka:
  client:
    service-url:
      defaultZone: http://${eureka.instance.hostname}:8700/eureka
  instance:
    hostname: localhost
    appname: demo-server1

#当前服务名称
spring:
  application:
    name: demo-server1
```



#### 修改启动类

```java
// 增加注解
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.example.Feign"})	// 表示要通过 feign 调用别的服务
```



#### 开发服务

server1 提供一个 rpc 调用 server2 提供的服务

首先要写一个 interface，注册一个 client 去调用别的服务

- @FeignClient
  - name 参数表示服务名，application.yml 中配置的，这里调用 demo-server2

```java
@FeignClient(name = "demo-server2")
public interface ClientApi {
    @RequestMapping(value = "/server2-api")
    String ServiceClient(@RequestParam("s") String s);  // @RequestParam("s") 必须写
}
```

然后就可以通过这个 client 获取 demo-server2 提供的 /server2-api 接口的返回了

```java
@RestController
@RequestMapping("/")
public class Controller implements Api{
    @Autowired
    private ClientApi client;
    @RequestMapping("/server1-api")
    public String Service(String s){
        return client.ServiceClient(s);
    }
}
```



#### 启动项目



## 检验

http://localhost:8600/demo-server1/server1-api?s=test

- 8600 是 gateway 的端口，指定服务名 demo-server1 会路由到 demo-server1
- 返回 我是 Service2, 传入的值为：test 



# Ref

- https://gitee.com/study-awesome/spring-cloud-sample#/study-awesome/spring-cloud-sample/blob/master/sample-feign/doc/SpringCloud_feign_01_feign%E5%85%A5%E9%97%A8%E5%AE%9E%E4%BE%8B.md

- 从零搭建spring cloud：https://mp.weixin.qq.com/s/S2LA_RbnJKzpZxWEhhCp5A

- https://blog.csdn.net/weixin_37558140/article/details/119999066

- Maven Archetype Catalogs：https://stackoverflow.com/questions/20030667/how-to-use-maven-archetype-in-intellij-idea-such-as-spring-mvc-quickstart-arche
