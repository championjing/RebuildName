# RebuildName
在使用springcloud的服务注册与发现过程中，随着业务发展会有很多个eureka-client向eureka-server注册，某些相同的erureka-client需要部署到不同集群上，如果client的注册名和所在集群相关就能更好的管理服务模块

# 更好的解决方法

该项目是通过加入源码的流程，达到定制目的，但是，这种方法不仅需要新的代码，更为危险的是可能会引入其他的bug。所以更为安全和方便的方式是通过使用变量${}的方式进行参数配置：
 举例：

 app.cluster=one
 
 spring.application.name=${app.cluster}_own
 
 现在的spring.application.name就是配置项app.cluster+"_own"的组合
