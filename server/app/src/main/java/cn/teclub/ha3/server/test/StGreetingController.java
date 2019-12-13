package cn.teclub.ha3.server.test;

import java.util.concurrent.atomic.AtomicLong;

import cn.teclub.ha3.net.StClientInfo;
import cn.teclub.ha3.request.StLoginReq;
import cn.teclub.ha3.request.StLoginRes;
import cn.teclub.ha3.server.ctrl.StController;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;


/**
 * demo controller
 * ONLY for testing
 */
@SuppressWarnings("ALL")
@RestController
public class StGreetingController extends StController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     *
     * <pre>
     *
     * [Theodor: 2019/1/25] access url: http://localhost:8080/greeting
     *
     * WebSocketMessageBrokerConfigurer[E1] has NO effect on request message!!!
     *
     * [E1] thie is the websocket config:
     *      @Configuration
     *      @EnableWebSocketMessageBroker
     *      public class StWebSocketConfig implements WebSocketMessageBrokerConfigurer {
     *          @Override
     *          public void configureMessageBroker(MessageBrokerRegistry registry) {
     *              System.out.println("==== cook: enable simple broker");
     *              registry.enableSimpleBroker("/topic", "/b", "/g", "/u");
     *              registry.setApplicationDestinationPrefixes("/app");
     *          }
     *
     *          @Override
     *          public void registerStompEndpoints(StompEndpointRegistry registry) {
     *              System.out.println("==== cook: register end-point");
     *              registry.addEndpoint("/ep-st-websocket").withSockJS();
     *      }
     *
     * </pre>
     *
     */
    @RequestMapping("/greeting")
    public StGreeting greeting(@RequestParam(value = "name", defaultValue = "user02") String name) {
        StClientInfo ci = global.dao.queryClientByName(name);
        log.info("get client-info: " + ci.dumpSimple());
        return new StGreeting(counter.incrementAndGet(), String.format(template, name));
    }


    //@RequestMapping("/v1/login/pass")
    @PostMapping("/v0/login/pass")
    public StLoginRes pass(@RequestBody StLoginReq req) {
        log.info("recv login request: " + req.toString());
        StLoginRes res = new StLoginRes();
        res.setToken("FAKE TOKEN: 0x01234567890ABCDEF");
        return res;
    }


    // copied from ws-srv
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public StGreeting greeting(StHelloMessage message) throws Exception {
        System.out.println("==== COOK: get a hello message: " + message );
        Thread.sleep(1000); // simulated delay
        System.out.println("==== return a StGreeting obj...");
        return new StGreeting( counter.incrementAndGet(), "[StGreeting Message from server]" + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
