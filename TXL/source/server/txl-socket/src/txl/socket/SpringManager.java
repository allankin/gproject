package txl.socket;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringManager {
	
	private final static Logger log = Logger.getLogger(SpringManager.class);
	private static ApplicationContext context;
	
	
	static{
		new SpringManager();
	}

	
	private SpringManager()
	{
		 context=new ClassPathXmlApplicationContext(new String[]{"config/spring/spring-model.xml"});

	}
	
	public static void init()
	{
		log.info("----------------------SpringManager 开始初始化 spring 环境-------------");

	}
	  
	public static Object getBean(String beanName)
	{
		return context.getBean(beanName);
		/*WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext((request).getSession().getServletContext());
		beanName = "userService";
		return ctx.getBean(beanName);*/
	}
}
