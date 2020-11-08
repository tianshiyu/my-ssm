package org.dishi.quartz;

import org.dishi.entity.Memo;
import org.dishi.message.MailMessage;
import org.dishi.service.EmailService;
import org.dishi.service.MemoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Map;

@Component
public class MemoJob implements Job {
    @Autowired
    private EmailService emailService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {

            // 让Spring可以把成员变量注入进来。使用了这种方式后，就不能使用setter方法获取传递进来的参数了。
            // 因此下面使用getJobDataMap方式获取
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            Map<String, Object> wrappedMap = jobExecutionContext.getJobDetail().getJobDataMap().getWrappedMap();
            MailMessage memo = (MailMessage) wrappedMap.get("memo");

            System.out.println("发送邮件");
            //发送邮件通知用户
            System.out.println(emailService+"emailServ");
            emailService.send(memo);

            System.out.println("发送完成");
            //修改状态
//            memo.setState(Memo.ALREADY_SEND);
//            memoService.updateByPrimaryKeySelective(memo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
