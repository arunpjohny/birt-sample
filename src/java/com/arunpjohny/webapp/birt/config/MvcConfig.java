package com.arunpjohny.webapp.birt.config;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
public class MvcConfig  {

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer obj = new FreeMarkerConfigurer();
		obj.setTemplateLoaderPath("/");
		obj.setDefaultEncoding("UTF-8");

		Properties settings = new Properties();
		settings.put("date_format", "dd MMM yyyy");
		settings.put("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.put("number_format", "########");
		obj.setFreemarkerSettings(settings);

		return obj;
	}

	@Bean
	public FreeMarkerViewResolver FreeMarkerViewResolver(
			FreeMarkerConfigurer configurer) {
		FreeMarkerViewResolver obj = new FreeMarkerViewResolver();
		obj.setExposePathVariables(Boolean.TRUE);
		obj.setRequestContextAttribute("rc");
		obj.setPrefix("");
		obj.setSuffix(".ftl");
		return obj;
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(Locale.US);
	}
}
