package cn.xihoway;

import cn.xihoway.container.HowayContainer;
import cn.xihoway.scheduler.MyScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServiceInit {
    public static void main( String[] args ) {
        HowayContainer container = new HowayContainer();
        container.start();
        //MyScheduler.execute();
        SpringApplication.run(WebServiceInit.class, args);
    }
}
