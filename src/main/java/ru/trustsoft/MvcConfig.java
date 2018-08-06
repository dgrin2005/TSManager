package ru.trustsoft;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/greeting").setViewName("greeting");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/baselist.html").setViewName("baselist");
        registry.addViewController("/basesofuserslist").setViewName("basesofuserslist");
        registry.addViewController("/contragentslist").setViewName("contragentslist");
        registry.addViewController("/userslist").setViewName("userslist");
    }

}