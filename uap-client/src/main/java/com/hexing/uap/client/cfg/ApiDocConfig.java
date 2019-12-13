package com.hexing.uap.client.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger服务文档生成配置<br>
 * 
 * @author xia
 * @version 0.2
 * @since 0.2
 */
@Configuration
@EnableSwagger2
public class ApiDocConfig {
	@Autowired
	private ClientProperties clientProperties;

	@Bean
	public Docket customDocket() {

		ParameterBuilder ticketPar = new ParameterBuilder();
		List<Parameter> pars = new ArrayList<Parameter>();
		// 服务请求头统一追加【Authorization】属性
		ticketPar.name("Authorization").description("Token  start with [Bearer ] ").modelRef(new ModelRef("string"))
				.parameterType("header").required(false).build(); // header中的ticket参数非必填，传空也可以
		pars.add(ticketPar.build()); // 根据每个方法名也知道当前方法在设置什么参数

		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage(clientProperties.getRestBasePackage())).build()
				.globalOperationParameters(pars).apiInfo(apiInfo());

	}

	private ApiInfo apiInfo() {
		Contact contact = new Contact(" UAP ", "http://www.hxgroup.cn/", "grid17_020@hxgroup.co");
		@SuppressWarnings("rawtypes")
		Collection<VendorExtension> s = new ArrayList<>();
		return new ApiInfo(" Uap Restful API", "", "0.2", "", contact, "", "", s);
	}

}