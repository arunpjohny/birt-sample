package com.arunpjohny.webapp.birt.config;

import java.io.IOException;
import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy
@EnableLoadTimeWeaving(aspectjWeaving = AspectJWeaving.ENABLED)
@EnableTransactionManagement
@Configuration
public class SpringConfiguration {

	private DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

	@Bean
	public PropertyPlaceholderConfigurer propertyConfigurer()
			throws IOException, ClassNotFoundException, SQLException {

		Resource resource = defaultResourceLoader
				.getResource("classpath:application.properties");

		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setLocation(resource);
		configurer.setIgnoreUnresolvablePlaceholders(false);

		return configurer;
	}

	@Bean
	public DataSource dataSource(
			@Value("${jdbc.driver}") String driverClassName,
			@Value("${jdbc.url}") String url,
			@Value("${jdbc.username}") String username,
			@Value("${jdbc.password}") String password)
			throws BeanInstantiationException, ClassNotFoundException {
		Driver driver = (Driver) BeanUtils.instantiateClass(Class
				.forName(driverClassName));
		SimpleDriverDataSource ds = new SimpleDriverDataSource(driver, url,
				username, password);
		return ds;
	}
}
