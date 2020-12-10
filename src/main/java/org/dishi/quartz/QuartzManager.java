package org.dishi.quartz;

import org.dishi.entity.Memo;
import org.dishi.message.MailMessage;
import org.dishi.utils.Date2Cron;
import org.quartz.*;

import java.util.HashMap;
import java.util.Map;

public class QuartzManager {

    // job组和trigger组默认的名字
    private static String JOB_GROUP_NAME = "MY_JOB_GROUP";
    private static String TRIGGER_GROUP_NAME = "MY_TRIGGER_GROUP";


    /**
     * @param scheduler    调度器
     * @param jobClass 任务
     * @Description: 添加一个定时任务
     * @Title: QuartzManager.java
     */
    public static void addJob(Scheduler scheduler, Class<? extends Job> jobClass, MailMessage memoMail, Memo memo) {

        //装载着参数
        Map<String, Object> map = new HashMap<>();
        map.put("memo", memoMail);
        try {
            JobDetail jobDetail = JobBuilder
                    .newJob(jobClass)
                    .withIdentity(String.valueOf(memo.getMid()), JOB_GROUP_NAME)
                    .usingJobData(new JobDataMap(map)).build();// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(String.valueOf(memo.getMid()), TRIGGER_GROUP_NAME)// 触发器名,触发器组
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(Date2Cron.getCron(memo.getSendtime())))
                    .build();
            //绑定任务和触发器，开始执行
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched 调度器
     * @Description: 修改一个任务的触发时间
     * @Title: QuartzManager.java
     */
    public static void modifyJobTime(Scheduler sched, MailMessage mailMessage, Memo memo) {
        try {
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(String.valueOf(memo.getMid()), TRIGGER_GROUP_NAME));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();

            if (!oldTime.equalsIgnoreCase(Date2Cron.getCron(memo.getSendtime()))) {

                JobDetail jobDetail = sched.getJobDetail(new JobKey(String.valueOf(memo.getMid()), JOB_GROUP_NAME));
                Class<? extends Job> objJobClass = jobDetail.getJobClass();
                removeJob(sched, String.valueOf(memo.getMid()), String.valueOf(memo.getMid()));

                addJob(sched, objJobClass, mailMessage, memo);

                sched.start();


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param sched       调度器
     * @param jobName
     * @param triggerName
     * @Description: 移除一个任务
     * @Title: QuartzManager.java
     */
    public static void removeJob(Scheduler sched, String jobName, String triggerName) {
        try {
            sched.pauseTrigger(new TriggerKey(triggerName, TRIGGER_GROUP_NAME));// 停止触发器
            sched.unscheduleJob(new TriggerKey(triggerName, TRIGGER_GROUP_NAME));// 移除触发器
            sched.deleteJob(new JobKey(jobName, JOB_GROUP_NAME));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched 调度器
     * @Description:启动所有定时任务
     * @Title: QuartzManager.java
     */
    public static void startJobs(Scheduler sched) {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sched 调度器
     * @Description:关闭所有定时任务
     * @Title: QuartzManager.java
     */
    public static void shutdownJobs(Scheduler sched) {
        try {
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

