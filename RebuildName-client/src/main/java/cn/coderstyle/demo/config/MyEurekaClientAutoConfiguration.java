package cn.coderstyle.demo.config;

/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.noop.NoopDiscoveryClientAutoConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaClientConfig;

import static org.springframework.cloud.commons.util.IdUtils.getDefaultInstanceId;

/**
 * @author Dave Syer
 * @author Spencer Gibb
 * @author Jon Schneider
 * @author Matt Jenkins
 * @author Ryan Baxter
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(EurekaClientConfig.class)
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
@AutoConfigureBefore({ NoopDiscoveryClientAutoConfiguration.class,
		CommonsClientAutoConfiguration.class })
@AutoConfigureAfter(name = "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration")

public class MyEurekaClientAutoConfiguration extends EurekaClientAutoConfiguration {
	@Value("${server.port:${SERVER_PORT:${PORT:8080}}}")
	int nonSecurePort;

	@Value("${management.port:${MANAGEMENT_PORT:${server.port:${SERVER_PORT:${PORT:8080}}}}}")
	int managementPort;

	@Value("${eureka.instance.hostname:${EUREKA_INSTANCE_HOSTNAME:}}")
	String hostname;

	@Autowired
	ConfigurableEnvironment env;

	@Bean
	@ConditionalOnMissingBean(value = ServerNameConfig.class, search = SearchStrategy.CURRENT)
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
		EurekaInstanceConfigBean instance = new ServerNameConfig(inetUtils);
		instance.setNonSecurePort(this.nonSecurePort);
		instance.setInstanceId(getDefaultInstanceId(this.env));

		if (this.managementPort != this.nonSecurePort && this.managementPort != 0) {
			if (StringUtils.hasText(this.hostname)) {
				instance.setHostname(this.hostname);
			}
			RelaxedPropertyResolver relaxedPropertyResolver = new RelaxedPropertyResolver(env, "eureka.instance.");
			String statusPageUrlPath = relaxedPropertyResolver.getProperty("statusPageUrlPath");
			String healthCheckUrlPath = relaxedPropertyResolver.getProperty("healthCheckUrlPath");
			if (StringUtils.hasText(statusPageUrlPath)) {
				instance.setStatusPageUrlPath(statusPageUrlPath);
			}
			if (StringUtils.hasText(healthCheckUrlPath)) {
				instance.setHealthCheckUrlPath(healthCheckUrlPath);
			}
			String scheme = instance.getSecurePortEnabled() ? "https" : "http";
			instance.setStatusPageUrl(scheme + "://" + instance.getHostname() + ":"
					+ this.managementPort + instance.getStatusPageUrlPath());
			instance.setHealthCheckUrl(scheme + "://" + instance.getHostname() + ":"
					+ this.managementPort + instance.getHealthCheckUrlPath());
		}
		return instance;
	}

}
