package cn.teclub.ha3.server.sys;


import cn.teclub.ha3.server.interceptor.StStatsInterceptor;
import cn.teclub.ha3.server.interceptor.StUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Server MVC configurer.
 */
@Configuration
public class FsServerMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private StStatsInterceptor stStatsInterceptor;
    @Autowired
    private StUserInterceptor stUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(stStatsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(stUserInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/**/avatar")
                .excludePathPatterns("/api/**/login/**")
                .excludePathPatterns("/api/**/signin")
                .excludePathPatterns("/api/**/authcode/**")
                .excludePathPatterns("/**/resetPasswd");
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mmConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_XML);
        mediaTypes.add(MediaType.TEXT_HTML);
        mmConverter.setSupportedMediaTypes(mediaTypes);

        converters.add(mmConverter);
        converters.add(new StringHttpMessageConverter());
    }
}
