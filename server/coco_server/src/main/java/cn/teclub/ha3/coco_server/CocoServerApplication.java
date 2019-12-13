package cn.teclub.ha3.coco_server;

import cn.teclub.ha3.coco_server.sys.StServerErrorAttributes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;


@EnableAutoConfiguration
@EnableCaching
@MapperScan("cn.teclub.ha3.coco_server.model.dao")
@SpringBootApplication
public class CocoServerApplication {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new StServerErrorAttributes();
    }

    public static void main(String[] args) {
        SpringApplication.run(CocoServerApplication.class, args);
    }

}
