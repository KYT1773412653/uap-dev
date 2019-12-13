package com.hexing.uap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	
	@Bean
	public CorsConfiguration corsConfiguration(RoutePredicateHandlerMapping routePredicateHandlerMapping) {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(Collections.unmodifiableList(Arrays.asList(CorsConfiguration.ALL)));
		corsConfiguration.setAllowedMethods(Arrays.asList(
				HttpMethod.POST.name(),
				HttpMethod.GET.name(),
				HttpMethod.OPTIONS.name(),
				HttpMethod.DELETE.name(),
				HttpMethod.PUT.name(),
				HttpMethod.PATCH.name()
		));
		corsConfiguration.setAllowedHeaders(Collections.unmodifiableList(Arrays.asList(CorsConfiguration.ALL)));
		corsConfiguration.setMaxAge(3600L);
		corsConfiguration.setAllowCredentials(true);
		routePredicateHandlerMapping.setCorsConfigurations(
				new HashMap<String, CorsConfiguration>() {{ put("/**", corsConfiguration); }});
		return corsConfiguration;
	}

}

