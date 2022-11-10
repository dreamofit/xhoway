package cn.xihoway;

import cn.xihoway.container.HowayContainer;
import cn.xihoway.scheduler.MyScheduler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 初始化dubbo服务
 */
public class DubboServerInit {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo/provider.xml");
        //System.out.println(context.getDisplayName() + ": here");
        context.start();
        HowayContainer container = new HowayContainer();
        container.start();
        MyScheduler.execute();
        System.out.println("*** xhoway服务已经启动 ***");
        System.in.read();
    }
}
