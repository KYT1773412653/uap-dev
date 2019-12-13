package com.hexing.uap.client.cfg;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 注册Web相关的拦截器，过滤器等资源
 * 
 * @author xia
 * @version 0.2
 * @since 0.2
 */
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class WebConfiguration implements WebMvcConfigurer {
	private static Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

	@Autowired
	ClientTokenInterceptor tokenInterceptor;
	@Autowired
	private ClientProperties clientProperties;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] interPatterns = { "/v2/api-docs/**", "/swagger/**", "/swagger-resources/**", "/ui/**", "/error" };
		List<String> patternsAll = new ArrayList<>();
		for (String interPattern : interPatterns) {
			patternsAll.add(interPattern);
		}
		String[] extraPatterns = null;
		String excludePathPatterns = clientProperties.getExcludePathPatterns();
		if (!StringUtils.isEmpty(excludePathPatterns)) {
			extraPatterns = excludePathPatterns.split(",");
		}
		if (extraPatterns != null && extraPatterns.length > 0) {
			for (String extraPattern : extraPatterns) {
				patternsAll.add(extraPattern);
			}
		}
		LOG.info("ExcludePathPatterns  are {} ", patternsAll.toString());
		registry.addInterceptor(tokenInterceptor).excludePathPatterns(patternsAll).addPathPatterns("/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 设置允许跨域的路径
		registry.addMapping("/**")
				// 设置允许跨域请求的域名
				.allowedOrigins("*")
				// 是否允许证书 不再默认开启
				.allowCredentials(true)
				// 设置允许的方法
				.allowedMethods("*")
				// 跨域允许时间
				.maxAge(3600);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
		return new Jackson2ObjectMapperBuilderCustomizer() {
			@Override
			public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
				jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			}
		};
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
		return converter;
	}
}
