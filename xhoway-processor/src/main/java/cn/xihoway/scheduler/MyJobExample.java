package cn.xihoway.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务示例，需要实现Job接口
 */
public class MyJobExample  implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:ss");
        Date date = new Date(System.currentTimeMillis());
        System.out.println("["+ sdf.format(date) +"] this is a example job!");
    }
}
