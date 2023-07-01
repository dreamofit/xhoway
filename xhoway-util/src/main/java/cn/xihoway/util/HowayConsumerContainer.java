package cn.xihoway.util;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HowayConsumerContainer {
    public static ClassPathXmlApplicationContext context;
    public HowayConsumerContainer(){}
    public static ClassPathXmlApplicationContext getContext(){
        return context;
    }
    public void start(String... configLocations){
        context = new ClassPathXmlApplicationContext(configLocations);
        context.refresh();
        context.start();
    }
}
