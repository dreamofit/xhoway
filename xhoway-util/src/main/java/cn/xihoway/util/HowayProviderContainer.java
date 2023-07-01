package cn.xihoway.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HowayProviderContainer {
    public static ClassPathXmlApplicationContext context;
    public HowayProviderContainer(){}

    public void start(String... configLocations){
        context = new ClassPathXmlApplicationContext(configLocations);
        context.refresh();
        context.start();
    }
}
