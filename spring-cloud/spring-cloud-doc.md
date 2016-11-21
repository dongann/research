# Spring Cloud

Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state). Coordination of distributed systems leads to boiler plate patterns, and using Spring Cloud developers can quickly stand up services and applications that implement those patterns. They will work well in any distributed environment, including the developer’s own laptop, bare metal data centres, and managed platforms such as Cloud Foundry.

# Features Spring Cloud中的功能模块
Spring Cloud focuses on providing good out of box experience for typical use cases and extensibility mechanism to cover others.

对于系统架构中多种典型场景的实现和可扩展性的要求，Spring Cloud提供了很好的开箱即用的解决方案。如下：

* Distributed/versioned configuration 分布式的/版本化的配置管理
* Service registration and discovery 服务的注册和发现
* Routing 路由
* Service-to-service calls 服务间的调用
* Load balancing 负载均衡
* Circuit Breakers 断路器
* Global locks 全局锁
* Leadership election and cluster state 主节点选举和集群状态
* Distributed messaging 分布式消息

# Cloud Native Applications

Cloud Native is a style of application development that encourages easy adoption of best practices in the areas of continuous delivery and value-driven development. A related discipline is that of building 12-factor Apps in which development practices are aligned with delivery and operations goals, for instance by using declarative programming and management and monitoring. Spring Cloud facilitates these styles of development in a number of specific ways and the starting point is a set of features that all components in a distributed system either need or need easy access to when required.

在快速迭代和价值驱动的敏捷开发领域，对于需要快速开发的应用，Cloud Native是一种很好的最佳实践方式。一个契合度非常高的例子就是12-factor app，它的理论包含了自动运维、持续交付、微服务等，例如通过声明式方式进行编程，同时需要对应的管理和监控手段介入。当被请求的分布式系统中相关组件需要很方便的进行访问的时候，Spring Cloud提供了这样的相关功能同时在不断的发展。

Many of those features are covered by Spring Boot, which we build on in Spring Cloud. Some more are delivered by Spring Cloud as two libraries: Spring Cloud Context and Spring Cloud Commons. Spring Cloud Context provides utilities and special services for the ApplicationContext of a Spring Cloud application (bootstrap context, encryption, refresh scope and environment endpoints). Spring Cloud Commons is a set of abstractions and common classes used in different Spring Cloud implementations (eg. Spring Cloud Netflix vs. Spring Cloud Consul).

Spring Cloud的大部分功能是基于Spring Boot的。Spring Cloud通过两个库实现了更多的功能：Spring Cloud Context和Spring Cloud Commons。Spring Cloud Context提供了针对于Spring Cloud应用上下文的相关服务（例如：启动上下文，加密服务，刷新作用域和环境端点服务）。Spring Cloud Commons针对在不同的SpringCloud实现中提供了一组公共类的集合（例如：Spring Cloud Netflix 和 Spring Cloud Consul）。

If you are getting an exception due to "Illegal key size" and you are using Sun’s JDK, you need to install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files. See the following links for more information:

如果你正是用sun公司的JDK，因为“非法键值长度”产生一个异常，那么你需要安装Java加密扩展包（JCE）的无限制权限策略文件。更多信息查看以下链接：

* Java 6 JCE
* Java 7 JCE
* Java 8 JCE

Extract files into JDK/jre/lib/security folder (whichever version of JRE/JDK x64/x86 you are using).

进入目录JDK/jre/lib/security提取文件（无论您正在使用的是JDK/JRE的哪个版本，x64/x86）。
备注：Spring Cloud 是在没有限制的apache2.0许可下发布。如果您想要贡献一些文档或者发现了一个错误，请在{githubmaster}/docs/src/main/asciidoc[github]工程中找到源码并且进行问题的跟踪。

> **NOTE**<br />
> Spring Cloud is released under the non-restrictive Apache 2.0 license. If you would like to contribute to this section of the documentation or if you find an error, please find the source code and issue trackers in the project at {githubmaster}/docs/src/main/asciidoc[github].

# Spring Cloud Context: Application Context Services 应用上下文服务

Spring Boot has an opinionated view of how to build an application with Spring: for instance it has conventional locations for common configuration file, and endpoints for common management and monitoring tasks. Spring Cloud builds on top of that and adds a few features that probably all components in a system would use or occasionally need.

Spring Boot有自己的方式来创建一个应用程序：比如它约定了配置文件的位置、通用的端点管理和监控任务。Spring Cloud构建在应用程序的基础之上，并且增加了许多分布式系统可能需要的组件。

## The Bootstrap Application Context 启动上下文

A Spring Cloud application operates by creating a "bootstrap" context, which is a parent context for the main application. Out of the box it is responsible for loading configuration properties from the external sources, and also decrypting properties in the local external configuration files. The two contexts share an Environment which is the source of external properties for any Spring application. Bootstrap properties are added with high precedence, so they cannot be overridden by local configuration.

启动上下文对于Spring应用的Application Context来说是一个父上下文，一个Spring Cloud应用通过创建它来运行。它也是开箱即用的，负责从外部资源中加载配置属性，同时可以在本地的外部配置文件中解析相关属性。对于任意的Spring应用来说，两个上下文可以共享同一个外部获取的属性“Environment”。因为Bootstrap属性被优先加载，所以他们不能被本地配置重写。

The bootstrap context uses a different convention for locating external configuration than the main application context, so instead of application.yml (or .properties) you use bootstrap.yml, keeping the external configuration for bootstrap and main context nicely separate. Example:

Bootstrap context和Application Context有着不同的约定，所以新增了一个bootstrap.yml文件，而不是使用“application.yml” (或者application.properties)，保证Bootstrap Context和Application Context配置的分离。例如：

`bootstrap.yml`
```yaml
spring:
  application:
    name: foo
  cloud:
    config:
      uri: ${SPRING_CONFIG_URI:http://localhost:8888}
```

It is a good idea to set the spring.application.name (in bootstrap.yml or application.yml) if your application needs any application-specific configuration from the server.

如果你想在服务端设置应用的名字，以上可以通过在bootstrap.yml中配置spring.application.name来实现。

You can disable the bootstrap process completely by setting spring.cloud.bootstrap.enabled=false (e.g. in System properties).

你可以通过设置spring.cloud.bootstrap.enabled=false来禁用bootstrap。

## Application Context Hierarchies 应用上下文层次结构

If you build an application context from SpringApplication or SpringApplicationBuilder, then the Bootstrap context is added as a parent to that context. It is a feature of Spring that child contexts inherit property sources and profiles from their parent, so the "main" application context will contain additional property sources, compared to building the same context without Spring Cloud Config. The additional property sources are:

如果你通过SpringApplication或者SpringApplicationBuilder创建一个Application Context,那么会为spring应用的Application Context创建父上下文Bootstrap Context。在Spring里有个特性，子上下文会继承父类的“property sources” and “profiles” ，所以“main application context” 相对于没有使用Spring Cloud Config，会新增额外的property sources。额外的property sources有：

* "bootstrap": an optional CompositePropertySource appears with high priority if any PropertySourceLocators are found in the Bootstrap context, and they have non-empty properties. An example would be properties from the Spring Cloud Config Server. See below for instructions on how to customize the contents of this property source.<br />
  "bootstrap" : 如果在Bootstrap Context扫描到PropertySourceLocators并且有属性，则会添加到CompositePropertySource。Spirng Cloud Config就是通过这种方式来添加的属性的。详见下面的说明如何自定义属性源的内容。
* "applicationConfig: [classpath:bootstrap.yml]" (and friends if Spring profiles are active). If you have a bootstrap.yml (or properties) then those properties are used to configure the Bootstrap context, and then they get added to the child context when its parent is set. They have lower precedence than the application.yml (or properties) and any other property sources that are added to the child as a normal part of the process of creating a Spring Boot application. See below for instructions on how to customize the contents of these property sources.<br />
  "applicationConfig: [classpath:bootstrap.yml]": 如果你使用bootstrap.yml来配置，他比application.yml优先级要低。它将添加到子上下文，作为Spring Boot应用程序的一部分。下文有介绍。

Because of the ordering rules of property sources the "bootstrap" entries take precedence, but note that these do not contain any data from bootstrap.yml, which has very low precedence, but can be used to set defaults.

由于优先级规则，不包含从bootstrap.yml来的数据，但是可以用它作为默认设置。

You can extend the context hierarchy by simply setting the parent context of any ApplicationContext you create, e.g. using its own interface, or with the SpringApplicationBuilder convenience methods (parent(), child() and sibling()). The bootstrap context will be the parent of the most senior ancestor that you create yourself. Every context in the hierarchy will have its own "bootstrap" property source (possibly empty) to avoid promoting values inadvertently from parents down to their descendants. Every context in the hierarchy can also (in principle) have a different spring.application.name and hence a different remote property source if there is a Config Server. Normal Spring application context behaviour rules apply to property resolution: properties from a child context override those in the parent, by name and also by property source name (if the child has a property source with the same name as the parent, the one from the parent is not included in the child).

你可以很容易的扩展任何你建立的上下文层次，可以使用它提供的接口，或者使用SpringApplicationBuilder的一些方法（parent(),child()sibling()）。启动上线问将是最高级别的父类。扩展的每一个Context都有有自己的bootstrap property source（有可能是空的）。扩展的每一个Context都有不同spring.application.name。同一层层次的父子上下文原则上也有一有不同的名称，因此，也会有不同的Config Server配置。子上下文的属性在相同名字的情况下将覆盖父上下文的属性。

Note that the SpringApplicationBuilder allows you to share an Environment amongst the whole hierarchy, but that is not the default. Thus, sibling contexts in particular do not need to have the same profiles or property sources, even though they will share common things with their parent.

注意允许共享Environment到所有层次，但不是默认的。因此，同级的兄弟上下文不在和父类共享一些东西的时候不一定有相同的profiles或者property sources源码位置。

## Changing the Location of Bootstrap Properties 修改启动文件的位置

The bootstrap.yml (or .properties) location can be specified using spring.cloud.bootstrap.name (default "bootstrap") or spring.cloud.bootstrap.location (default empty), e.g. in System properties. Those properties behave like the spring.config.* variants with the same name, in fact they are used to set up the bootstrap ApplicationContext by setting those properties in its Environment. If there is an active profile (from spring.profiles.active or through the Environment API in the context you are building) then properties in that profile will be loaded as well, just like in a regular Spring Boot app, e.g. from bootstrap-development.properties for a "development" profile.

bootstrap.yml的位置可以由spring.cloud.bootstrap.name（默认:bootstrap）或者spring.cloud.bootstrap.location（默认空）来进行指定。这些属性行为与spring.config.*类似，通过它的Environment来配置引导ApplicationContext进行加载。如果有一个激活的profile（来源于spring.profiles.active或者正在构建的Environment  Api）然后其中的属性即将被加载，就像Spring Boot应用通过 bootstrap-development.properties对开发环境进行了配置。

## Customizing the Bootstrap Configuration 自定义启动配置

The bootstrap context can be trained to do anything you like by adding entries to /META-INF/spring.factories under the key org.springframework.cloud.bootstrap.BootstrapConfiguration. This is a comma-separated list of Spring @Configuration classes which will be used to create the context. Any beans that you want to be available to the main application context for autowiring can be created here, and also there is a special contract for @Beans of type ApplicationContextInitializer. Classes can be marked with an @Order if you want to control the startup sequence (the default order is "last").

可以通过修改/META-INF/spring.factories配置文件中的org.springframework.cloud.bootstrap.BootstrapConfiguration配置项的值来控制启动上下文。同时通过逗号分隔的Spring@Configuration类来建立上下文。任何main application context需要的自动注入的Bean可以在这里通过这种方式来获取。这也是ApplicationContextInitializer建立@Bean的方式。可以通过@Order来更改初始化序列，默认是”last”。

> **WARNING**<br />
> Be careful when adding custom BootstrapConfiguration that the classes you add are not @ComponentScanned by mistake into your "main" application context, where they might not be needed. Use a separate package name for boot configuration classes that is not already covered by your @ComponentScan or @SpringBootApplication annotated configuration classes.<br />
> **警告**<br />
> 你添加的自定义BootstrapConfiguration类没有错误的把@ComponentScanned加入到你的主应用上下文，他们可能是不需要的。使用一个另外的包不被@ComponentScan或者@SpringBootApplication注解覆盖到。

The bootstrap process ends by injecting initializers into the main SpringApplication instance (i.e. the normal Spring Boot startup sequence, whether it is running as a standalone app or deployed in an application server). First a bootstrap context is created from the classes found in spring.factories and then all @Beans of type ApplicationContextInitializer are added to the main SpringApplication before it is started.

通过spring.factories配置的类初始化的所有的Bean都会在SpingApplicatin启动前加入到它的上下文里去

## Customizing the Bootstrap Property Sources 自定义启动属性源

The default property source for external configuration added by the bootstrap process is the Config Server, but you can add additional sources by adding beans of type PropertySourceLocator to the bootstrap context (via spring.factories). You could use this to insert additional properties from a different server, or from a database, for instance.

默认的property source添加额外的配置是通过配置服务（Config Server），你也可以自定义添加property source通过实现PropertySourceLocator接口来添加。你可以从不同的服务或者数据库等使用它增加配置属性。

As an example, consider the following trivial custom locator:

示例：

```java
@Configuration
public class CustomPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {
        return new MapPropertySource("customProperty",
                Collections.<String, Object>singletonMap("property.from.sample.custom.source", "worked as intended"));
    }

}
```

The Environment that is passed in is the one for the ApplicationContext about to be created, i.e. the one that we are supplying additional property sources for. It will already have its normal Spring Boot-provided property sources, so you can use those to locate a property source specific to this Environment (e.g. by keying it on the spring.application.name, as is done in the default Config Server property source locator).

If you create a jar with this class in it and then add a META-INF/spring.factories containing:

Environment在上述代码中被建立并且并传入，就像定义了一个额外的属性源一样。对于一个Spring Boot应用来说已经有自己的属性源，那么你能有这种方式提供自定义的属性源。如果你建立了一个jar包，在META-INF/spring.factories文件中包含属性：

`org.springframework.cloud.bootstrap.BootstrapConfiguration=sample.custom.CustomPropertySourceLocator`

then the "customProperty" PropertySource will show up in any application that includes that jar on its classpath.

那么，customProperty的PropertySource将会被包含到应用。

## Environment Changes 修改Environment

The application will listen for an EnvironmentChangedEvent and react to the change in a couple of standard ways (additional ApplicationListeners can be added as @Beans by the user in the normal way). When an EnvironmentChangedEvent is observed it will have a list of key values that have changed, and the application will use those to:

应用程序将监听EnvironmentChangeEvent，并且响应这一些改变（添加的ApplicationListeners可以被用户用标准的方式增加到@Beans）。当EnvironmentChangeEvent事件被监听到以后，它将获取到一个已经改变了值的集合列表，同时应用程序用这个列表做如下事情：

* Re-bind any @ConfigurationProperties beans in the context<br />
  重新把@ConfigrationProperties加入上下文。
* Set the logger levels for any properties in logging.level.* <br />
  在配置项logging.level.*中设置日志级别

Note that the Config Client does not by default poll for changes in the Environment, and generally we would not recommend that approach for detecting changes (although you could set it up with a @Scheduled annotation). If you have a scaled-out client application then it is better to broadcast the EnvironmentChangedEvent to all the instances instead of having them polling for changes (e.g. using the Spring Cloud Bus).

默认情况下，Config Client不轮询Environment的改变。一般情况，不建议使用这种方式来监测变化（虽然你可以通过@Scheduled注解来设置）。对于可扩展的应用程序，使用广播到所有实例的方式，好过轮询的方式。（比如使用Spring Cloud Bus项目）。

The EnvironmentChangedEvent covers a large class of refresh use cases, as long as you can actually make a change to the Environment and publish the event (those APIs are public and part of core Spring). You can verify the changes are bound to @ConfigurationProperties beans by visiting the /configprops endpoint (normal Spring Boot Actuator feature). For instance a DataSource can have its maxPoolSize changed at runtime (the default DataSource created by Spring Boot is an @ConfigurationProperties bean) and grow capacity dynamically. Re-binding @ConfigurationProperties does not cover another large class of use cases, where you need more control over the refresh, and where you need a change to be atomic over the whole ApplicationContext. To address those concerns we have @RefreshScope.

EnvironmentChangedEvent覆盖了非常大一部分刷新场景的用例，只要，只要对对Environment进行修改并且发布这个事件（这些是Spring核心API的一部分）。你可以通过观察/configprops端点（Spring Boot Actuator的特性）来检查这些改变绑定到@ConfigurationProperties的bean的情况。对于DataSource这样一个实例，在运行的时候修改maxPoolSize增加大小，修改的变化不会通知实例里面，可以使用@RefreshScope来初始化Bean。

### Refresh Scope 刷新作用域

A Spring @Bean that is marked as @RefreshScope will get special treatment when there is a configuration change. This addresses the problem of stateful beans that only get their configuration injected when they are initialized. For instance if a DataSource has open connections when the database URL is changed via the Environment, we probably want the holders of those connections to be able to complete what they are doing. Then the next time someone borrows a connection from the pool he gets one with the new URL.

一个Spring的@Bean在添加了@RefreshScope注解，可以解决Bean初始化的时候只能获得初始配置的问题。比如，在使用DataSource获得一个数据库连接的时候，当通过Environment修改数据库连接字符串的时候，我们可以通过执行@RefreshScope俩根据修改的配置获取一个新的URL的连接。

Refresh scope beans are lazy proxies that initialize when they are used (i.e. when a method is called), and the scope acts as a cache of initialized values. To force a bean to re-initialize on the next method call you just need to invalidate its cache entry.

@RefreshScope的beans，在使用它初始化的时候是延迟代理的，并且初始化的值放在缓存中。为了在访问下一个方法的时候重新初始化bean，只需要让它的缓存实体失效即可。

The RefreshScope is a bean in the context and it has a public method refreshAll() to refresh all beans in the scope by clearing the target cache. There is also a refresh(String) method to refresh an individual bean by name. This functionality is exposed in the /refresh endpoint (over HTTP or JMX).

@RefreshScope有一个刷新所有bean的方法refreshAll()，也有通过bean的名字刷新单个bean的方法refresh(String)。

> **NOTE**<br />
> @RefreshScope works (technically) on an @Configuration class, but it might lead to surprising behaviour: e.g. it does not mean that all the @Beans defined in that class are themselves @RefreshScope. Specifically, anything that depends on those beans cannot rely on them being updated when a refresh is initiated, unless it is itself in @RefreshScope (in which it will be rebuilt on a refresh and its dependencies re-injected, at which point they will be re-initialized from the refreshed @Configuration).<br />
> **注意**<br />
> @RefreshScope注解在一个@Configuration类上面，但是它可能会产生不可预知的问题。在重新初始化的时候需要注意到可以相互依赖造成的冲突。

## Encryption and Decryption 加密和解密

The Config Client has an Environment pre-processor for decrypting property values locally. It follows the same rules as the Config Server, and has the same external configuration via encrypt.*. Thus you can use encrypted values in the form {cipher}* and as long as there is a valid key then they will be decrypted before the main application context gets the Environment. To use the encryption features in a client you need to include Spring Security RSA in your classpath (Maven co-ordinates "org.springframework.security:spring-security-rsa") and you also need the full strength JCE extensions in your JVM.

Config Client可以在本地进行预处理的解密，与Config Server有相同的规则，通过设置相同的encrypt.*来实现。因此，可以使用加密的值在{cipher}*上，同时该有效值在应用通过Environment获得属值之前进行解密。使用加密特性需要包含Spring Security RSA的包到classpath。Maven依赖”org.springframework.security:spring-security-rsa”。并且，需要在JVM添加JCE扩展。

If you are getting an exception due to "Illegal key size" and you are using Sun’s JDK, you need to install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files. See the following links for more information:

如果你正是用sun公司的JDK，因为“非法键值长度”产生一个异常，那么你需要安装Java加密扩展包（JCE）的无限制权限策略文件。更多信息查看以下链接：

* Java 6 JCE
* Java 7 JCE
* Java 8 JCE

Extract files into JDK/jre/lib/security folder (whichever version of JRE/JDK x64/x86 you are using).

进入目录JDK/jre/lib/security提取文件（无论您正在使用的是JDK/JRE的哪个版本，x64/x86）。

### Endpoints

For a Spring Boot Actuator application there are some additional management endpoints:

相对于Spring Boot Actuator的应用，添加了一些管理端点：

* POST to /env to update the Environment and rebind @ConfigurationProperties and log levels<br />
  发送到/env的POST请求，可以更新Environment重新加载@ConfigurationProperties和日志级别。
* /refresh for re-loading the boot strap context and refreshing the @RefreshScope beans<br />
  /refresh请求可以重新初始化添加了@RefreshScope 的bean。
* /restart for closing the ApplicationContext and restarting it (disabled by default)<br />
  /restart请求可以重启初始化ApplicationContext，重启 (默认是禁用的)。
* /pause and /resume for calling the Lifecycle methods (stop() and start() on the ApplicationContext)<br />
  /pause和/resume请求可以调用ApplicationContext生命周期的方法stop()和start()。

-----------------------------------------------
#Spring Cloud Commons: Common Abstractions 公共抽象库

Patterns such as service discovery, load balancing and circuit breakers lend themselves to a common abstraction layer that can be consumed by all Spring Cloud clients, independent of the implementation (e.g. discovery via Eureka or Consul).

比如服务发现、负载平衡和断路器等通用的模型，它们本身是一个抽象层，可以被所有Spring Cloud组件独立的实现，例如服务发现的具体实现Eureka、Consul。

## Spring RestTemplate as a Load Balancer Client 作为负载均衡客户端

You can use Ribbon indirectly via an autoconfigured RestTemplate when RestTemplate is on the classpath and a LoadBalancerClient bean is defined):

当RestTemplate可以引入工程并且LoadBalancerClient已经存在的情况下，RestTemplate可以使用ribbon进行自动配置。

```java
public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    public String doOtherStuff() {
        String results = restTemplate.getForObject("http://stores/stores", String.class);
        return results;
    }
}
```

The URI needs to use a virtual host name (ie. service name, not a host name). The Ribbon client is used to create a full physical address. See RibbonAutoConfiguration for details of how the RestTemplate is set up.

访问的地址使用虚拟地址（比如服务名，而不是主机名）。Bibbon客户端负责连接物理地址。

## Multiple RestTemplate objects 多个RestTemplate 对象

If you want a RestTemplate that is not load balanced, create a RestTemplate bean and inject it as normal. To access the load balanced RestTemplate use the provided `@LoadBalanced Qualifier:

如果你希望创建一个不是负载均衡器的RestTemplate，可以用正常的方式来注入。访问负载均衡器的RestTemplate使用@LoadBalanced来注解。

```java
public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @LoadBalanced
    private RestTemplate loadBalanced;

    public String doOtherStuff() {
        return loadBalanced.getForObject("http://stores/stores", String.class);
    }

    public String doStuff() {
        return restTemplate.getForObject("http://example.com", String.class);
    }
}
```

## Ignore Network Interfaces 忽略网络接口

Sometimes it is useful to ignore certain named network interfaces so they can be excluded from Service Discovery registration (eg. running in a Docker container). A list of regular expressions can be set that will cause the desired network interfaces to be ignored. The following configuration will ignore the "docker0" interface and all interfaces that start with "veth".

有的时候，对于服务发现注册，忽略某些命名的网络接口是非常有用的，比如使用Docker容器的时候。可以通过一些规则设置来忽略这些网络接口，下面这个配置显示了忽略“docker0”的入口，所有的入口以“veth.*”来匹配。详细的配置类见`InetUtilsProperties`。

`application.yml`
```yaml
spring:
  cloud:
    inetutils:
      ignoredInterfaces:
        - docker0
        - veth.*
```

---------------------------------
# Spring Cloud Config

Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system. With the Config Server you have a central place to manage external properties for applications across all environments. The concepts on both client and server map identically to the Spring Environment and PropertySource abstractions, so they fit very well with Spring applications, but can be used with any application running in any language. As an application moves through the deployment pipeline from dev to test and into production you can manage the configuration between those environments and be certain that applications have everything they need to run when they migrate. The default implementation of the server storage backend uses git so it easily supports labelled versions of configuration environments, as well as being accessible to a wide range of tooling for managing the content. It is easy to add alternative implementations and plug them in with Spring configuration.

Spring Cloud Config项目提供了一个解决分布式系统的配置管理方案，它包含了客户端和服务器两个部分。在客户机和服务器的概念中，Spring环境可以和外部资源的属性直接映射，所以这样的做法很适合与Spring应用程序开发，并且可以被用于任何语言与项目中。随着项目的部署，从开发环境、到测试环境、到生产环节，使用Spring管理这些环境之间的配置，以确保项目拥有他们所需要的一切配置信息都能够安全迁移。后端存储默认使用Git工具实现，因此很容易支持配置环境的版本管理，同时也能够使用其它的工具来管理这部分内容。在Spring Configuration中很容易地利用可插拔方式来添加替代实现。

## Quick Start 快速入门
Start the server:

启动服务：

```shell
$ cd spring-cloud-config-server
$ mvn spring-boot:run
```

The server is a Spring Boot application so you can run it from your IDE instead if you prefer (the main class is ConfigServerApplication). Then try it out a client:

这个服务是一个Spring Boot应用,如果你喜欢你可以直接用IDE启动(main类是ConfigServerApplication). 然后试着启动一个客户端:

```shell
$ curl localhost:8888/foo/development
{"name":"development","label":"master","propertySources":[
  {"name":"https://github.com/scratches/config-repo/foo-development.properties","source":{"bar":"spam"}},
  {"name":"https://github.com/scratches/config-repo/foo.properties","source":{"foo":"bar"}}
]}
```

The default strategy for locating property sources is to clone a git repository (at spring.cloud.config.server.git.uri) and use it to initialize a mini SpringApplication. The mini-application’s Environment is used to enumerate property sources and publish them via a JSON endpoint.

默认加载property资源的策略是克隆一个git仓库(at spring.cloud.config.server.git.uri'),用它去初始化一个mini `SpringApplication, 这个mini SpringApplication的Environment用来枚举property,通过json节点发布它们.

The HTTP service has resources in the form:

HTTP服务资源的构成:

```shell
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

where the "application" is injected as the spring.config.name in the SpringApplication (i.e. what is normally "application" in a regular Spring Boot app), "profile" is an active profile (or comma-separated list of properties), and "label" is an optional git label (defaults to "master".)

application是SpringApplication的spring.config.name,(一般来说'application'是一个常规的Spring Boot应用),profile是一个active的profile(或者逗号分隔的属性列表),label是一个可选的git标签(默认为"master").

The YAML and properties forms are coalesced into a single map, even if the origin of the values (reflected in the "propertySources" of the "standard" form) has multiple sources.

Spring Cloud Config Server pulls configuration for remote clients from a git repository (which must be provided):

Spring Cloud Config Server从git仓库为 为远程客户端拉取配置信息：

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
```

## Client Side Usage 客户端示例

To use these features in an application, just build it as a Spring Boot application that depends on spring-cloud-config-client (e.g. see the test cases for the config-client, or the sample app). The most convenient way to add the dependency is via a Spring Boot starter org.springframework.cloud:spring-cloud-starter-config. There is also a parent pom and BOM (spring-cloud-starter-parent) for Maven users and a Spring IO version management properties file for Gradle and Spring CLI users. Example Maven configuration:

通过构建一个依赖spring-cloud-config-client(例如config-client的测试用例或者样例)的Spring Boot应用,就可以使用这些特性.最方便的方法就是添加一个依赖org.springframework.cloud:spring-cloud-starter-config.对于Maven用户,有一个父pom和BOM(spring-cloud-starter-parent),对于Gradle和Spring CLI用户有一个Spring IO 版本管理文件.例如Maven的配置:

`pom.xml`

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.2.3.RELEASE</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-parent</artifactId>
            <version>1.0.1.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>

<!-- repositories also needed for snapshots and milestones -->
```

Then you can create a standard Spring Boot application, like this simple HTTP server:

然后你可以创建一个标准的Spring Boot应用,比如一个简单的HTTP服务:

```java
@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

When it runs it will pick up the external configuration from the default local config server on port 8888 if it is running. To modify the startup behaviour you can change the location of the config server using bootstrap.properties (like application.properties but for the bootstrap phase of an application context), e.g.

当它运行的时候,它将默认从本地配置服务器8888端口获取外部配置,你可以改版配置服务的位置通过bootstrap.properties(类似于application.properties,但是是一个应用上下文启动的配置文件),例如:

```yaml
spring.cloud.config.uri: http://myconfigserver.com
```

The bootstrap properties will show up in the /env endpoint as a high-priority property source, e.g.

bootstrap.properties将会在/env节点作为一个高优先级的property展示,例如:

```shell
$ curl localhost:8080/env
{
  "profiles":[],
  "configService:https://github.com/spring-cloud-samples/config-repo/bar.properties":{"foo":"bar"},
  "servletContextInitParams":{},
  "systemProperties":{...},
  ...
}
```

(a property source called "configService:<URL of remote repository>/<file name>" contains the property "foo" with value "bar" and is highest priority).

(一条叫做"configService:<URL of remote repository>/<file name>"的属性源包含了属性"foo"和值"bar",它是最高优先级的)

> **NOTE**<br />
> the URL in the property source name is the git repository not the config server URL.<br />
> **备注**<br />
> 属性源中的 URL是git仓库的地址而不是配置服务器的URL

## Spring Cloud Config Server 配置服务

The Server provides an HTTP, resource-based API for external configuration (name-value pairs, or equivalent YAML content). The server is easily embeddable in a Spring Boot application using the @EnableConfigServer annotation. So this app is a config server:

针对系统外的配置项(如name-value对或相同功能的YAML内容),该服务器提供了基于资源的HTTP接口.使用@EnableConfigServer注解,该服务器可以很容易的被嵌入到Spring Boot 类型系统中.使用该注解之后该应用系统也是一个配置服务器:

`ConfigServer.java`
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
  public static void main(String[] args) {
    SpringApplication.run(ConfigServer.class, args);
  }
}
```

Like all Spring Boot apps it runs on port 8080 by default, but you can switch it to the conventional port 8888 in various ways. The easiest, which also sets a default configuration repository, is by launching it with spring.config.name=configserver (there is a configserver.yml in the Config Server jar). Another is to use your own application.properties, e.g.

像所有的Spring Boot应用系统默认运行在8080端口一样,你也可以通过各种方式切换这个端口为Config Server默认运行端口为8888.最简单的方法是启动Config Server 时,通过spring.config.name=configserver参数(在Config Server jar中必须有一个configserver.yml文件),这种方式也可以给整个Config Server设置一个默认配置参数集合.另一种方式是使用你自己的application.properties文件，例如：

`application.properties`
```yaml
server.port: 8888
spring.cloud.config.server.git.uri: file://${user.home}/config-repo
```

where `${user.home}/config-repo` is a git repository containing YAML and properties files.

其中`${user.home}/config-repo`包含了YAML 和 properties文件的git库.

> **NOTE** <br />
> in Windows you need an extra "/" in the file URL if it is absolute with a drive prefix, e.g. `file:///${user.home}/config-repo`.<br />
> **注意**：在Windows系统中,如果文件URL是绝对路径并前面有驱动符号,你需要多增加个'/'符号，例如：`file:///${user.home}/config-repo`.

>**TIP** <br />
> Here’s a recipe for creating the git repository in the example above:<br />
> **提示**：创建上面例子使用的git库,使用如下简单方法:

> ```shell
> $ cd $HOME
> $ mkdir config-repo
> $ cd config-repo
> $ git init .
> $ echo info.foo: bar > application.properties
> $ git add -A .
> $ git commit -m "Add application.properties"
> ```

> **WARNING**<br />
> using the local filesystem for your git repository is intended for testing only. Use a server to host your configuration repositories in production.<br />
> **警告**：你的git库使用本地文件系统目的是为了测试.在生产环境中,你需要使用服务器来运行你的git库.

> **WARNING**<br />
> the initial clone of your configuration repository will be quick and efficient if you only keep text files in it. If you start to store binary files, especially large ones, you may experience delays on the first request for configuration and/or out of memory errors in the server.<br />
> **警告**：在配置库中,你如果只存储文本文件,初始clone配置库是非常快捷和高效的.如果你开始存储二进制文件特别是大型的二进制文件,你可能会遇到第一次请求配置文件比较慢或遇到发生在配置服务器上的内存溢出.

## Environment Repository 资源库环境

Where do you want to store the configuration data for the Config Server? The strategy that governs this behaviour is the EnvironmentRepository, serving Environment objects. This Environment is a shallow copy of the domain from the Spring Environment (including propertySources as the main feature). The Environment resources are parametrized by three variables:

你想把Config Server的配置数据存储到哪里?解决这个问题的策略是EnvironmentRepository,并提供Environment对象.Environment对象是对Spring的Environment(其中包括 propertySources做为主要属性)的浅拷贝. Environment资源被参数化成了3个变量:

* {application} maps to "spring.application.name" on the client side;<br />
  {application} 对应客户端的"spring.application.name"属性;
* {profile} maps to "spring.active.profiles" on the client (comma separated list); and<br />
  {profile} 对应客户端的 "spring.profiles.active"属性(逗号分隔的列表); 和
* {label} which is a server side feature labelling a "versioned" set of config files.
  {label} 对应服务端属性,这个属性能标示一组配置文件的版本

Repository implementations generally behave just like a Spring Boot application loading configuration files from a "spring.config.name" equal to the {application} parameter, and "spring.profiles.active" equal to the {profiles} parameter. Precedence rules for profiles are also the same as in a regular Boot application: active profiles take precedence over defaults, and if there are multiple profiles the last one wins (like adding entries to a Map).

Repository的通常实现特征,非常像SpringBoot系统加载配置文件,从"spring.config.name"加载配置相当于从{application}参数加载,从"spring.profiles.active"加载配置相当于从{profiles}参数加载. profiles的优先级规则也和通常的Spring Boot系统一样:激活的profiles的优先级高于defaults,有多个profiles,最后一个起作用 (像往Map中增加entries).

Example: a client application has this bootstrap configuration:

举例: 一个客户端应用系统有这样一个bootstrap 配置:

`bootstrap.yml`
```yaml
spring:
  application:
    name: foo
  profiles:
    active: dev,mysql
```

(as usual with a Spring Boot application, these properties could also be set as environment variables or command line arguments).

(像通常的Spring Boot应用程序一样, 这些参数也可以通过环境变量或命令行参数进行设置).

If the repository is file-based, the server will create an Environment from application.yml (shared between all clients), and foo.yml (with foo.yml taking precedence). If the YAML files have documents inside them that point to Spring profiles, those are applied with higher precendence (in order of the profiles listed), and if there are profile-specific YAML (or properties) files these are also applied with higher precedence than the defaults. Higher precendence translates to a PropertySource listed earlier in the Environment. (These are the same rules as apply in a standalone Spring Boot application.)

如果配置库是基于文件的,服务器将从application.yml(所有的客户端共享)和 foo.yml(foo.yml拥有高优先级)中创建一个Environment对象. 如果这些 YAML文件中有指定Spring profiles,那么这些profiles将有较高优先级(按在profiles列出的顺序),同时如果存在指定profile的YAML(或properties)文件,这些文件就比default文件具有较高优先级.高优先级的配置优先转成Environment对象中的PropertySource.(这和单独的Spring Boot系统使用的规则是一样的.)

## Git Backend Git后端

The default implementation of EnvironmentRepository uses a Git backend, which is very convenient for managing upgrades and physical environments, and also for auditing changes. To change the location of the repository you can set the "spring.cloud.config.server.git.uri" configuration property in the Config Server (e.g. in application.yml). If you set it with a file: prefix it should work from a local repository so you can get started quickly and easily without a server, but in that case the server operates directly on the local repository without cloning it (it doesn’t matter if it’s not bare because the Config Server never makes changes to the "remote" repository). To scale the Config Server up and make it highly available, you would need to have all instances of the server pointing to the same repository, so only a shared file system would work. Even in that case it is better to use the ssh: protocol for a shared filesystem repository, so that the server can clone it and use a local working copy as a cache.

EnvironmentRepository的默认实现是使用Git后端,Git后端对于管理升级和物理环境是很方便的,对审计配置变更也很方便.想改变配置库的位置,你可以在Config Server中设置"spring.cloud.config.server.git.uri"配置项的值(e.g. 如在 application.yml文件中).如果你设置这个属性使用 file:前缀,Config Server 是从本地配置库中取数据,这种方式可以不使用Git的情况下,快速和简单的运行起来,但是这种情况下,Config Server不通过Clone Git配置库直接操作本地库(如果本地配置库不为空,也不用担心,因为Config Server不会把变更推送到"remote"库中). 为了增强Config Server 的高可靠性,需要按比例增加Config Server的数量,这时候需要让所有的Config Server 实例指向相同的配置库,此种情况下只能有一个共享的文件系统才能很好的运行.即使在这种情况下,最好还是使用ssh: 协议来访问共享文件系统库,以便让服务器能够克隆Git库并可以让本地工作副本当作缓存来使用.

This repository implementation maps the {label} parameter of the HTTP resource to a git label (commit id, branch name or tag). If the git branch or tag name contains a slash ("/") then the label in the HTTP URL should be specified with the special string "(_)" instead (to avoid ambiguity with other URL paths). Be careful with the brackets in the URL if you are using a command line client like curl (e.g. escape them from the shell with quotes '').

这个配置库的实现通过映射HTTP资源的{label}参数为git label(提交id,分支名称或tag).如果git分支或tag的名称包含一个斜杠 ("/"),此时HTTP URL中的label需要使用特殊字符串"(_)"来替代(为了避免与其他URL路径相互混淆).如果使用了命令行客户端如 curl,请谨慎处理URL中的括号(例如：在shell下请使用引号''来转移他们).

## Placeholders in Git URI Git URI占位符

Spring Cloud Config Server supports a git repository URL with placeholders for the {application} and {profile} (and {label} if you need it, but remember that the label is applied as a git label anyway). So you can easily support a "one repo per application" policy using (for example):

Spring Cloud Config Server支持git库URL中包含针对{application}和 {profile}的占位符(如果你需要,{label}也可包含占位符, 不过要牢记的是任何情况下label只指git的label).所以,你可以很容易的支持"一个应用系统一个配置库"策略(举例):

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/{application}
```

or a "one repo per profile" policy using a similar pattern but with {profile}.

或者"一个profile一个配置库"策略,这两种策略的使用模式是一样的,后者使用{profile}进行配置.

## Pattern Matching and Multiple Repositories 模式匹配和多资源库

There is also support for more complex requirements with pattern matching on the application and profile name. The pattern format is a comma-separated list of {application}/{profile} names with wildcards (where a pattern beginning with a wildcard may need to be quoted). Example:

针对application和profile名称的模式匹配,Config Server也支持更复杂的需求.匹配的表达式格式是用带有通配符号的{application}/{profile}名称列表,这些名称列表用逗号分隔. (如果这个表达式用通配符开始,则这个表达式需要用引号括住). 例如:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            simple: https://github.com/simple/config-repo
            special:
              pattern: special*/dev*,*special*/dev*
              uri: https://github.com/special/config-repo
            local:
              pattern: local*
              uri: file:/home/configsvc/config-repo
```

If {application}/{profile} does not match any of the patterns, it will use the default uri defined under "spring.cloud.config.server.git.uri". In the above example, for the "simple" repository, the pattern is simple/* (i.e. it only matches one application named "simple" in all profiles). The "local" repository matches all application names beginning with "local" in all profiles (the /* suffix is added automatically to any pattern that doesn’t have a profile matcher).

如果 {application}/{profile}不能匹配任何表达式, 那么将使用"spring.cloud.config.server.git.uri"对应的值. 在上例子中, 对于 "simple" 配置库, 匹配模式是simple/* (也就说,无论profile是什么，它只匹配application 名称为"simple"应用系统)."local"库匹配所有application名称以"local"开头任何应用系统,不管profiles是什么(因没有配置对profile的匹配规则,/*后缀会被自动的增加到任何的匹配表达式中 ).

> **NOTE**<br />
> the "one-liner" short cut used in the "simple" example above can only be used if the only property to be set is the URI. If you need to set anything else (credentials, pattern, etc.) you need to use the full form.<br />
> **注意**：在上述"simple"例子中,如果设置的属性只是一个URI,只使用一行就能完整的表示清楚. 如果你需要设置其他属性(如credentials, pattern, etc.),你需要使用完整的格式才能表示清楚.

The pattern property in the repo is actually an array, so you can use a YAML array (or [0], [1], etc. suffixes in properties files) to bind to multiple patterns. You may need to do this if you are going to run apps with multiple profiles. Example:

在配置参数中pattern本质上讲,其实是个数组, 因此你可以使用一个YAML 数组(或在properties类型文件中使用 [0],[1],etc.为配置后缀)来绑定多个匹配表达式.当你想使用多个了profile 来运行应用程序时,你可能需要采用数组匹配模式.

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            development:
              pattern:
                - */development
                - */staging
              uri: https://github.com/development/config-repo
            staging:
              pattern:
                - */qa
                - */production
              uri: https://github.com/staging/config-repo
```

> **NOTE**<br />
> Spring Cloud will guess that a pattern containing a profile that doesn’t end in \* implies that you actually want to match a list of profiles starting with this pattern (so `*/staging` is a shortcut for `["*/staging", "*/staging,*"])`. This is common where you need to run apps in the "development" profile locally but also the "cloud" profile remotely, for instance.<br />
> **注意**：如果 profile 不是以\*结尾的,Spring Cloud 会猜想实际的profile值, 这意味着你实际上是匹配以这个表达式开头的一个profile列表(因此 `*/staging` 只是 `["*/staging", "*/staging,*"]`的 简单表示法)。举例，普遍\*\*\*\*的一种方法是在本地环境中使用"development" profile 来运行系统,在远程环境中运行"cloud" profile.

Every repository can also optionally store config files in sub-directories, and patterns to search for those directories can be specified as searchPaths. For example at the top level:

每个配置库也可以选择存储配置文件到子目录中,用searchPaths属性来指定配置的子目录,搜索匹配的表达式就在此子目录中进行的. 大概的例子如下:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar*
```

In this example the server searches for config files in the top level and in the "foo/" sub-directory and also any sub-directory whose name begins with "bar".

在这个例子中,服务会在顶层目录、"foo/"子目录和以"bar"开头的子目录中查找文件.

By default the server clones remote repositories when configuration is first requested. The server can be configured to clone the repositories at startup. For example at the top level:

当配置文件第一次被请求时,默认地服务器会clone远程的配置库.服务器也可以配置在启动时clone远程的配置库.下面就是一个简单的例子:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://git/common/config-repo.git
          repos:
            team-a:
                pattern: team-a-*
                cloneOnStart: true
                uri: http://git/team-a/config-repo.git
            team-b:
                pattern: team-b-*
                cloneOnStart: false
                uri: http://git/team-b/config-repo.git
            team-c:
                pattern: team-c-*
                uri: http://git/team-a/config-repo.git
```

In this example the server clones team-a’s config-repo on startup before it accepts any requests. All other repositories will not be cloned until configuration from the repository is requested.

在这个例子中, 服务器在启动未接收任何请求的过程中,会clone team-a的配置库.其他的配置库都只在请求时才会clone远程的配置.

To use HTTP basic authentication on the remote repository add the "username" and "password" properties separately (not in the URL), e.g.

对于远程配置库,为了使用HTTP的 basic authentication 方法进行认证,需要分开增加 "username" and "password" 属性 (不在URL中增加),

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          username: trolley
          password: strongpassword
```

If you don’t use HTTPS and user credentials, SSH should also work out of the box when you store keys in the default directories (~/.ssh) and the uri points to an SSH location, e.g. "git@github.com:configuration/cloud-configuration". The repository is accessed using JGit, so any documentation you find on that should be applicable. HTTPS proxy settings can be set in ~/.git/config or in the same way as for any other JVM process via system properties (-Dhttps.proxyHost and -Dhttps.proxyPort).

如果你没有使用HTTPS和用户凭证,当你在默认目录(~/.ssh)中存储了key,并且uri参数配置了SSH地址,如配置了"git@github.com:configuration/cloud-configuration",SSH应该是你非常容易使用的方式.这种方式下,是使用JGit来访问配置库的,比较适合查找任何文档.HTTPS proxy 可以在 ~/.git/config中进行设置, 通过JVM的系统属性(-Dhttps.proxyHost and -Dhttps.proxyPort)设置可以完成此功能.

## File System Backend 文件系统后端

There is also a "native" profile in the Config Server that doesn’t use Git, but just loads the config files from the local classpath or file system (any static URL you want to point to with "spring.cloud.config.server.native.searchLocations"). To use the native profile just launch the Config Server with "spring.profiles.active=native".

在Config Server中,还有一种不使用Git的"native"的配置方式,这种方式是从本地classpath 或文件系统中加载配置文件(使用 "spring.cloud.config.server.native.searchLocations"配置项进行设置). 加载Config Server 的"spring.profiles.active=native"配置项可以开启native配置.

> **NOTE** <br />
> Remember to use the file: prefix for file resources (the default without a prefix is usually the classpath). Just as with any Spring Boot configuration you can embed `${}`-style environment placeholders, but remember that absolute paths in Windows require an extra "/", e.g. `file:///${user.home}/config-repo` <br />
> **注意**：牢记使用file:前缀来指示资源 (默认没有前缀是从classpath中去文件).就像任何 Spring Boot配置一样,你也可以 嵌入`${}`环境参数占位符,但是windows系统下使用绝对路径,前缀后面需要多加个"/", e.g. `file:///${user.home}/config-repo`

> **WARNING** <br />
The default value of the `searchLocations` is identical to a local Spring Boot application (so `[classpath:/, classpath:/config, file:./, file:./config]`). This does not expose the `application.properties` from the server to all clients because any property sources present in the server are removed before being sent to the client. <br />
> **警告**：默认的 `searchLocations`值和本地Spring Boot 应用系统是一样的(如 `[classpath:/, classpath:/config,file:./, file:./config]`). 这种方式下,并不暴露服务器上的`application.properties`文件给客户端,因为在把属性传给客户端之前,服务器中属性源信息会被删除掉.

> **TIP**<br />
> A filesystem backend is great for getting started quickly and for testing. To use it in production you need to be sure that the file system is reliable, and shared across all instances of the Config Server.<br />
> **提示**：对于测试来说,用文件系统做后端是非常便捷的.如果想生产环境中使用它,你需要确定文件系统的可靠性,并垮所有的Config Server 实例是能够共享这些本地文件.

The search locations can contain placeholders for {application}, {profile} and {label}. In this way you can segregate the directories in the path, and choose a strategy that makes sense for you (e.g. sub-directory per application, or sub-directory per profile).

对于{application},{profile} and {label}来说,搜寻位置可以包含占位符. 采用这种方式,你可以隔离目录,也可以选择某种策略来使你的应用程序更加清晰明了(如:一个application一个子目录,或一个profile一个子目录).

If you don’t use placeholders in the search locations, this repository also appends the {label} parameter of the HTTP resource to a suffix on the search path, so properties files are loaded from each search location and a subdirectory with the same name as the label (the labelled properties take precedence in the Spring Environment). Thus the default behaviour with no placeholders is the same as adding a search location ending with /{label}/. For example `file:/tmp/config is the same as file:/tmp/config,file:/tmp/config/{label}

如果你在在查找位置中不指定占位符,可以把在HTTP资源请求参数{label}追加到查询路径的后面,此时是从查询路径和用label名称一样的子目录中加载配置文件的(在Spring环境下,被打标记的属性具有较高优先级别).因此,没有占位符的默认特性和以/{label}/结尾的结果一样.举例`file:/tmp/config和 file:/tmp/config,file:/tmp/config/{label}请求效果等同.

## Sharing Configiration With All Applications 共享配置给所有应用

With file-based (i.e. git, svn and native) repositories, resources with file names in application* are shared between all client applications (so application.properties, application.yml, application-*.properties etc.). You can use resources with these file names to configure global defaults and have them overridden by application-specific files as necessary.

在基于文件的资源库中(i.e. git, svn and native), 这样的文件名 application* 命名的资源在所有的客户端都是共享的(如 application.properties, application.yml, application-*.properties,etc.).

The #_property_overrides[property overrides] feature can also be used for setting global defaults, and with placeholders applications are allowed to override them locally.

你可以使用这种文件名的资源进行全局默认值配置,当需要覆盖默认值时,可以使用指定application名称的文件名. 也可以使用#_property_overrides[property overrides]方式来设置全局默认值,通过带占位符的applications来进行本地化覆盖.

> **TIP** <br />
> With the "native" profile (local file system backend) it is recommended that you use an explicit search location that isn’t part of the server’s own configuration. Otherwise the application* resources in the default search locations are removed because they are part of the server. <br />
> **注意**：对于"native" profile(本地文件系统作为后端),推荐你使用指定查询路径进行配置，这个路径不是服务器自己配置的一部分.除此之外,默认查询路径中的application*资源都会被删除,因为这些资源都是服务器的一部分.

## Property Overrides 属性覆盖

The Config Server has an "overrides" feature that allows the operator to provide configuration properties to all applications that cannot be accidentally changed by the application using the normal Spring Boot hooks. To declare overrides just add a map of name-value pairs to spring.cloud.config.server.overrides. For example

Config Server有一个“覆盖”功能,允许开发人员为所有的应用程序提供配置属性,这些配置属性不会被Spring Boot应用程序进行错误设置.定义覆盖只需要为“spring.cloud.config.server.overrides”添加一个Map类型的name-value对. 例如:

```yaml
spring:
  cloud:
    config:
      server:
        overrides:
          foo: bar
```

will cause all applications that are config clients to read foo=bar independent of their own configuration. (Of course an application can use the data in the Config Server in any way it likes, so overrides are not enforceable, but they do provide useful default behaviour if they are Spring Cloud Config clients.)

会使所有的配置客户端应用程序读取foo=bar到他们自己配置参数中. ( 如果这个应用程序是Spring Cloud Config 客户端，当然,应用程序可以按任何喜欢的方式 使用配置服务器中的数据,所以覆盖并不是强制的,但覆盖提供了非常有用的默认行为.)

> **TIP** <br />
> Normal, Spring environment placeholders with "${}" can be escaped (and resolved on the client) by using backslash ("\") to escape the "$" or the "{", e.g. \${app.foo:bar} resolves to "bar" unless the app provides its own "app.foo". Note that in YAML you don’t need to escape the backslash itself, but in properties files you do, when you configure the overrides on the server. <br />
> **提示**：一般情况下,Spring环境变量占位符"${}"可以通过使用反斜杠(\)来被转义(或者在客户端被正常处理) 如:"\${app.foo:bar}"会被处理成"bar",除非应用程序提供了自己的“app.foo”值.注意,当你在服务器上配置覆盖参数时,在YAML类型文件中,你不需要转义反斜杠本身,而是在properties文件中,你需要对他进行转义.

You can change the priority of all overrides in the client to be more like default values, allowing applications to supply their own values in environment variables or System properties, by setting the flag 

你可以改变客户端所有的覆盖参数的优先级,而使这些值更像默认值,在环境变量或系统属性中提供自己的参数值来修改这些值.

## Health Indicator 健康指示器

Config Server comes with a Health Indicator that checks if the configured EnvironmentRepository is working. By default it asks the EnvironmentRepository for an application named app, the default profile and the default label provided by the EnvironmentRepository implementation.

Config Server也提供了健康指示器,通过这个指示器能够检查已经配置的EnvironmentRepository 是否正常运行.默认情况下,指示器通过 EnvironmentRepository提供的app应用程序名称、default`profile和默认label来请求`EnvironmentRepository.

You can configure the Health Indicator to check more applications along with custom profiles and custom labels, e.g.

你也可以自定义更多的applications、profiles和labels进行检查，例如：

```yaml
spring:
  cloud:
    config:
      server:
        health:
          repositories:
            myservice:
              label: mylabel
            myservice-dev:
              name: myservice
              profiles: development
```

You can disable the Health Indicator by setting spring.cloud.config.server.health.enabled=false.

你也可以通过设置 spring.cloud.config.server.health.enabled=false参数来禁用健康指示器.

## Security 安全

You are free to secure your Config Server in any way that makes sense to you (from physical network security to OAuth2 bearer tokens), and Spring Security and Spring Boot make it easy to do pretty much anything.

你可以自由选择任何你觉得合理的方式来保护你的Config Server(从物理网络安全到OAuth2 令牌),同时使用Spring Security和Spring Boog 能使你做更多其他有用的事情.

To use the default Spring Boot configured HTTP Basic security, just include Spring Security on the classpath (e.g. through spring-boot-starter-security). The default is a username of "user" and a randomly generated password, which isn’t going to be very useful in practice, so we recommend you configure the password (via security.user.password) and encrypt it (see below for instructions on how to do that).

为了使用默认的Spring Boot HTTP Basic 安全,只需要把Spring Security 增加到classpath中(如通过spring-boot-starter-security).默认的用户名是"user",对应的会生成一个随机密码,这种情况在实际使用中并没有意义,我们建议你配置一个密码(通过 security.user.password属性进行配置)并对这个密码进行加密(下面章节有关于怎么加密的步骤).

## Encryption and Decryption 加密与解密

> **IMPORTANT** <br />
> **Prerequisites**: to use the encryption and decryption features you need the full-strength JCE installed in your JVM (it’s not there by default). You can download the "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files" from Oracle, and follow instructions for installation (essentially replace the 2 policy files in the JRE lib/security directory with the ones that you downloaded). <br />
> **重要**<br />
> **预备内容**：为了加密和解密功能,你需要在JVM中安装full-strength JCE(默认并没有安装).你可以在Oracle站点上下载"Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files",然后根据提示安装(一般来说就是使用你下载的2个策略文件替换JRE lib/security目录下的相同名称文件).

If the remote property sources contain encrypted content (values starting with {cipher}) they will be decrypted before sending to clients over HTTP. The main advantage of this set up is that the property values don’t have to be in plain text when they are "at rest" (e.g. in a git repository). If a value cannot be decrypted it is removed from the property source and an additional property is added with the same key, but prefixed with "invalid." and a value that means "not applicable" (usually "<n/a>"). This is largely to prevent cipher text being used as a password and accidentally leaking.

如果远程属性包含加密内容(以{cipher}开头),这些值将在通过HTTP传递到客户端之前被解密.这样设置的最大优点在于属性值不使用时(如在git库中时)，没有必要以明文的方式显示.如果属性值不能被解密,那么这个值将会被删除,之后增加一个和原来key一样的属性值,不过，key的前缀是"invalid."，值是"not applicable"(通常是"<n/a>").这种设计方法很大程度上是为了阻止使用密文当作密码和意外密文泄露.

If you are setting up a remote config repository for config client applications it might contain an application.yml like this, for instance:

如果你想为config client设置远程config库时使用密文，可能包含的文件application.yml像这样，举例:

`application.yml`
```yaml
spring:
  datasource:
    username: dbuser
    password: '{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ'
```

Encrypted values in a .properties file must not be wrapped in quotes, otherwise the value will not be decrypted:

如果是.properties文件,加密的属性值一定不能用引号扩起来，不然值不能解密:

`application.properties`
```yaml
spring.datasource.username: dbuser
spring.datasource.password: {cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ
```

You can safely push this plain text to a shared git repository and the secret password is protected.

你可以设置安全的把纯文本推送到共享的git库中,密码也可以很好的得到保护.

The server also exposes /encrypt and /decrypt endpoints (on the assumption that these will be secured and only accessed by authorized agents). If you are editing a remote config file you can use the Config Server to encrypt values by POSTing to the /encrypt endpoint, e.g.

服务器也会可以暴露 /encrypt和/decrypt接口(假设这些会是安全的,只能被授权的客户端访问).如果你想编辑远程配置文件，你可以请求Config Server的/encrypt接口来加密,e.g.

```shell
$ curl localhost:8888/encrypt -d mysecret
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
```

The inverse operation is also available via /decrypt (provided the server is configured with a symmetric key or a full key pair):

逆操作也可以通过/decrypt 来完成(服务器提供配置一个对称密钥或密钥对来实现此功能):

```shell
$ curl localhost:8888/decrypt -d 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
```

> **TIP** <br />
> If you are testing like this with curl, then use --data-urlencode (instead of -d) or set an explicit Content-Type: text/plain to make sure curl encodes the data correctly when there are special characters ('+' is particularly tricky).<br />
> **提示**：urlencode (取代 -d)或 明确指定Content-Type:text/plain 参数来对这些数据正确编码.

Take the encrypted value and add the {cipher} prefix before you put it in the YAML or properties file, and before you commit and push it to a remote, potentially insecure store.

你加密的值增加{cipher}前缀后,放到YAML或properties文件之前，或者在递交或推送到远程服务器之前,这些加密的值都面临着潜在的不安全存储风险.

The /encrypt and /decrypt endpoints also both accept paths of the form /*/{name}/{profiles} which can be used to control cryptography per application (name) and profile when clients call into the main Environment resource.

当客户端程序对主环境资源不能确定时,/encrypt 和/decrypt接口也接受带有路径形式的请求 /*/{name}/{profiles},这样可以针对每个application(name)和profile进行详细的控制加解密.

> **NOTE** <br />
> to control the cryptography in this granular way you must also provide a @Bean of type TextEncryptorLocator that creates a different encryptor per name and profiles. The one that is provided by default does not do this (so all encryptions use the same key). <br />
> **注意**：如果以这种细粒度的方式来控制加解密，你必须提供一个TextEncryptorLocator类型的@Bean,使用这个Bean 可以为每个名称或profile创建不同的加密方法.默认提供的并没有这个功能(所有加密使用相同的密钥).

The spring command line client (with Spring Cloud CLI extensions installed) can also be used to encrypt and decrypt, e.g.

spring命令行客户端(和 Spring Cloud CLI扩展一起安装)也可以用于加密和解密, e.g.

```shell
$ spring encrypt mysecret --key foo
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
$ spring decrypt --key foo 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
```

To use a key in a file (e.g. an RSA public key for encryption) prepend the key value with "@" and provide the file path, e.g.

为了使用文件中的密钥 (如使用 RSA 公钥加密),需要在提供的文件前增加一个"@"符号,e.g.

```shell
$ spring encrypt mysecret --key @${HOME}/.ssh/id_rsa.pub
AQAjPgt3eFZQXwt8tsHAVv/QHiY5sI2dRcR+...
```

The key argument is mandatory (despite having a -- prefix).

key 参数是必填的(尽管有--前缀).

## Key Management 密钥管理

The Config Server can use a symmetric (shared) key or an asymmetric one (RSA key pair). The asymmetric choice is superior in terms of security, but it is often more convenient to use a symmetric key since it is just a single property value to configure.

Config Server 可以使用对称(共享)密钥或者非对称密钥(RSA密钥对).就安全性来讲,非对称密钥是优先选择,但是，选择对称密钥进行加解密处理显得非常方便，因为,只需要简单的配置一个属性值.

To configure a symmetric key you just need to set encrypt.key to a secret String (or use an enviroment variable ENCRYPT_KEY to keep it out of plain text configuration files).

为了配置对称密钥,你只需要把encrypt.key设置成你的密码字符串(或者使用环境变量ENCRYPT_KEY进行配置,这样可以让密码不在明文配置文件中显示).

To configure an asymmetric key you can either set the key as a PEM-encoded text value (in encrypt.key), or via a keystore (e.g. as created by the keytool utility that comes with the JDK). The keystore properties are encrypt.keyStore.* with * equal to

为了配置非对称密钥，你可以使用PEM编码格式的文本字符串(对encrypt.key配置项设置值),或者使用keystore(例如,可以使用JDK携带的keytool工具创建).keystore的配置属性是encrypt.keyStore.*,*可以是

* location (a Resource location), <br />
  location ( Resource 位置)
* password (to unlock the keystore) and <br />
  password (用来打开keystore) and
* alias (to identify which key in the store is to be used). <br />
  alias (确定使用keystore 中的那个密钥)

The encryption is done with the public key, and a private key is needed for decryption. Thus in principle you can configure only the public key in the server if you only want to do encryption (and are prepared to decrypt the values yourself locally with the private key). In practice you might not want to do that because it spreads the key management process around all the clients, instead of concentrating it in the server. On the other hand it’s a useful option if your config server really is relatively insecure and only a handful of clients need the encrypted properties.

加密使用公钥完成,私钥用来解密.原则上，如果你只想加密，服务器上只配置公钥(用私钥在本地自行解密).实际中,你可能不想这样做,原因是这种方式会把密钥管理过程让所有的客户端都知道，而不是把焦点聚集在服务器上.从另外一个方面讲,一个非常有益的建议是如果你的配置服务器 相对不安全,只能有少数客户端需要加密配置属性.

## Creating a Key Store for Testing 创建一个测试密钥库

To create a keystore for testing you can do something like this:

为了创建用于测试的keystore,你可以按照以下步骤来做:

```shell
$ keytool -genkeypair -alias mytestkey -keyalg RSA \
  -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" \
  -keypass changeme -keystore server.jks -storepass letmein
```

Put the server.jks file in the classpath (for instance) and then in your application.yml for the Config Server:

把 server.jks文件放到你的classpath中(配置服务器的系统中),然后再application.yml文件中对Config Server 进行配置:

```yaml
encrypt:
  keyStore:
    location: classpath:/server.jks
    password: letmein
    alias: mytestkey
    secret: changeme
```

## Using Multiple Keys and Key Rotation 使用多密钥和循环密钥

In addition to the {cipher} prefix in encrypted property values, the Config Server looks for {name:value} prefixes (zero or many) before the start of the (Base64 encoded) cipher text. The keys are passed to a TextEncryptorLocator which can do whatever logic it needs to locate a TextEncryptor for the cipher. If you have configured a keystore (encrypt.keystore.location) the default locator will look for keys in the store with aliases as supplied by the "key" prefix, i.e. with a cipher text like this:

除了在加密属性前增加{cipher}前缀外,在开始使用密文前,Config Server也查找{name:value}这种前缀的属性(0次或多次).系统会把密钥传递给可以做任何逻辑处理的TextEncryptorLocator类,然后使用该类定位到一个用于加密的TextEncryptor类.如果你配置了一个keystore(通过encrypt.keystore.location配置),默认的定位器将定位密钥库中的密钥,定位时使用"key" 前缀定义的别名.举例,配置的密钥文本如下:

```yaml
foo:
  bar: `{cipher}{key:testkey}...`
```

the locator will look for a key named "testkey". A secret can also be supplied via a {secret:…​} value in the prefix, but if it is not the default is to use the keystore password (which is what you get when you build a keytore and don’t specify a secret). If you do supply a secret it is recommended that you also encrypt the secrets using a custom SecretLocator.

定位器将查找一个名字为"testkey"的密钥.密码也可以直接通过{secret:…}这种方式来指定，但是这种方式不是默认使用keystore密码的方式(目的是为了让你明白当使用keystore时，不要特定指定密码).如果你提供了一个密码,建议你自定义一个SecretLocator对密码也进行加密.

Key rotation is hardly ever necessary on cryptographic grounds if the keys are only being used to encrypt a few bytes of configuration data (i.e. they are not being used elsewhere), but occasionally you might need to change the keys if there is a security breach for instance. In that case all the clients would need to change their source config files (e.g. in git) and use a new {key:…​} prefix in all the ciphers, checking beforehand of course that the key alias is available in the Config Server keystore.

在整个加解密使用过程中,如果只使用密钥用来加密一些配置属性值,密钥更换几乎不需要(密钥不需要在其他系统使用的情况)，但是在某些情况下如安全信息泄露,可能需要更换密钥.在这种情况下,所有的客户端都需要更改他们连接配置属性源文件(e.g.如在git中的文件)，还需要使用新的{key:…}前缀加到密文信息中,同时检查在Config Server keystore 中密钥的别名是否可用.

> **TIP** <br />
> the {name:value} prefixes can also be added to plaintext posted to the /encrypt endpoint, if you want to let the Config Server handle all encryption as well as decryption. <br />
> **提示**：如果你想让 Config Server处理所有的加密和解密,{name:value}前缀也可以放到明文前面,然后通过/encrypt接口进行加密.

## Serving Plain Text 文本解释服务 

Instead of using the Environment abstraction (or one of the alternative representations of it in YAML or properties format) your applications might need generic plain text configuration files, tailored to their environment. The Config Server provides these through an additional endpoint at /{name}/{profile}/{label}/{path} where "name", "profile" and "label" have the same meaning as the regular environment endpoint, but "path" is a file name (e.g. log.xml). The source files for this endpoint are located in the same way as for the environment endpoints: the same search path is used as for properties or YAML files, but instead of aggregating all matching resources, only the first one to match is returned.

有的时候根据不同环境，可以考虑不使用`Environment`抽象层（即：不使用JSON,YAML,Properties），而直接使用纯文本方式进行配置。Config Server提供几个额外的访问端点：`/{name}/{profile}/{label}/{path}`起到和正常`Environment`端点一样的作用，不过这里的`{path}`是指的一个文件名(例如 log.xml)。这里的源文件的定位与正常YAML或者属性文件的扫描处理逻辑类似，不同点在于只会搜索到第一个匹配到的文件，而不会想YAML及属性文件那样扫描加载所有匹配文件。

After a resource is located, placeholders in the normal format (${…​}) are resolved using the effective Environment for the application name, profile and label supplied. In this way the resource endpoint is tightly integrated with the environment endpoints. Example, if you have this layout for a GIT (or SVN) repository:

当资源文件被找到后，与常规的配置文件一样，也会先处理占位符。这种配置方法会与环境端点紧密的关联。例如，如果在git（svn）上你有下面这些文件：

```
application.yml
nginx.conf
```

where nginx.conf looks like this:

```conf
server {
    listen              80;
    server_name         ${nginx.server.name};
}
and application.yml like this:

nginx:
  server:
    name: example.com
---
spring:
  profiles: development
nginx:
  server:
    name: develop.com
```

then the /foo/default/master/nginx.conf resource looks like this:

接着有一个/foo/default/master/nginx.conf 文件:

```conf
server {
    listen              80;
    server_name         example.com;
}
and /foo/development/master/nginx.conf like this:

server {
    listen              80;
    server_name         develop.com;
}
```

NOTE
just like the source files for environment configuration, the "profile" is used to resolve the file name, so if you want a profile-specific file then /*/development/*/logback.xml will be resolved by a file called logback-development.xml (in preference to logback.xml).
Embedding the Config Server
The Config Server runs best as a standalone application, but if you need to you can embed it in another application. Just use the @EnableConfigServer annotation. An optional property that can be useful in this case is spring.cloud.config.server.bootstrap which is a flag to indicate that the server should configure itself from its own remote repository. The flag is off by default because it can delay startup, but when embedded in another application it makes sense to initialize the same way as any other application.

NOTE
It should be obvious, but remember that if you use the bootstrap flag the config server will need to have its name and repository URI configured in bootstrap.yml.
To change the location of the server endpoints you can (optionally) set spring.cloud.config.server.prefix, e.g. "/config", to serve the resources under a prefix. The prefix should start but not end with a "/". It is applied to the @RequestMappings in the Config Server (i.e. underneath the Spring Boot prefixes server.servletPath and server.contextPath).

If you want to read the configuration for an application directly from the backend repository (instead of from the config server) that’s basically an embedded config server with no endpoints. You can switch off the endpoints entirely if you don’t use the @EnableConfigServer annotation (just set spring.cloud.config.server.bootstrap=true).

Push Notifications and Spring Cloud Bus
Many source code repository providers (like Github, Gitlab or Bitbucket for instance) will notify you of changes in a repository through a webhook. You can configure the webhook via the provider’s user interface as a URL and a set of events in which you are interested. For instance Github will POST to the webhook with a JSON body containing a list of commits, and a header "X-Github-Event" equal to "push". If you add a dependency on the spring-cloud-config-monitor library and activate the Spring Cloud Bus in your Config Server, then a "/monitor" endpoint is enabled.

When the webhook is activated the Config Server will send a RefreshRemoteApplicationEvent targeted at the applications it thinks might have changed. The change detection can be strategized, but by default it just looks for changes in files that match the application name (e.g. "foo.properties" is targeted at the "foo" application, and "application.properties" is targeted at all applications). The strategy if you want to override the behaviour is PropertyPathNotificationExtractor which accepts the request headers and body as parameters and returns a list of file paths that changed.

The default configuration works out of the box with Github, Gitlab or Bitbucket. In addition to the JSON notifications from Github, Gitlab or Bitbucket you can trigger a change notification by POSTing to "/monitor" with a form-encoded body parameters path={name}. This will broadcast to applications matching the "{name}" pattern (can contain wildcards).

NOTE
the RefreshRemoteApplicationEvent will only be transmitted if the spring-cloud-bus is activated in the Config Server and in the client application.
NOTE
the default configuration also detects filesystem changes in local git repositories (the webhook is not used in that case but as soon as you edit a config file a refresh will be broadcast).
Spring Cloud Config Client
A Spring Boot application can take immediate advantage of the Spring Config Server (or other external property sources provided by the application developer), and it will also pick up some additional useful features related to Environment change events.

Config First Bootstrap
This is the default behaviour for any application which has the Spring Cloud Config Client on the classpath. When a config client starts up it binds to the Config Server (via the bootstrap configuration property spring.cloud.config.uri) and initializes Spring Environment with remote property sources.

The net result of this is that all client apps that want to consume the Config Server need a bootstrap.yml (or an environment variable) with the server address in spring.cloud.config.uri (defaults to "http://localhost:8888").

Eureka First Bootstrap
If you are using Spring Cloud Netflix and Eureka Service Discovery, then you can have the Config Server register with Eureka if you want to, but in the default "Config First" mode, clients won’t be able to take advantage of the registration.

If you prefer to use Eureka to locate the Config Server, you can do that by setting spring.cloud.config.discovery.enabled=true (default "false"). The net result of that is that client apps all need a bootstrap.yml (or an environment variable) with the Eureka server address, e.g. in eureka.client.serviceUrl.defaultZone. The price for using this option is an extra network round trip on start up to locate the service registration. The benefit is that the Config Server can change its co-ordinates, as long as Eureka is a fixed point. The default service id is "CONFIGSERVER" but you can change that on the client with spring.cloud.config.discovery.serviceId (and on the server in the usual way for a service, e.g. by setting spring.application.name).

The discovery client implementations all support some kind of metadata map (e.g. for Eureka we have eureka.instance.metadataMap). Some additional properties of the Config Server may need to be configured in its service registration metadata so that clients can connect correctly. If the Config Server is secured with HTTP Basic you can configure the credentials as "username" and "password". And if the Config Server has a context path you can set "configPath". Example, for a Config Server that is a Eureka client:

bootstrap.yml
eureka:
  instance:
    ...
    metadataMap:
      username: osufhalskjrtl
      password: lviuhlszvaorhvlo5847
      configPath: /config
Config Client Fail Fast
In some cases, it may be desirable to fail startup of a service if it cannot connect to the Config Server. If this is the desired behavior, set the bootstrap configuration property spring.cloud.config.failFast=true and the client will halt with an Exception.

Config Client Retry
If you expect that the config server may occasionally be unavailable when your app starts, you can ask it to keep trying after a failure. First you need to set spring.cloud.config.failFast=true, and then you need to add spring-retry and spring-boot-starter-aop to your classpath. The default behaviour is to retry 6 times with an initial backoff interval of 1000ms and an exponential multiplier of 1.1 for subsequent backoffs. You can configure these properties (and others) using spring.cloud.config.retry.* configuration properties.

TIP
To take full control of the retry add a @Bean of type RetryOperationsInterceptor with id "configServerRetryInterceptor". Spring Retry has a RetryInterceptorBuilder that makes it easy to create one.
Locating Remote Configuration Resources
The Config Service serves property sources from /{name}/{profile}/{label}, where the default bindings in the client app are

"name" = ${spring.application.name}

"profile" = ${spring.profiles.active} (actually Environment.getActiveProfiles())

"label" = "master"

All of them can be overridden by setting spring.cloud.config.* (where * is "name", "profile" or "label"). The "label" is useful for rolling back to previous versions of configuration; with the default Config Server implementation it can be a git label, branch name or commit id. Label can also be provided as a comma-separated list, in which case the items in the list are tried on-by-one until one succeeds. This can be useful when working on a feature branch, for instance, when you might want to align the config label with your branch, but make it optional (e.g. spring.cloud.config.label=myfeature,develop).

Security
If you use HTTP Basic security on the server then clients just need to know the password (and username if it isn’t the default). You can do that via the config server URI, or via separate username and password properties, e.g.

bootstrap.yml
spring:
  cloud:
    config:
     uri: https://user:secret@myconfig.mycompany.com
or

bootstrap.yml
spring:
  cloud:
    config:
     uri: https://myconfig.mycompany.com
     username: user
     password: secret
The spring.cloud.config.password and spring.cloud.config.username values override anything that is provided in the URI.

If you deploy your apps on Cloud Foundry then the best way to provide the password is through service credentials, e.g. in the URI, since then it doesn’t even need to be in a config file. An example which works locally and for a user-provided service on Cloud Foundry named "configserver":

bootstrap.yml
spring:
  cloud:
    config:
     uri: ${vcap.services.configserver.credentials.uri:http://user:password@localhost:8888}
If you use another form of security you might need to provide a RestTemplate to the ConfigServicePropertySourceLocator (e.g. by grabbing it in the bootstrap context and injecting one).

Spring Cloud Netflix

This project provides Netflix OSS integrations for Spring Boot apps through autoconfiguration and binding to the Spring Environment and other Spring programming model idioms. With a few simple annotations you can quickly enable and configure the common patterns inside your application and build large distributed systems with battle-tested Netflix components. The patterns provided include Service Discovery (Eureka), Circuit Breaker (Hystrix), Intelligent Routing (Zuul) and Client Side Load Balancing (Ribbon).
Service Discovery: Eureka Clients
Service Discovery is one of the key tenets of a microservice based architecture. Trying to hand configure each client or some form of convention can be very difficult to do and can be very brittle. Eureka is the Netflix Service Discovery Server and Client. The server can be configured and deployed to be highly available, with each server replicating state about the registered services to the others.

Registering with Eureka
When a client registers with Eureka, it provides meta-data about itself such as host and port, health indicator URL, home page etc. Eureka receives heartbeat messages from each instance belonging to a service. If the heartbeat fails over a configurable timetable, the instance is normally removed from the registry.

Example eureka client:

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
(i.e. utterly normal Spring Boot app). In this example we use @EnableEurekaClient explicitly, but with only Eureka available you could also use @EnableDiscoveryClient. Configuration is required to locate the Eureka server. Example:

application.yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
where "defaultZone" is a magic string fallback value that provides the service URL for any client that doesn’t express a preference (i.e. it’s a useful default).

The default application name (service ID), virtual host and non-secure port, taken from the Environment, are ${spring.application.name}, ${spring.application.name} and ${server.port} respectively.

@EnableEurekaClient makes the app into both a Eureka "instance" (i.e. it registers itself) and a "client" (i.e. it can query the registry to locate other services). The instance behaviour is driven by eureka.instance.* configuration keys, but the defaults will be fine if you ensure that your application has a spring.application.name (this is the default for the Eureka service ID, or VIP).

See EurekaInstanceConfigBean and EurekaClientConfigBean for more details of the configurable options.

Status Page and Health Indicator
The status page and health indicators for a Eureka instance default to "/info" and "/health" respectively, which are the default locations of useful endpoints in a Spring Boot Actuator application. You need to change these, even for an Actuator application if you use a non-default context path or servlet path (e.g. server.servletPath=/foo) or management endpoint path (e.g. management.contextPath=/admin). Example:

application.yml
eureka:
  instance:
    statusPageUrlPath: ${management.context-path}/info
    healthCheckUrlPath: ${management.context-path}/health
These links show up in the metadata that is consumed by clients, and used in some scenarios to decide whether to send requests to your application, so it’s helpful if they are accurate.

Registering a Secure Application
If your app wants to be contacted over HTTPS you can set two flags in the EurekaInstanceConfig, viz eureka.instance.[nonSecurePortEnabled,securePortEnabled]=[false,true] respectively. This will make Eureka publish instance information showing an explicit preference for secure communication. The Spring Cloud DiscoveryClient will always return an https://…​; URI for a service configured this way, and the Eureka (native) instance information will have a secure health check URL.

Because of the way Eureka works internally, it will still publish a non-secure URL for status and home page unless you also override those explicitly. You can use placeholders to configure the eureka instance urls, e.g.

application.yml
eureka:
  instance:
    statusPageUrl: https://${eureka.hostname}/info
    healthCheckUrl: https://${eureka.hostname}/health
    homePageUrl: https://${eureka.hostname}/
(Note that ${eureka.hostname} is a native placeholder only available in later versions of Eureka. You could achieve the same thing with Spring placeholders as well, e.g. using ${eureka.instance.hostName}.)

NOTE
If your app is running behind a proxy, and the SSL termination is in the proxy (e.g. if you run in Cloud Foundry or other platforms as a service) then you will need to ensure that the proxy "forwarded" headers are intercepted and handled by the application. An embedded Tomcat container in a Spring Boot app does this automatically if it has explicit configuration for the 'X-Forwarded-\*` headers. A sign that you got this wrong will be that the links rendered by your app to itself will be wrong (the wrong host, port or protocol).
Eureka’s Health Checks
By default, Eureka uses the client heartbeat to determine if a client is up. Unless specified otherwise the Discovery Client will not propagate the current health check status of the application per the Spring Boot Actuator. Which means that after successful registration Eureka will always announce that the application is in 'UP' state. This behaviour can be altered by enabling Eureka health checks, which results in propagating application status to Eureka. As a consequence every other application won’t be sending traffic to application in state other then 'UP'.

application.yml
eureka:
  client:
    healthcheck:
      enabled: true
If you require more control over the health checks, you may consider implementing your own com.netflix.appinfo.HealthCheckHandler.

Eureka Metadata for Instances and Clients
It’s worth spending a bit of time understanding how the Eureka metadata works, so you can use it in a way that makes sense in your platform. There is standard metadata for things like hostname, IP address, port numbers, status page and health check. These are published in the service registry and used by clients to contact the services in a straightforward way. Additional metadata can be added to the instance registration in the eureka.instance.metadataMap, and this will be accessible in the remote clients, but in general will not change the behaviour of the client, unless it is made aware of the meaning of the metadata. There are a couple of special cases described below where Spring Cloud already assigns meaning to the metadata map.

Using Eureka on Cloudfoundry
Cloudfoundry has a global router so that all instances of the same app have the same hostname (it’s the same in other PaaS solutions with a similar architecture). This isn’t necessarily a barrier to using Eureka, but if you use the router (recommended, or even mandatory depending on the way your platform was set up), you need to explicitly set the hostname and port numbers (secure or non-secure) so that they use the router. You might also want to use instance metadata so you can distinguish between the instances on the client (e.g. in a custom load balancer). By default, the eureka.instance.instanceId is vcap.application.instance_id. For example:

application.yml
eureka:
  instance:
    hostname: ${vcap.application.uris[0]}
    nonSecurePort: 80
Depending on the way the security rules are set up in your Cloudfoundry instance, you might be able to register and use the IP address of the host VM for direct service-to-service calls. This feature is not (yet) available on Pivotal Web Services (PWS).

Using Eureka on AWS
If the application is planned to be deployed to an AWS cloud, then the Eureka instance will have to be configured to be Amazon aware and this can be done by customizing the EurekaInstanceConfigBean the following way:

@Bean
@Profile("!default")
public EurekaInstanceConfigBean eurekaInstanceConfig() {
  EurekaInstanceConfigBean b = new EurekaInstanceConfigBean();
  AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
  b.setDataCenterInfo(info);
  return b;
}
Changing the Eureka Instance ID
A vanilla Netflix Eureka instance is registered with an ID that is equal to its host name (i.e. only one service per host). Spring Cloud Eureka provides a sensible default that looks like this: ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}. For example myhost:myappname:8080.

Using Spring Cloud you can override this by providing a unique identifier in eureka.instance.instanceId. For example:

application.yml
eureka:
  instance:
    instanceId: ${spring.application.name}:${spring.application.instance_id:${random.value}}
With this metadata, and multiple service instances deployed on localhost, the random value will kick in there to make the instance unique. In Cloudfoundry the spring.application.instance_id will be populated automatically in a Spring Boot Actuator application, so the random value will not be needed.

Using the EurekaClient
Once you have an app that is @EnableDiscoveryClient (or @EnableEurekaClient) you can use it to discover service instances from the Eureka Server. One way to do that is to use the native com.netflix.discovery.EurekaClient (as opposed to the Spring Cloud DiscoveryClient), e.g.

@Autowired
private EurekaClient discoveryClient;

public String serviceUrl() {
    InstanceInfo instance = discoveryClient.getNextServerFromEureka("STORES", false);
    return instance.getHomePageUrl();
}
TIP
Don’t use the EurekaClient in @PostConstruct method or in a @Scheduled method (or anywhere where the ApplicationContext might not be started yet). It is initialized in a SmartLifecycle (with phase=0) so the earliest you can rely on it being available is in another SmartLifecycle with higher phase.
Alternatives to the native Netflix EurekaClient
You don’t have to use the raw Netflix EurekaClient and usually it is more convenient to use it behind a wrapper of some sort. Spring Cloud has support for Feign (a REST client builder) and also Spring RestTemplate using the logical Eureka service identifiers (VIPs) instead of physical URLs. To configure Ribbon with a fixed list of physical servers you can simply set <client>.ribbon.listOfServers to a comma-separated list of physical addresses (or hostnames), where <client> is the ID of the client.

You can also use the org.springframework.cloud.client.discovery.DiscoveryClient which provides a simple API for discovery clients that is not specific to Netflix, e.g.

@Autowired
private DiscoveryClient discoveryClient;

public String serviceUrl() {
    List<ServiceInstance> list = client.getInstances("STORES");
    if (list != null && list.size() > 0 ) {
        return list.get(0).getUri();
    }
    return null;
}
Why is it so Slow to Register a Service?
Being an instance also involves a periodic heartbeat to the registry (via the client’s serviceUrl) with default duration 30 seconds. A service is not available for discovery by clients until the instance, the server and the client all have the same metadata in their local cache (so it could take 3 hearbeats). You can change the period using eureka.instance.leaseRenewalIntervalInSeconds and this will speed up the process of getting clients connected to other services. In production it’s probably better to stick with the default because there are some computations internally in the server that make assumptions about the lease renewal period.

Service Discovery: Eureka Server
Example eureka server (e.g. using spring-cloud-starter-eureka-server to set up the classpath):

@SpringBootApplication
@EnableEurekaServer
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
The server has a home page with a UI, and HTTP API endpoints per the normal Eureka functionality under /eureka/*.

Eureka background reading: see flux capacitor and google group discussion.

TIP
Due to Gradle’s dependency resolution rules and the lack of a parent bom feature, simply depending on spring-cloud-starter-eureka-server can cause failures on application startup. To remedy this the Spring dependency management plugin must be added and the Spring cloud starter parent bom must be imported like so:

build.gradle
buildscript {
  dependencies {
    classpath "io.spring.gradle:dependency-management-plugin:0.4.0.RELEASE"
  }
}

apply plugin: "io.spring.dependency-management"

dependencyManagement {
  imports {
    mavenBom 'org.springframework.cloud:spring-cloud-starter-parent:1.0.0.RELEASE'
  }
}
High Availability, Zones and Regions
The Eureka server does not have a backend store, but the service instances in the registry all have to send heartbeats to keep their registrations up to date (so this can be done in memory). Clients also have an in-memory cache of eureka registrations (so they don’t have to go to the registry for every single request to a service).

By default every Eureka server is also a Eureka client and requires (at least one) service URL to locate a peer. If you don’t provide it the service will run and work, but it will shower your logs with a lot of noise about not being able to register with the peer.

See also below for details of Ribbon support on the client side for Zones and Regions.

Standalone Mode
The combination of the two caches (client and server) and the heartbeats make a standalone Eureka server fairly resilient to failure, as long as there is some sort of monitor or elastic runtime keeping it alive (e.g. Cloud Foundry). In standalone mode, you might prefer to switch off the client side behaviour, so it doesn’t keep trying and failing to reach its peers. Example:

application.yml (Standalone Eureka Server)
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
Notice that the serviceUrl is pointing to the same host as the local instance.

Peer Awareness
Eureka can be made even more resilient and available by running multiple instances and asking them to register with each other. In fact, this is the default behaviour, so all you need to do to make it work is add a valid serviceUrl to a peer, e.g.

application.yml (Two Peer Aware Eureka Servers)
---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: http://peer2/eureka/

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: http://peer1/eureka/
In this example we have a YAML file that can be used to run the same server on 2 hosts (peer1 and peer2), by running it in different Spring profiles. You could use this configuration to test the peer awareness on a single host (there’s not much value in doing that in production) by manipulating /etc/hosts to resolve the host names. In fact, the eureka.instance.hostname is not needed if you are running on a machine that knows its own hostname (it is looked up using java.net.InetAddress by default).

You can add multiple peers to a system, and as long as they are all connected to each other by at least one edge, they will synchronize the registrations amongst themselves. If the peers are physically separated (inside a data centre or between multiple data centres) then the system can in principle survive split-brain type failures.

Prefer IP Address
In some cases, it is preferable for Eureka to advertise the IP Adresses of services rather than the hostname. Set eureka.instance.preferIpAddress to true and when the application registers with eureka, it will use its IP Address rather than its hostname.

Circuit Breaker: Hystrix Clients
Netflix has created a library called Hystrix that implements the circuit breaker pattern. In a microservice architecture it is common to have multiple layers of service calls.

HystrixGraph
Figure 1. Microservice Graph
A service failure in the lower level of services can cause cascading failure all the way up to the user. When calls to a particular service reach a certain threshold (20 failures in 5 seconds is the default in Hystrix), the circuit opens and the call is not made. In cases of error and an open circuit a fallback can be provided by the developer.

HystrixFallback
Figure 2. Hystrix fallback prevents cascading failures
Having an open circuit stops cascading failures and allows overwhelmed or failing services time to heal. The fallback can be another Hystrix protected call, static data or a sane empty value. Fallbacks may be chained so the first fallback makes some other business call which in turn falls back to static data.

Example boot app:

@SpringBootApplication
@EnableCircuitBreaker
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}

@Component
public class StoreIntegration {

    @HystrixCommand(fallbackMethod = "defaultStores")
    public Object getStores(Map<String, Object> parameters) {
        //do stuff that might fail
    }

    public Object defaultStores(Map<String, Object> parameters) {
        return /* something useful */;
    }
}
The @HystrixCommand is provided by a Netflix contrib library called "javanica". Spring Cloud automatically wraps Spring beans with that annotation in a proxy that is connected to the Hystrix circuit breaker. The circuit breaker calculates when to open and close the circuit, and what to do in case of a failure.

To configure the @HystrixCommand you can use the commandProperties attribute with a list of @HystrixProperty annotations. See here for more details. See the Hystrix wiki for details on the properties available.

Propagating the Security Context or using Spring Scopes
If you want some thread local context to propagate into a @HystrixCommand the default declaration will not work because it executes the command in a thread pool (in case of timeouts). You can switch Hystrix to use the same thread as the caller using some configuration, or directly in the annotation, by asking it to use a different "Isolation Strategy". For example:

@HystrixCommand(fallbackMethod = "stubMyService",
    commandProperties = {
      @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")
    }
)
...
The same thing applies if you are using @SessionScope or @RequestScope. You will know when you need to do this because of a runtime exception that says it can’t find the scoped context.

Health Indicator
The state of the connected circuit breakers are also exposed in the /health endpoint of the calling application.

{
    "hystrix": {
        "openCircuitBreakers": [
            "StoreIntegration::getStoresByLocationLink"
        ],
        "status": "CIRCUIT_OPEN"
    },
    "status": "UP"
}
Hystrix Metrics Stream
To enable the Hystrix metrics stream include a dependency on spring-boot-starter-actuator. This will expose the /hystrix.stream as a management endpoint.

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
Circuit Breaker: Hystrix Dashboard
One of the main benefits of Hystrix is the set of metrics it gathers about each HystrixCommand. The Hystrix Dashboard displays the health of each circuit breaker in an efficient manner.

Hystrix
Figure 3. Hystrix Dashboard
To run the Hystrix Dashboard annotate your Spring Boot main class with @EnableHystrixDashboard. You then visit /hystrix and point the dashboard to an individual instances /hystrix.stream endpoint in a Hystrix client application.

Turbine
Looking at an individual instances Hystrix data is not very useful in terms of the overall health of the system. Turbine is an application that aggregates all of the relevant /hystrix.stream endpoints into a combined /turbine.stream for use in the Hystrix Dashboard. Individual instances are located via Eureka. Running Turbine is as simple as annotating your main class with the @EnableTurbine annotation (e.g. using spring-cloud-starter-turbine to set up the classpath). All of the documented configuration properties from the Turbine 1 wiki apply. The only difference is that the turbine.instanceUrlSuffix does not need the port prepended as this is handled automatically unless turbine.instanceInsertPort=false.

The configuration key turbine.appConfig is a list of eureka serviceIds that turbine will use to lookup instances. The turbine stream is then used in the Hystrix dashboard using a url that looks like: http://my.turbine.sever:8080/turbine.stream?cluster=<CLUSTERNAME>; (the cluster parameter can be omitted if the name is "default"). The cluster parameter must match an entry in turbine.aggregator.clusterConfig. Values returned from eureka are uppercase, thus we expect this example to work if there is an app registered with Eureka called "customers":

turbine:
  aggregator:
    clusterConfig: CUSTOMERS
  appConfig: customers
The clusterName can be customized by a SPEL expression in turbine.clusterNameExpression with root an instance of InstanceInfo. The default value is appName, which means that the Eureka serviceId ends up as the cluster key (i.e. the InstanceInfo for customers has an appName of "CUSTOMERS"). A different example would be turbine.clusterNameExpression=aSGName, which would get the cluster name from the AWS ASG name. Another example:

turbine:
  aggregator:
    clusterConfig: SYSTEM,USER
  appConfig: customers,stores,ui,admin
  clusterNameExpression: metadata['cluster']
In this case, the cluster name from 4 services is pulled from their metadata map, and is expected to have values that include "SYSTEM" and "USER".

To use the "default" cluster for all apps you need a string literal expression (with single quotes):

turbine:
  appConfig: customers,stores
  clusterNameExpression: 'default'
Spring Cloud provides a spring-cloud-starter-turbine that has all the dependencies you need to get a Turbine server running. Just create a Spring Boot application and annotate it with @EnableTurbine.

Turbine AMQP
In some environments (e.g. in a PaaS setting), the classic Turbine model of pulling metrics from all the distributed Hystrix commands doesn’t work. In that case you might want to have your Hystrix commands push metrics to Turbine, and Spring Cloud enables that with AMQP messaging. All you need to do on the client is add a dependency to spring-cloud-netflix-hystrix-amqp and make sure there is a Rabbit broker available (see Spring Boot documentation for details on how to configure the client credentials, but it should work out of the box for a local broker or in Cloud Foundry).

On the server side Just create a Spring Boot application and annotate it with @EnableTurbineAmqp and by default it will come up on port 8989 (point your Hystrix dashboard to that port, any path). You can customize the port using either server.port or turbine.amqp.port. If you have spring-boot-starter-web and spring-boot-starter-actuator on the classpath as well, then you can open up the Actuator endpoints on a separate port (with Tomcat by default) by providing a management.port which is different.

You can then point the Hystrix Dashboard to the Turbine AMQP Server instead of individual Hystrix streams. If Turbine AMQP is running on port 8989 on myhost, then put http://myhost:8989 in the stream input field in the Hystrix Dashboard. Circuits will be prefixed by their respective serviceId, followed by a dot, then the circuit name.

Spring Cloud provides a spring-cloud-starter-turbine-amqp that has all the dependencies you need to get a Turbine AMQP server running. You need Java 8 to run the app because it is Netty-based.

Customizing the AMQP ConnectionFactory
If you are using AMQP there needs to be a ConnectionFactory (from Spring Rabbit) in the application context. If there is a single ConnectionFactory it will be used, or if there is a one qualified as @HystrixConnectionFactory (on the client) and @TurbineConnectionFactory (on the server) it will be preferred over others, otherwise the @Primary one will be used. If there are multiple unqualified connection factories there will be an error.

Note that Spring Boot (as of 1.2.2) creates a ConnectionFactory that is not @Primary, so if you want to use one connection factory for the bus and another for business messages, you need to create both, and annotate them @*ConnectionFactory and @Primary respectively.

Client Side Load Balancer: Ribbon
Ribbon is a client side load balancer which gives you a lot of control over the behaviour of HTTP and TCP clients. Feign already uses Ribbon, so if you are using @FeignClient then this section also applies.

A central concept in Ribbon is that of the named client. Each load balancer is part of an ensemble of components that work together to contact a remote server on demand, and the ensemble has a name that you give it as an application developer (e.g. using the @FeignClient annotation). Spring Cloud creates a new ensemble as an ApplicationContext on demand for each named client using RibbonClientConfiguration. This contains (amongst other things) an ILoadBalancer, a RestClient, and a ServerListFilter.

Customizing the Ribbon Client
You can configure some bits of a Ribbon client using external properties in <client>.ribbon.*, which is no different than using the Netflix APIs natively, except that you can use Spring Boot configuration files. The native options can be inspected as static fields in CommonClientConfigKey (part of ribbon-core).

Spring Cloud also lets you take full control of the client by declaring additional configuration (on top of the RibbonClientConfiguration) using @RibbonClient. Example:

@Configuration
@RibbonClient(name = "foo", configuration = FooConfiguration.class)
public class TestConfiguration {
}
In this case the client is composed from the components already in RibbonClientConfiguration together with any in FooConfiguration (where the latter generally will override the former).

WARNING
The FooConfiguration has to be @Configuration but take care that it is not in a @ComponentScan for the main application context, otherwise it will be shared by all the @RibbonClients. If you use @ComponentScan (or @SpringBootApplication) you need to take steps to avoid it being included (for instance put it in a separate, non-overlapping package, or specify the packages to scan explicitly in the @ComponentScan).
Spring Cloud Netflix provides the following beans by default for ribbon (BeanType beanName: ClassName):

IClientConfig ribbonClientConfig: DefaultClientConfigImpl

IRule ribbonRule: ZoneAvoidanceRule

IPing ribbonPing: NoOpPing

ServerList<Server> ribbonServerList: ConfigurationBasedServerList

ServerListFilter<Server> ribbonServerListFilter: ZonePreferenceServerListFilter

ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

Creating a bean of one of those type and placing it in a @RibbonClient configuration (such as FooConfiguration above) allows you to override each one of the beans described. Example:

@Configuration
public class FooConfiguration {
    @Bean
    public IPing ribbonPing(IClientConfig config) {
        return new PingUrl();
    }
}
This replaces the NoOpPing with PingUrl.

Using Ribbon with Eureka
When Eureka is used in conjunction with Ribbon the ribbonServerList is overridden with an extension of DiscoveryEnabledNIWSServerList which populates the list of servers from Eureka. It also replaces the IPing interface with NIWSDiscoveryPing which delegates to Eureka to determine if a server is up. The ServerList that is installed by default is a DomainExtractingServerList and the purpose of this is to make physical metadata available to the load balancer without using AWS AMI metadata (which is what Netflix relies on). By default the server list will be constructed with "zone" information as provided in the instance metadata (so on the client set eureka.instance.metadataMap.zone), and if that is missing it can use the domain name from the server hostname as a proxy for zone (if the flag approximateZoneFromHostname is set). Once the zone information is available it can be used in a ServerListFilter (by default it will be used to locate a server in the same zone as the client because the default is a ZonePreferenceServerListFilter).

Example: How to Use Ribbon Without Eureka
Eureka is a convenient way to abstract the discovery of remote servers so you don’t have to hard code their URLs in clients, but if you prefer not to use it, Ribbon and Feign are still quite amenable. Suppose you have declared a @RibbonClient for "stores", and Eureka is not in use (and not even on the classpath). The Ribbon client defaults to a configured server list, and you can supply the configuration like this

application.yml
stores:
  ribbon:
    listOfServers: example.com,google.com
Example: Disable Eureka use in Ribbon
Setting the property ribbon.eureka.enabled = false will explicitly disable the use of Eureka in Ribbon.

application.yml
ribbon:
  eureka:
   enabled: false
Using the Ribbon API Directly
You can also use the LoadBalancerClient directly. Example:

public class MyClass {
    @Autowired
    private LoadBalancerClient loadBalancer;

    public void doStuff() {
        ServiceInstance instance = loadBalancer.choose("stores");
        URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
        // ... do something with the URI
    }
}
Declarative REST Client: Feign
Feign is a declarative web service client. It makes writing web service clients easier. To use Feign create an interface and annotate it. It has pluggable annotation support including Feign annotations and JAX-RS annotations. Feign also supports pluggable encoders and decoders. Spring Cloud adds support for Spring MVC annotations and for using the same HttpMessageConverters used by default in Spring Web. Spring Cloud integrates Ribbon and Eureka to provide a load balanced http client when using Feign.

Example spring boot app

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
StoreClient.java
@FeignClient("stores")
public interface StoreClient {
    @RequestMapping(method = RequestMethod.GET, value = "/stores")
    List<Store> getStores();

    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Long storeId, Store store);
}
In the @FeignClient annotation the String value ("stores" above) is an arbitrary client name, which is used to create a Ribbon load balancer (see below for details of Ribbon support). You can also specify a URL using the url attribute (absolute value or just a hostname).

The Ribbon client above will want to discover the physical addresses for the "stores" service. If your application is a Eureka client then it will resolve the service in the Eureka service registry. If you don’t want to use Eureka, you can simply configure a list of servers in your external configuration (see above for example).

Overriding Feign Defaults
A central concept in Spring Cloud’s Feign support is that of the named client. Each feign client is part of an ensemble of components that work together to contact a remote server on demand, and the ensemble has a name that you give it as an application developer using the @FeignClient annotation. Spring Cloud creates a new ensemble as an ApplicationContext on demand for each named client using FeignClientsConfiguration. This contains (amongst other things) an feign.Decoder, a feign.Encoder, and a feign.Contract.

Spring Cloud lets you take full control of the feign client by declaring additional configuration (on top of the FeignClientsConfiguration) using @FeignClient. Example:

@FeignClient(name = "stores", configuration = FooConfiguration.class)
public interface StoreClient {
    //..
}
In this case the client is composed from the components already in FeignClientsConfiguration together with any in FooConfiguration (where the latter will override the former).

WARNING
The FooConfiguration has to be @Configuration but take care that it is not in a @ComponentScan for the main application context, otherwise it will be used for every @FeignClient. If you use @ComponentScan (or @SpringBootApplication) you need to take steps to avoid it being included (for instance put it in a separate, non-overlapping package, or specify the packages to scan explicitly in the @ComponentScan).
NOTE
The serviceId attribute is now deprecated in favor of the name attribute.
WARNING
Previously, using the url attribute, did not require the name attribute. Using name is now required.
Placeholders are supported in the name and url attributes.

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface StoreClient {
    //..
}
Spring Cloud Netflix provides the following beans by default for feign (BeanType beanName: ClassName):

Decoder feignDecoder: ResponseEntityDecoder (which wraps a SpringDecoder)

Encoder feignEncoder: SpringEncoder

Logger feignLogger: Slf4jLogger

Contract feignContract: SpringMvcContract

Feign.Builder feignBuilder: HystrixFeign.Builder

Spring Cloud Netflix does not provide the following beans by default for feign, but still looks up beans of these types from the application context to create the feign client:

Logger.Level

Retryer

ErrorDecoder

Request.Options

Collection<RequestInterceptor>

Creating a bean of one of those type and placing it in a @FeignClient configuration (such as FooConfiguration above) allows you to override each one of the beans described. Example:

@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContractg() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}
This replaces the SpringMvcContract with feign.Contract.Default and adds a RequestInterceptor to the collection of RequestInterceptor.

Default configurations can be specified in the @EnableFeignClients attribute defaultConfiguration in a similar manner as described above. The difference is that this configuration will apply to all feign clients.

Feign Hystrix Support
If Hystrix is on the classpath, by default Feign will wrap all methods with a circuit breaker. Returning a com.netflix.hystrix.HystrixCommand is also available. This lets you use reactive patterns (with a call to .toObservable() or .observe() or asynchronous use (with a call to .queue()).

To disable Hystrix support for Feign, set feign.hystrix.enabled=false.

To disable Hystrix support on a per-client basis create a vanilla Feign.Builder with the "prototype" scope, e.g.:

@Configuration
public class FooConfiguration {
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
Feign Hystrix Fallbacks
Hystrix supports the notion of a fallback: a default code path that is executed when they circuit is open or there is an error. To enable fallbacks for a given @FeignClient set the fallback attribute to the class name that implements the fallback.

@FeignClient(name = "hello", fallback = HystrixClientFallback.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

static class HystrixClientFallback implements HystrixClient {
    @Override
    public Hello iFailSometimes() {
        return new Hello("fallback");
    }
}
WARNING
There is a limitation with the implementation of fallbacks in Feign and how Hystrix fallbacks work. Fallbacks are currently not supported for methods that return com.netflix.hystrix.HystrixCommand and rx.Observable.
Feign Inheritance Support
Feign supports boilerplate apis via single-inheritance interfaces. This allows grouping common operations into convenient base interfaces. Together with Spring MVC you can share the same contract for your REST endpoint and Feign client.

UserService.java
public interface UserService {

    @RequestMapping(method = RequestMethod.GET, value ="/users/{id}")
    User getUser(@PathVariable("id") long id);
}
UserResource.java
@RestController
public class UserResource implements UserService {

}
UserClient.java
package project.user;

@FeignClient("users")
public interface UserClient extends UserService {

}
Feign request/response compression
You may consider enabling the request or response GZIP compression for your Feign requests. You can do this by enabling one of the properties:

feign.compression.request.enabled=true
feign.compression.response.enabled=true
Feign request compression gives you settings similar to what you may set for your web server:

feign.compression.request.enabled=true
feign.compression.request.mime-types=text/xml,application/xml,application/json
feign.compression.request.min-request-size=2048
These properties allow you to be selective about the compressed media types and minimum request threshold length.

Feign logging
A logger is created for each Feign client created. By default the name of the logger is the full class name of the interface used to create the Feign client. Feign logging only responds to the DEBUG level.

application.yml
logging.level.project.user.UserClient: DEBUG
The Logger.Level object that you may configure per client, tells Feign how much to log. Choices are:

NONE, No logging (DEFAULT).

BASIC, Log only the request method and URL and the response status code and execution time.

HEADERS, Log the basic information along with request and response headers.

FULL, Log the headers, body, and metadata for both requests and responses.

For example, the following would set the Logger.Level to FULL:

@Configuration
public class FooConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
External Configuration: Archaius
Archaius is the Netflix client side configuration library. It is the library used by all of the Netflix OSS components for configuration. Archaius is an extension of the Apache Commons Configuration project. It allows updates to configuration by either polling a source for changes or for a source to push changes to the client. Archaius uses Dynamic<Type>Property classes as handles to properties.

Archaius Example
class ArchaiusTest {
    DynamicStringProperty myprop = DynamicPropertyFactory
            .getInstance()
            .getStringProperty("my.prop");

    void doSomething() {
        OtherClass.someMethod(myprop.get());
    }
}
Archaius has its own set of configuration files and loading priorities. Spring applications should generally not use Archaius directly, but the need to configure the Netflix tools natively remains. Spring Cloud has a Spring Environment Bridge so Archaius can read properties from the Spring Environment. This allows Spring Boot projects to use the normal configuration toolchain, while allowing them to configure the Netflix tools, for the most part, as documented.

Router and Filter: Zuul
Routing in an integral part of a microservice architecture. For example, / may be mapped to your web application, /api/users is mapped to the user service and /api/shop is mapped to the shop service. Zuul is a JVM based router and server side load balancer by Netflix.

Netflix uses Zuul for the following:

Authentication

Insights

Stress Testing

Canary Testing

Dynamic Routing

Service Migration

Load Shedding

Security

Static Response handling

Active/Active traffic management

Zuul’s rule engine allows rules and filters to be written in essentially any JVM language, with built in support for Java and Groovy.

Embedded Zuul Reverse Proxy
Spring Cloud has created an embedded Zuul proxy to ease the development of a very common use case where a UI application wants to proxy calls to one or more back end services. This feature is useful for a user interface to proxy to the backend services it requires, avoiding the need to manage CORS and authentication concerns independently for all the backends.

To enable it, annotate a Spring Boot main class with @EnableZuulProxy, and this forwards local calls to the appropriate service. By convention, a service with the Eureka ID "users", will receive requests from the proxy located at /users (with the prefix stripped). The proxy uses Ribbon to locate an instance to forward to via Eureka, and all requests are executed in a hystrix command, so failures will show up in Hystrix metrics, and once the circuit is open the proxy will not try to contact the service.

To skip having a service automatically added, set zuul.ignored-services to a list of service id patterns. If a service matches a pattern that is ignored, but also included in the explicitly configured routes map, then it will be unignored. Example:

application.yml
 zuul:
  ignoredServices: '*'
  routes:
    users: /myusers/**
In this example, all services are ignored except "users".

To augment or change the proxy routes, you can add external configuration like the following:

application.yml
 zuul:
  routes:
    users: /myusers/**
This means that http calls to "/myusers" get forwarded to the "users" service (for example "/myusers/101" is forwarded to "/101").

To get more fine-grained control over a route you can specify the path and the serviceId independently:

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users_service
This means that http calls to "/myusers" get forwarded to the "users_service" service. The route has to have a "path" which can be specified as an ant-style pattern, so "/myusers/*" only matches one level, but "/myusers/{asterisk}{asterisk}" matches hierarchically.

The location of the backend can be specified as either a "serviceId" (for a Eureka service) or a "url" (for a physical location), e.g.

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      url: http://example.com/users_service
These simple url-routes doesn’t get executed as HystrixCommand nor can you loadbalance multiple url with Ribbon. To achieve this specify a service-route and configure a Ribbon client for the serviceId (this currently requires disabling Eureka support in Ribbon: see above for more information), e.g.

application.yml
zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users

ribbon:
  eureka:
    enabled: false

users:
  ribbon:
    listOfServers: example.com,google.com
You can provide convention between serviceId and routes using regexmapper. It uses regular expression named group to extract variables from serviceId and inject them into a route pattern.

ApplicationConfiguration.java
@Bean
public PatternServiceRouteMapper serviceRouteMapper() {
    return new PatternServiceRouteMapper(
        "(?<name>^.+)-(?<version>v.+$)",
        "${version}/${name}");
}
This means that a serviceId "myusers-v1" will be mapped to route "/v1/myusers/{asterisk}{asterisk}". Any regular expression is accepted but all named group must be present in both servicePattern and routePattern. If servicePattern do not match a serviceId, the default behavior is used. In exemple above, a serviceId "myusers" will be mapped to route "/myusers/{asterisk}{asterisk}" (no version detected) These feature is disable by default and is only applied to discovered services.

To add a prefix to all mappings, set zuul.prefix to a value, such as /api. The proxy prefix is stripped from the request before the request is forwarded by default (switch this behaviour off with zuul.stripPrefix=false). You can also switch off the stripping of the service-specific prefix from individual routes, e.g.

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      stripPrefix: false
In this example requests to "/myusers/101" will be forwarded to "/myusers/101" on the "users" service.

The zuul.routes entries actually bind to an object of type ProxyRouteLocator. If you look at the properties of that object you will see that it also has a "retryable" flag. Set that flag to "true" to have the Ribbon client automatically retry failed requests (and if you need to you can modify the parameters of the retry operations using the Ribbon client configuration).

The X-Forwarded-Host header is added to the forwarded requests by default. To turn it off set zuul.addProxyHeaders = false. The prefix path is stripped by default, and the request to the backend picks up a header "X-Forwarded-Prefix" ("/myusers" in the examples above).

An application with the @EnableZuulProxy could act as a standalone server if you set a default route ("/"), for example zuul.route.home: / would route all traffic (i.e. "/{asterisk}{asterisk}") to the "home" service.

If more fine-grained ignoring is needed, you can specify specific patterns to ignore. These patterns are being evaluated at the start of the route location process, which means prefixes should be included in the pattern to warrant a match. Ignored patterns span all services and supersede any other route specification.

application.yml
 zuul:
  ignoredPatterns: /**/admin/**
  routes:
    users: /myusers/**
This means that all calls such as "/myusers/101" will be forwarded to "/101" on the "users" service. But calls including "/admin/" will not resolve.

Strangulation Patterns and Local Forwards
A common pattern when migrating an existing application or API is to "strangle" old endpoints, slowly replacing them with different implementations. The Zuul proxy is a useful tool for this because you can use it to handle all traffic from clients of the old endpoints, but redirect some of the requests to new ones.

Example configuration:

application.yml
 zuul:
  routes:
    first:
      path: /first/**
      url: http://first.example.com
    second:
      path: /second/**
      url: forward:/second
    third:
      path: /third/**
      url: forward:/3rd
    legacy:
      path: /**
      url: http://legacy.example.com
In this example we are strangling the "legacy" app which is mapped to all requests that do not match one of the other patterns. Paths in /first/{asterisk}{asterisk} have been extracted into a new service with an external URL. And paths in /second/{asterisk}{asterisk} are forwared so they can be handled locally, e.g. with a normal Spring @RequestMapping. Paths in /third/{asterisk}{asterisk} are also forwarded, but with a different prefix (i.e. /third/foo is forwarded to /3rd/foo).

NOTE
The ignored pattterns aren’t completely ignored, they just aren’t handled by the proxy (so they are also effectively forwarded locally).
Uploading Files through Zuul
If you @EnableZuulProxy you can use the proxy paths to upload files and it should just work as long as the files are small. For large files there is an alternative path which bypasses the Spring DispatcherServlet (to avoid multipart processing) in "/zuul/*". I.e. if zuul.routes.customers=/customers/{asterisk}{asterisk} then you can POST large files to "/zuul/customers/*". The servlet path is externalized via zuul.servletPath. Extremely large files will also require elevated timeout settings if the proxy route takes you through a Ribbon load balancer, e.g.

application.yml
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
Note that for streaming to work with large files, you need to use chunked encoding in the request (which some browsers do not do by default). E.g. on the command line:

$ curl -v -H "Transfer-Encoding: chunked" \
    -F "file=@mylarge.iso" localhost:9999/zuul/simple/file
Plain Embedded Zuul
You can also run a Zuul server without the proxying, or switch on parts of the proxying platform selectively, if you use @EnableZuulServer (instead of @EnableZuulProxy). Any beans that you add to the application of type ZuulFilter will be installed automatically, as they are with @EnableZuulProxy, but without any of the proxy filters being added automatically.

In this case the routes into the Zuul server are still specified by configuring "zuul.routes.*", but there is no service discovery and no proxying, so the "serviceId" and "url" settings are ignored. For example:

application.yml
 zuul:
  routes:
    api: /api/**
maps all paths in "/api/{asterisk}{asterisk}" to the Zuul filter chain.

Disable Zuul Filters
Zuul for Spring Cloud comes with a number of ZuulFilter beans enabled by default in both proxy and server mode. See the zuul filters package for the possible filters that are enabled. If you want to disable one, simply set zuul.<SimpleClassName>.<filterType>.disable=true. By convention, the package after filters is the Zuul filter type. For example to disable org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter set zuul.SendResponseFilter.post.disable=true.

Polyglot support with Sidecar
Do you have non-jvm languages you want to take advantage of Eureka, Ribbon and Config Server? The Spring Cloud Netflix Sidecar was inspired by Netflix Prana. It includes a simple http api to get all of the instances (ie host and port) for a given service. You can also proxy service calls through an embedded Zuul proxy which gets its route entries from Eureka. The Spring Cloud Config Server can be accessed directly via host lookup or through the Zuul Proxy. The non-jvm app should implement a health check so the Sidecar can report to eureka if the app is up or down.

To enable the Sidecar, create a Spring Boot application with @EnableSidecar. This annotation includes @EnableCircuitBreaker, @EnableDiscoveryClient, and @EnableZuulProxy. Run the resulting application on the same host as the non-jvm application.

To configure the side car add sidecar.port and sidecar.health-uri to application.yml. The sidecar.port property is the port the non-jvm app is listening on. This is so the Sidecar can properly register the app with Eureka. The sidecar.health-uri is a uri accessible on the non-jvm app that mimicks a Spring Boot health indicator. It should return a json document like the following:

health-uri-document
{
  "status":"UP"
}
Here is an example application.yml for a Sidecar application:

application.yml
server:
  port: 5678
spring:
  application:
    name: sidecar

sidecar:
  port: 8000
  health-uri: http://localhost:8000/health.json
The api for the DiscoveryClient.getInstances() method is /hosts/{serviceId}. Here is an example response for /hosts/customers that returns two instances on different hosts. This api is accessible to the non-jvm app (if the sidecar is on port 5678) at http://localhost:5678/hosts/{serviceId}.

/hosts/customers
[
    {
        "host": "myhost",
        "port": 9000,
        "uri": "http://myhost:9000",
        "serviceId": "CUSTOMERS",
        "secure": false
    },
    {
        "host": "myhost2",
        "port": 9000,
        "uri": "http://myhost2:9000",
        "serviceId": "CUSTOMERS",
        "secure": false
    }
]
The Zuul proxy automatically adds routes for each service known in eureka to /<serviceId>, so the customers service is available at /customers. The Non-jvm app can access the customer service via http://localhost:5678/customers (assuming the sidecar is listening on port 5678).

If the Config Server is registered with Eureka, non-jvm application can access it via the Zuul proxy. If the serviceId of the ConfigServer is configserver and the Sidecar is on port 5678, then it can be accessed at http://localhost:5678/configserver

Non-jvm app can take advantage of the Config Server’s ability to return YAML documents. For example, a call to http://sidecar.local.spring.io:5678/configserver/default-master.yml might result in a YAML document like the following

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  password: password
info:
  description: Spring Cloud Samples
  url: https://github.com/spring-cloud-samples
Metrics: Spectator, Servo, and Atlas
When used together, Spectator/Servo and Atlas provide a near real-time operational insight platform.

Spectator and Servo are Netflix’s metrics collection libraries. Atlas is a Netflix metrics backend to manage dimensional time series data.

Servo served Netflix for several years and is still usable, but is gradually being phased out in favor of Spectator, which is only designed to work with Java 8. Spring Cloud Netflix provides support for both, but Java 8 based applications are encouraged to use Spectator.

Dimensional vs. Hierarchical Metrics
Spring Boot Actuator metrics are hierarchical and metrics are separated only by name. These names often follow a naming convention that embeds key/value attribute pairs (dimensions) into the name separated by periods. Consider the following metrics for two endpoints, root and star-star:

{
    "counter.status.200.root": 20,
    "counter.status.400.root": 3,
    "counter.status.200.star-star": 5,
}
The first metric gives us a normalized count of successful requests against the root endpoint per unit of time. But what if the system had 20 endpoints and you want to get a count of successful requests against all the endpoints? Some hierarchical metrics backends would allow you to specify a wild card such as counter.status.200. that would read all 20 metrics and aggregate the results. Alternatively, you could provide a HandlerInterceptorAdapter that intercepts and records a metric like counter.status.200.all for all successful requests irrespective of the endpoint, but now you must write 20+1 different metrics. Similarly if you want to know the total number of successful requests for all endpoints in the service, you could specify a wild card such as counter.status.2.*.

Even in the presence of wildcarding support on a hierarchical metrics backend, naming consistency can be difficult. Specifically the position of these tags in the name string can slip with time, breaking queries. For example, suppose we add an additional dimension to the hierarchical metrics above for HTTP method. Then counter.status.200.root becomes counter.status.200.method.get.root, etc. Our counter.status.200.* suddenly no longer has the same semantic meaning. Furthermore, if the new dimension is not applied uniformly across the codebase, certain queries may become impossible. This can quickly get out of hand.

Netflix metrics are tagged (a.k.a. dimensional). Each metric has a name, but this single named metric can contain multiple statistics and 'tag' key/value pairs that allows more querying flexibility. In fact, the statistics themselves are recorded in a special tag.

Recorded with Netflix Servo or Spectator, a timer for the root endpoint described above contains 4 statistics per status code, where the count statistic is identical to Spring Boot Actuator’s counter. In the event that we have encountered an HTTP 200 and 400 thus far, there will be 8 available data points:

{
    "root(status=200,stastic=count)": 20,
    "root(status=200,stastic=max)": 0.7265630630000001,
    "root(status=200,stastic=totalOfSquares)": 0.04759702862580789,
    "root(status=200,stastic=totalTime)": 0.2093076914666667,
    "root(status=400,stastic=count)": 1,
    "root(status=400,stastic=max)": 0,
    "root(status=400,stastic=totalOfSquares)": 0,
    "root(status=400,stastic=totalTime)": 0,
}
Default Metrics Collection
Without any additional dependencies or configuration, a Spring Cloud based service will autoconfigure a Servo MonitorRegistry and begin collecting metrics on every Spring MVC request. By default, a Servo timer with the name rest will be recorded for each MVC request which is tagged with:

HTTP method

HTTP status (e.g. 200, 400, 500)

URI (or "root" if the URI is empty), sanitized for Atlas

The exception class name, if the request handler threw an exception

The caller, if a request header with a key matching netflix.metrics.rest.callerHeader is set on the request. There is no default key for netflix.metrics.rest.callerHeader. You must add it to your application properties if you wish to collect caller information.

Set the netflix.metrics.rest.metricName property to change the name of the metric from rest to a name you provide.

If Spring AOP is enabled and org.aspectj:aspectjweaver is present on your runtime classpath, Spring Cloud will also collect metrics on every client call made with RestTemplate. A Servo timer with the name of restclient will be recorded for each MVC request which is tagged with:

HTTP method

HTTP status (e.g. 200, 400, 500), "CLIENT_ERROR" if the response returned null, or "IO_ERROR" if an IOException occurred during the execution of the RestTemplate method

URI, sanitized for Atlas

Client name

Metrics Collection: Spectator
To enable Spectator metrics, include a dependency on spring-boot-starter-spectator:

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-spectator</artifactId>
    </dependency>
In Spectator parlance, a meter is a named, typed, and tagged configuration and a metric represents the value of a given meter at a point in time. Spectator meters are created and controlled by a registry, which currently has several different implementations. Spectator provides 4 meter types: counter, timer, gauge, and distribution summary.

Spring Cloud Spectator integration configures an injectable com.netflix.spectator.api.Registry instance for you. Specifically, it configures a ServoRegistry instance in order to unify the collection of REST metrics and the exporting of metrics to the Atlas backend under a single Servo API. Practically, this means that your code may use a mixture of Servo monitors and Spectator meters and both will be scooped up by Spring Boot Actuator MetricReader instances and both will be shipped to the Atlas backend.

Spectator Counter
A counter is used to measure the rate at which some event is occurring.

// create a counter with a name and a set of tags
Counter counter = registry.counter("counterName", "tagKey1", "tagValue1", ...);
counter.increment(); // increment when an event occurs
counter.increment(10); // increment by a discrete amount
The counter records a single time-normalized statistic.

Spectator Timer
A timer is used to measure how long some event is taking. Spring Cloud automatically records timers for Spring MVC requests and conditionally RestTemplate requests, which can later be used to create dashboards for request related metrics like latency:

Request Latency
image::RequestLatency.png []

// create a timer with a name and a set of tags
Timer timer = registry.timer("timerName", "tagKey1", "tagValue1", ...);

// execute an operation and time it at the same time
T result = timer.record(() -> fooReturnsT());

// alternatively, if you must manually record the time
Long start = System.nanoTime();
T result = fooReturnsT();
timer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
The timer simultaneously records 4 statistics: count, max, totalOfSquares, and totalTime. The count statistic will always match the single normalized value provided by a counter if you had called increment() once on the counter for each time you recorded a timing, so it is rarely necessary to count and time separately for a single operation.

For long running operations, Spectator provides a special LongTaskTimer.

Spectator Gauge
Gauges are used to determine some current value like the size of a queue or number of threads in a running state. Since gauges are sampled, they provide no information about how these values fluctuate between samples.

The normal use of a gauge involves registering the gauge once in initialization with an id, a reference to the object to be sampled, and a function to get or compute a numeric value based on the object. The reference to the object is passed in separately and the Spectator registry will keep a weak reference to the object. If the object is garbage collected, then Spectator will automatically drop the registration. See the note in Spectator’s documentation about potential memory leaks if this API is misused.

// the registry will automatically sample this gauge periodically
registry.gauge("gaugeName", pool, Pool::numberOfRunningThreads);

// manually sample a value in code at periodic intervals -- last resort!
registry.gauge("gaugeName", Arrays.asList("tagKey1", "tagValue1", ...), 1000);
Spectator Distribution Summaries
A distribution summary is used to track the distribution of events. It is similar to a timer, but more general in that the size does not have to be a period of time. For example, a distribution summary could be used to measure the payload sizes of requests hitting a server.

// the registry will automatically sample this gauge periodically
DistributionSummary ds = registry.distributionSummary("dsName", "tagKey1", "tagValue1", ...);
ds.record(request.sizeInBytes());
Metrics Collection: Servo
WARNING
If your code is compiled on Java 8, please use Spectator instead of Servo as Spectator is destined to replace Servo entirely in the long term.
In Servo parlance, a monitor is a named, typed, and tagged configuration and a metric represents the value of a given monitor at a point in time. Servo monitors are logically equivalent to Spectator meters. Servo monitors are created and controlled by a MonitorRegistry. In spite of the above warning, Servo does have a wider array of monitor options than Spectator has meters.

Spring Cloud integration configures an injectable com.netflix.servo.MonitorRegistry instance for you. Once you have created the appropriate Monitor type in Servo, the process of recording data is wholly similar to Spectator.

Creating Servo Monitors
If you are using the Servo MonitorRegistry instance provided by Spring Cloud (specifically, an instance of DefaultMonitorRegistry), Servo provides convenience classes for retrieving counters and timers. These convenience classes ensure that only one Monitor is registered for each unique combination of name and tags.

To manually create a Monitor type in Servo, especially for the more exotic monitor types for which convenience methods are not provided, instantiate the appropriate type by providing a MonitorConfig instance:

MonitorConfig config = MonitorConfig.builder("timerName").withTag("tagKey1", "tagValue1").build();

// somewhere we should cache this Monitor by MonitorConfig
Timer timer = new BasicTimer(config);
monitorRegistry.register(timer);
Metrics Backend: Atlas
Atlas was developed by Netflix to manage dimensional time series data for near real-time operational insight. Atlas features in-memory data storage, allowing it to gather and report very large numbers of metrics, very quickly.

Atlas captures operational intelligence. Whereas business intelligence is data gathered for analyzing trends over time, operational intelligence provides a picture of what is currently happening within a system.

Spring Cloud provides a spring-cloud-starter-atlas that has all the dependencies you need. Then just annotate your Spring Boot application with @EnableAtlas and provide a location for your running Atlas server with the netflix.atlas.uri property.

Global tags
Spring Cloud enables you to add tags to every metric sent to the Atlas backend. Global tags can be used to separate metrics by application name, environment, region, etc.

Each bean implementing AtlasTagProvider will contribute to the global tag list:

@Bean
AtlasTagProvider atlasCommonTags(
    @Value("${spring.application.name}") String appName) {
  return () -> Collections.singletonMap("app", appName);
}
Using Atlas
To bootstrap a in-memory standalone Atlas instance:

$ curl -LO https://github.com/Netflix/atlas/releases/download/v1.4.2/atlas-1.4.2-standalone.jar
$ java -jar atlas-1.4.2-standalone.jar
TIP
An Atlas standalone node running on an r3.2xlarge (61GB RAM) can handle roughly 2 million metrics per minute for a given 6 hour window.
Once running and you have collected a handful of metrics, verify that your setup is correct by listing tags on the Atlas server:

$ curl http://ATLAS/api/v1/tags
TIP
After executing several requests against your service, you can gather some very basic information on the request latency of every request by pasting the following url in your browser: http://ATLAS/api/v1/graph?q=name,rest,:eq,:avg
The Atlas wiki contains a compilation of sample queries for various scenarios.

Make sure to check out the alerting philosophy and docs on using double exponential smoothing to generate dynamic alert thresholds.

Spring Cloud Bus

Spring Cloud Bus links nodes of a distributed system with a lightweight message broker. This can then be used to broadcast state changes (e.g. configuration changes) or other management instructions. A key idea is that the Bus is like a distributed Actuator for a Spring Boot application that is scaled out, but it can also be used as a communication channel between apps. The only implementation currently is with an AMQP broker as the transport, but the same basic feature set (and some more depending on the transport) is on the roadmap for other transports.

https://raw.githubusercontent.com/spring-cloud/spring-cloud-build/master/docs/src/main/asciidoc/contributing-docs.adoc

Quick Start
Spring Cloud Bus works by adding Spring Boot autconfiguration if it detects itself on the classpath. All you need to do to enable the bus is to add spring-cloud-starter-bus-amqp to your dependency management and Spring Cloud takes care of the rest. Make sure RabbitMQ is available and configured to provide a ConnectionFactory: running on localhost you shouldn’t have to do anything, but if you are running remotely use Spring Cloud Connectors, or Spring Boot conventions to define the broker credentials, e.g.

application.yml
spring:
  rabbitmq:
    host: mybroker.com
    port: 5672
    username: user
    password: secret
The bus currently supports sending messages to all nodes listening or all nodes for a particular service (as defined by Eureka). More selector criteria will be added in the future (ie. only service X nodes in data center Y, etc…​). The http endpoints are under the /bus/* actuator namespace. There are currently two implemented. The first, /bus/env, sends key/values pairs to update each nodes Spring Environment. The second, /bus/refresh, will reload each application’s configuration, just as if they had all been pinged on their /refresh endpoint.

Addressing an Instance
The HTTP endpoints accept a "destination" parameter, e.g. "/bus/refresh?destination=customers:9000", where the destination is an ApplicationContext ID. If the ID is owned by an instance on the Bus then it will process the message and all other instances will ignore it. Spring Boot sets the ID for you in the ContextIdApplicationContextInitializer to a combination of the spring.application.name, active profiles and server.port by default.

Addressing all instances of a service
The "destination" parameter is used in a Spring PathMatcher (with the path separator as a colon :) to determine if an instance will process the message. Using the example from above, "/bus/refresh?destination=customers:**" will target all instances of the "customers" service regardless of the profiles and ports set as the ApplicationContext ID.

Application Context ID must be unique
The bus tries to eliminate processing an event twice, once from the original ApplicationEvent and once from the queue. To do this, it checks the sending application context id againts the current application context id. If multiple instances of a service have the same application context id, events will not be processed. Running on a local machine, each service will be on a different port and that will be part of the application context id. Cloud Foundry supplies an index to differentiate. To ensure that the application context id is the unique, set spring.application.index to something unique for each instance of a service. For example, in lattice, set spring.application.index=${INSTANCE_INDEX} in application.properties (or bootstrap.properties if using configserver).

Customizing the Message Broker
Spring Cloud Bus uses Spring Cloud Stream to broadcast the messages so to get messages to flow you only need to include the binder implementation of your choice in the classpath. There are convenient starters specifically for the bus with AMQP, Kafka and Redis (spring-cloud-starter-bus-[amqp,kafka,redis]). Generally speaking Spring Cloud Stream relies on Spring Boot autoconfiguration conventions for configuring middleware, so for instance the AMQP broker address can be changed with spring.rabbitmq.* configuration properties. Spring Cloud Bus has a handful of native configuration properties in spring.cloud.bus.* (e.g. spring.cloud.bus.destination is the name of the topic to use the the externall middleware). Normally the defaults will suffice.

To lean more about how to customize the message broker settings consult the Spring Cloud Stream documentation.

Tracing Bus Events
Bus events (subclasses of RemoteApplicationEvent) can be traced by setting spring.cloud.bus.trace.enabled=true. If you do this then the Spring Boot TraceRepository (if it is present) will show each event sent and all the acks from each service instance. Example (from the /trace endpoint):

{
  "timestamp": "2015-11-26T10:24:44.411+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "stores:8081",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.864+0000",
  "info": {
    "signal": "spring.cloud.bus.sent",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.862+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
}
This trace shows that a RefreshRemoteApplicationEvent was sent from customers:9000, broadcast to all services, and it was received (acked) by customers:9000 and stores:8081.

To handle the ack signals yourself you could add an @EventListener for the AckRemoteAppplicationEvent and SentApplicationEvent types to your app (and enable tracing). Or you could tap into the TraceRepository and mine the data from there.

NOTE
Any Bus application can trace acks, but sometimes it will be useful to do this in a central service that can do more complex queries on the data. Or forward it to a specialized tracing service.
Spring Boot Cloud CLI

Spring Boot CLI provides Spring Boot command line features for Spring Cloud. You can write Groovy scripts to run Spring Cloud component applications (e.g. @EnableEurekaServer). You can also easily do things like encryption and decryption to support Spring Cloud Config clients with secret configuration values.

https://raw.githubusercontent.com/spring-cloud/spring-cloud-build/master/docs/src/main/asciidoc/contributing-docs.adoc

Installation
To install, make sure you have Spring Boot CLI (1.2.0 or better):

$ spring version
Spring CLI v1.2.3.RELEASE
E.g. for GVM users

$ gvm install springboot 1.3.0.M5
$ gvm use springboot 1.3.0.M5
and install the Spring Cloud plugin:

$ mvn install
$ spring install org.springframework.cloud:spring-cloud-cli:1.1.0.BUILD-SNAPSHOT
IMPORTANT
Prerequisites: to use the encryption and decryption features you need the full-strength JCE installed in your JVM (it’s not there by default). You can download the "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files" from Oracle, and follow instructions for installation (essentially replace the 2 policy files in the JRE lib/security directory with the ones that you downloaded).
Writing Groovy Scripts and Running Applications
Spring Cloud CLI has support for most of the Spring Cloud declarative features, such as the @Enable* class of annotations. For example, here is a fully functional Eureka server

app.groovy
@EnableEurekaServer
class Eureka {}
which you can run from the command line like this

$ spring run app.groovy
To include additional dependencies, often it suffices just to add the appropriate feature-enabling annotation, e.g. @EnableConfigServer, @EnableOAuth2Sso or @EnableEurekaClient. To manually include a dependency you can use a @Grab with the special "Spring Boot" short style artifact co-ordinates, i.e. with just the artifact ID (no need for group or version information), e.g. to set up a client app to listen on AMQP for management events from the Spring CLoud Bus:

app.groovy
@Grab('spring-cloud-starter-bus-amqp')
@RestController
class Service {
  @RequestMapping('/')
  def home() { [message: 'Hello'] }
}
Encryption and Decryption
The Spring Cloud CLI comes with an "encrypt" and a "decrypt" command. Both accept arguments in the same form with a key specified as a mandatory "--key", e.g.

$ spring encrypt mysecret --key foo
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
$ spring decrypt --key foo 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
To use a key in a file (e.g. an RSA public key for encyption) prepend the key value with "@" and provide the file path, e.g.

$ spring encrypt mysecret --key @${HOME}/.ssh/id_rsa.pub
AQAjPgt3eFZQXwt8tsHAVv/QHiY5sI2dRcR+...
Spring Cloud Security

Spring Cloud Security offers a set of primitives for building secure applications and services with minimum fuss. A declarative model which can be heavily configured externally (or centrally) lends itself to the implementation of large systems of co-operating, remote components, usually with a central indentity management service. It is also extremely easy to use in a service platform like Cloud Foundry. Building on Spring Boot and Spring Security OAuth2 we can quickly create systems that implement common patterns like single sign on, token relay and token exchange.

https://raw.githubusercontent.com/spring-cloud/spring-cloud-build/master/docs/src/main/asciidoc/contributing-docs.adoc

Quickstart
OAuth2 Single Sign On
Here’s a Spring Cloud "Hello World" app with HTTP Basic authentication and a single user account:

app.groovy
@Grab('spring-boot-starter-security')
@Controller
class Application {

  @RequestMapping('/')
  String home() {
    'Hello World'
  }

}
You can run it with spring run app.groovy and watch the logs for the password (username is "user"). So far this is just the default for a Spring Boot app.

Here’s a Spring Cloud app with OAuth2 SSO:

app.groovy
@Controller
@EnableOAuth2Sso
class Application {

  @RequestMapping('/')
  String home() {
    'Hello World'
  }

}
Spot the difference? This app will actually behave exactly the same as the previous one, because it doesn’t know it’s OAuth2 credentals yet.

You can register an app in github quite easily, so try that if you want a production app on your own domain. If you are happy to test on localhost:8080, then set up these properties in your application configuration:

application.yml
spring:
  oauth2:
    client:
      clientId: bd1c0a783ccdd1c9b9e4
      clientSecret: 1a9030fbca47a5b2c28e92f19050bb77824b5ad1
      accessTokenUri: https://github.com/login/oauth/access_token
      userAuthorizationUri: https://github.com/login/oauth/authorize
      clientAuthenticationScheme: form
    resource:
      userInfoUri: https://api.github.com/user
      preferTokenInfo: false
run the app above and it will redirect to github for authorization. If you are already signed into github you won’t even notice that it has authenticated. These credentials will only work if your app is running on port 8080.

To limit the scope that the client asks for when it obtains an access token you can set spring.oauth2.client.scope (comma separated or an array in YAML). By default the scope is empty and it is up to to Authorization Server to decide what the defaults should be, usually depending on the settings in the client registration that it holds.

NOTE
The examples above are all Groovy scripts. If you want to write the same code in Java (or Groovy) you need to add Spring Security OAuth2 to the classpath (e.g. see the sample here).
OAuth2 Protected Resource
You want to protect an API resource with an OAuth2 token? Here’s a simple example (paired with the client above):

app.groovy
@Grab('spring-cloud-starter-security')
@RestController
@EnableResourceServer
class Application {

  @RequestMapping('/')
  def home() {
    [message: 'Hello World']
  }

}
and

application.yml
spring:
  oauth2:
    resource:
      userInfoUri: https://api.github.com/user
      preferTokenInfo: false
More Detail
Single Sign On
NOTE
All of the OAuth2 SSO and resource server features moved to Spring Boot in version 1.3. You can find documentation in the Spring Boot user guide.
Token Relay
A Token Relay is where an OAuth2 consumer acts as a Client and forwards the incoming token to outgoing resource requests. The consumer can be a pure Client (like an SSO application) or a Resource Server.

Client Token Relay
If your app has a Spring Cloud Zuul embedded reverse proxy (using @EnableZuulProxy) then you can ask it to forward OAuth2 access tokens downstream to the services it is proxying. Thus the SSO app above can be enhanced simply like this:

app.groovy
@Controller
@EnableOAuth2Sso
@EnableZuulProxy
class Application {

}
and it will (in addition to loggin the user in and grabbing a token) pass the authentication token downstream to the /proxy/* services. If those services are implemented with @EnableOAuth2Resource then they will get a valid token in the correct header.

How does it work? The @EnableOAuth2Sso annotation pulls in spring-cloud-starter-security (which you could do manually in a traditional app), and that in turn triggers some autoconfiguration for a ZuulFilter, which itself is activated because Zuul is on the classpath (via @EnableZuulProxy). The {github}/tree/master/src/main/java/org/springframework/cloud/security/oauth2/proxy/OAuth2TokenRelayFilter.java[filter] just extracts an access token from the currently authenticated user, and puts it in a request header for the downstream requests.

Resource Server Token Relay
If your app has @EnableOAuth2Resource and also is a Client (i.e. it has a spring.oauth2.client.clientId, even if it doesn’t use it), then the OAuth2RestOperations that is provided for @Autowired users by Spring Cloud (it is declared as @Primary) will also forward tokens. If you don’t want to forward tokens (and that is a valid choice, since you might want to act as yourself, rather than the client that sent you the token), then you only need to create your own OAuth2RestOperations instead of autowiring the default one. Here’s a basic example showing the use of the autowired rest template ("foo.com" is a Resource Server accepting the same tokens as the surrounding app):

MyController.java
@Autowired
private OAuth2RestOperations restTemplate;

@RequestMapping("/relay")
public String relay() {
    ResponseEntity<String> response =
      restTemplate.getForEntity("https://foo.com/bar", String.class);
    return "Success! (" + response.getBody() + ")";
}
Configuring Authentication Downstream of a Zuul Proxy
You can control the authorization behaviour downstream of an @EnableZuulProxy through the proxy.auth.* settings. Example:

application.yml
proxy:
  auth:
    routes:
      customers: oauth2
      stores: passthru
      recommendations: none
In this example the "customers" service gets an OAuth2 token relay, the "stores" service gets a passthrough (the authorization header is just passed downstream), and the "recommendations" service has its authorization header removed. The default behaviour is to do a token relay if there is a token available, and passthru otherwise.

See {github}/tree/master/src/main/java/org/springframework/cloud/security/oauth2/proxy/ProxyAuthenticationProperties[ ProxyAuthenticationProperties] for full details.

Last updated 2016-01-26 10:56:04 UTC