package com.museum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	String uploadPath = "file:///C:/museum/";
	
	/*
	 * @Value("${uploadPath}") String uploadPath; 배포용
	 */
	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/img/**")
				.addResourceLocations(uploadPath);
	}
}
