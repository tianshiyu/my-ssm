package org.dishi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.jms.ConnectionFactory;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


//@EnableJms
@EnableTransactionManagement
@EnableWebMvc
@Configuration
@PropertySource(value = {"classpath:/jdbc.properties", "classpath:/smtp.properties"})
@ComponentScan
@MapperScan("org.dishi.dao")
public class Application {

    // json序列化
    @Bean
    ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }

    //Mybatis
    @Bean
    public DataSource createDataSource(@Value("${jdbc.url}") String jdbcUrl, @Value("${jdbc.username}") String jdbcUsername,
                                       @Value("${jdbc.password}") String jdbcPassword){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("autoCommit", "false");
        config.addDataSourceProperty("connectionTimeout", "5");
        config.addDataSourceProperty("idleTimeout", "60");
        return new HikariDataSource(config);
    }
    @Bean
    PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean
    SqlSessionFactoryBean createSqlSessionFactoryBean(@Autowired DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        return sqlSessionFactoryBean;
    }


    //WebMVC
    @Bean
    WebMvcConfigurer createWebMvc(@Autowired HandlerInterceptor[] interceptors){
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**").addResourceLocations("/static/");
            }
            //过滤器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                for (HandlerInterceptor interceptor : interceptors) {
                    //设置拦截路径
                    registry.addInterceptor(interceptor);
                }
            }
        };
    }
    @Bean
    @Order(2)
    ViewResolver createViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix("");
        return viewResolver;
    }
    @Bean
    @Order(1)
    ViewResolver createPebbleViewResolver(@Autowired ServletContext servletContext){
        PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(true)
                // cache:
                .cacheActive(false)
                // loader:
                .loader(new ServletLoader(servletContext))
                // extension:
                .extension(new SpringExtension())
                // build:
                .build();
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix("");
        viewResolver.setPebbleEngine(engine);
        return viewResolver;
    }


    //email
    @Bean
    JavaMailSender createJavaMailSender(
            // smtp.properties:
            @Value("${smtp.host}") String host,
            @Value("${smtp.port}") int port,
            @Value("${smtp.auth:true}") String auth,
            @Value("${smtp.username}") String username,
            @Value("${smtp.password}") String password,
            @Value("${smtp.debug:true}") String debug)
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        //进行授权认证，它的目的就是阻止他人任意乱发邮件
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.timeout", 25000);
        //SMTP加密方式:连接到一个TLS保护连接
        props.put("mail.smtp.starttls.enable", "true");
//        if (port == 587) {
//        	//SMTP加密方式:连接到一个TLS保护连接
//            props.put("mail.smtp.starttls.enable", "true");
//        }
//        if (port == 465) {
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        }
        props.put("mail.debug", debug);
        return mailSender;
    }

    //quartz
    //解决属性不能注入的问题
    /**
     * 继承org.springframework.scheduling.quartz.SpringBeanJobFactory
     * 实现任务实例化方式
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;
        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }
        /**
         * 将job实例交给spring ioc托管
         * 我们在job实例实现类内可以直接使用spring注入的调用被spring ioc管理的实例
         * @param bundle
         * @return
         * @throws Exception
         */
        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            /**
             * 将job实例交付给spring ioc
             */
            beanFactory.autowireBean(job);
            return job;
        }
    }
    /**
     * 配置任务工厂实例
     * @param applicationContext spring上下文实例
     * @return
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext)
    {
        /**
         * 采用自定义任务工厂 整合spring实例来完成构建任务
         * see {@link AutowiringSpringBeanJobFactory}
         */
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
    /**
     * 配置任务调度器
     * 使用项目数据源作为quartz数据源
     * @param jobFactory 自定义配置任务工厂(其实就是AutowiringSpringBeanJobFactory)
     * @param dataSource 数据源实例
     * @return
     * @throws Exception
     */
    @Bean(destroyMethod = "destroy",autowire = Autowire.NO)
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) throws Exception
    {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //将spring管理job自定义工厂交由调度器维护
        schedulerFactoryBean.setJobFactory(jobFactory);
        //设置覆盖已存在的任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //项目启动完成后，等待2秒后开始执行调度器初始化
        schedulerFactoryBean.setStartupDelay(2);
        //设置调度器自动运行
        schedulerFactoryBean.setAutoStartup(true);
//        //设置数据源，使用与项目统一数据源
//        schedulerFactoryBean.setDataSource(dataSource);
        //设置上下文spring bean name
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
//        //设置配置文件位置
//        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
        return schedulerFactoryBean;
    }

//    //jms
//    @Bean
//    public ConnectionFactory createJmsConnFactory(@Value("${jms.uri}") String uri, @Value("jms.username") String username,
//                                                  @Value("${jms.password}") String password){
//        return new ActiveMQJMSConnectionFactory(uri, username, password);
//    }
//    @Bean
//    public JmsTemplate createJmsTemplate(@Autowired ConnectionFactory conn){
//        return new JmsTemplate(conn);
//    }
//    @Bean
//    public DefaultJmsListenerContainerFactory createJmsListenerContainerFactory(@Autowired ConnectionFactory connectionFactory){
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        return factory;
//    }
}
