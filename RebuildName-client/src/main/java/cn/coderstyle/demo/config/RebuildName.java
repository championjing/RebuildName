package cn.coderstyle.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
/**
* <p>Title: AccountDao</p>  
* <p>Description: </p>  
* @author champion  
* @date 2018/07/25
*/
@Configuration
public class RebuildName extends EurekaInstanceConfigBean{

	public RebuildName(InetUtils inetUtils) {
		super(inetUtils);
	}

	@Override
	public void setEnvironment(Environment environment){
		System.out.println("自定义的组合的 app.name");
		//super.setEnvironment(environment);
		// set some defaults from the environment, but allow the defaults to use relaxed binding
		RelaxedPropertyResolver springPropertyResolver = new RelaxedPropertyResolver(environment, "eureka.name.");
		String prefixName = springPropertyResolver.getProperty("prefix");
		String clusterName = springPropertyResolver.getProperty("cluster");
		if(StringUtils.hasText(prefixName) && StringUtils.hasText(clusterName) ) {
			String springAppName = prefixName + clusterName;
			System.out.println("eureka.instance.name = "+ springAppName);
			setAppname(springAppName);
			setVirtualHostName(springAppName);
			setSecureVirtualHostName(springAppName);
		} else {
			System.out.println("!!!!!!!!!!!!eureka.name.prefix 和 eureka.name.cluster 为空，请确认 !!!!!!!!!");
		}
		
	}

}
