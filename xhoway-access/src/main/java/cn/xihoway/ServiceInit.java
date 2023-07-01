package cn.xihoway;

import cn.xihoway.util.HowayConsumerContainer;
import cn.xihoway.util.HowayProviderContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ServiceInit {
    public static void main( String[] args ) throws IOException {
        HowayConsumerContainer consumerContainer = new HowayConsumerContainer();
        consumerContainer.start("dubbo/consumer.xml");
        //MyScheduler.execute();
        HowayProviderContainer providerContainer = new HowayProviderContainer();
        providerContainer.start("dubbo/provider.xml");

        System.out.println("*** xhoway服务已经启动 ***");
        System.in.read();

        SpringApplication.run(ServiceInit.class, args);
    }
}
