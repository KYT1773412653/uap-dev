package com.hexing.uap.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexing.uap.rest.organization.OrgQueryRequest;
import com.hexing.uap.rest.organization.OrgTreeRequest;
import com.hexing.uap.rest.organization.OrganizationCreateRequest;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { UapApplication.class })
//@Transactional
public class OrgTest {

	protected MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

//	@Before()
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	public void creatOrg() {
		OrganizationCreateRequest request = new OrganizationCreateRequest();
		request.setCode("test");
		request.setName("test");
		request.setState("1");
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonStr = mapper.writeValueAsString(request);
			MockHttpServletResponse response = mockMvc.perform(post("/org", "json").characterEncoding("UTF-8")
					.contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes())).andReturn().getResponse();
			System.out.println("--------返回的json = " + response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getOrgList() {
		OrgQueryRequest orgQueryRequest = new OrgQueryRequest();
		orgQueryRequest.setLimit(100);
		orgQueryRequest.setStart(0);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonStr = mapper.writeValueAsString(orgQueryRequest);
			MockHttpServletResponse response = mockMvc
					.perform(post("/org/list", "json").characterEncoding("UTF-8")
							.contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
					.andReturn().getResponse();
			System.out.println("--------返回的getStatus = " + response.getStatus());
			System.out.println("--------返回的json = " + response.getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findSubOrgs() {
		OrgTreeRequest orgTreeRequest = new OrgTreeRequest();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonStr = mapper.writeValueAsString(orgTreeRequest);
			MockHttpServletResponse response = mockMvc
					.perform(post("/org/sublist", "json").characterEncoding("UTF-8")
							.contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
					.andReturn().getResponse();
			System.out.println("--------返回的getStatus = " + response.getStatus());
			System.out.println("--------返回的json = " + response.getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
