package cn.teclub.ha3.server;

import cn.teclub.ha3.server.sys.StServerErrorAttributes;
import cn.teclub.ha3.utils.StConst;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.InputStreamReader;
import java.util.Scanner;

@EnableAutoConfiguration
@EnableCaching
@MapperScan("cn.teclub.ha3.server.dao")
@SpringBootApplication
public class StApplication {


    @Bean
    public ErrorAttributes errorAttributes() {
        return new StServerErrorAttributes();
    }

    private static void cmdLoop() {
        System.out.println("ST: Reading input from console using Scanner in Java ");
        System.out.println("ST: Please enter your input: ");
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        String line;
        label:
        for (; ; ) {
            System.out.print(">>> ");
            line = scanner.nextLine();
            System.out.println("#dbg: User Input from console: '" + line + "'");

            switch (line.trim()){
                case "":
                    break;

                case "quit":
                    System.out.println("WRN: Are your sure to exit? (Yes/no):  ");
                    line = scanner.nextLine();
                    if (line.equals("Yes")) {
                        System.out.println("INF: Exit Loop!");
                        break label;
                    }
                    break;

                default:
                    System.out.println("WRN: unknown command: '" + line + "'");
                    break;
            }
        }

    }



    public static void main(String[] args) {
        System.out.println("************************************************************************************");
        System.out.println("**** server version: " + StSrvGlobal.VERSION_INFO ) ;
        System.out.println("**** genlib version: " + StConst.getVersionInfo()) ;
        System.out.println("************************************************************************************");
        System.out.println("\n==== dump server config ==== \n" + StSrvGlobal.instance().conf );
        System.out.println("\n\n");
        ConfigurableApplicationContext ctx = SpringApplication.run(StApplication.class, args);
        cmdLoop();

        System.out.println("\n\nINF: close application context!");
        ctx.close();
    }
}
