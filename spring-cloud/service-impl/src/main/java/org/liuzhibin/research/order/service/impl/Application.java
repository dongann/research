package org.liuzhibin.research.order.service.impl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@RestController
//MyBatis的DAO Mapper接口定义位置
@MapperScan(basePackages = "org.liuzhibin.research.order.service.dao")
//如果不指定，应用能够成功启动，Application.home能够发挥作用，
//但org.liuzhibin.research.order.service.rest.OrderRestServiceImpl无法注册成为controller，不会接受相应url路由处理。
@ComponentScan(basePackages = "org.liuzhibin.research.order")
public class Application {
    @Autowired
    private DiscoveryClient client;
    
    /**
     * 根域名，返回简单的HTML，显示服务的REST接口清单
     * @return
     */
    @RequestMapping(produces = "text/html;charset=UTF-8")
    public String home() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-size:14px;padding:20px;line-height:25px;'>");
        sb.append("<a href=")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/ping?msg=Ping>")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/ping?msg=Ping</a>");
        sb.append("<br /><a href=")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/find?status=New>")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/find?status=New</a>");
        sb.append("<br /><a href=")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/get/1>")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/get/1</a>");
        sb.append("<br /><a href=")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/update/1?status=Close>")
            .append(client.getLocalServiceInstance().getUri())
            .append("/order/update/1?status=Close</a>");
        sb.append("</div>");
        return sb.toString();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }
}