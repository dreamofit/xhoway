package cn.xihoway.scheduler;

import org.quartz.SchedulerException;

/**
 * 定时任务添加列表
 */
public class MyScheduler {
    public static void execute(){
        try {
            SchedulerConfig scheduler = new SchedulerConfig();
            scheduler.init();
            //每五分钟持久化一次数据库
            //scheduler.addJob(AutoWriteToDb.class,"0 0/5 * * * ?");
            //每5秒执行一次 MyJobExample
            scheduler.addJob(MyJobExample.class,"0/5 * * * * ? ");
            scheduler.run();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
