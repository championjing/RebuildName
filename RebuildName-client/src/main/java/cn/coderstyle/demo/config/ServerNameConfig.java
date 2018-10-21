package cn.coderstyle.demo.config;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
/**
* <p>Title: </p>  
* <p>Description: </p>  
* @author champion  
* @date 2018/07/25
*/
public class ServerNameConfig extends EurekaInstanceConfigBean{

	private static Logger logger = LoggerFactory.getLogger(ServerNameConfig.class);
	
	public ServerNameConfig(InetUtils inetUtils) {
		super(inetUtils);
	}

	@Override
	public void setEnvironment(Environment environment){
		logger.debug("自定义实例名称");
		// set some defaults from the environment, but allow the defaults to use relaxed binding
		RelaxedPropertyResolver springPropertyResolver = new RelaxedPropertyResolver(environment, "eureka.app.");
		String prefixName = springPropertyResolver.getProperty("prefix");
		String clusterName = springPropertyResolver.getProperty("name");
		if(StringUtils.hasText(prefixName) && StringUtils.hasText(clusterName) ) {
			String springAppName = prefixName + clusterName;
			logger.info("eureka.instance.name = "+ springAppName);
			setAppname(springAppName);
			setVirtualHostName(springAppName);
			setSecureVirtualHostName(springAppName);
		} else {
			logger.error("!!!!!!!!!!!!eureka.name.prefix 和 eureka.name.cluster 为空，请确认 !!!!!!!!!");
		}
		
	}

}
