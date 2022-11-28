package com.example.cacheproxynew.service;


import com.example.cacheproxynew.entity.Element;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

@AutoConfigureWireMock(port = 3333)
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElementServiceTest {
    @Autowired
    private ElementService elementService;
    @Autowired
    private ObjectMapper mapper;
    @SpyBean
    private RestTemplate restTemplate;

    @Test
    public void getElementTest () throws JsonProcessingException {
        Element body = Element.builder().id(1).value("value").version(1).build();
        stubFor(get(urlEqualTo("/cache/api/1"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(mapper.writeValueAsString(body))));

        Element element = elementService.getElement(1);

        assertEquals(body.getId(), element.getId());
        assertEquals(body.getVersion(), element.getVersion());
        assertEquals(body.getValue(), element.getValue());
    }
    @Test
    public void saveElementTest () throws JsonProcessingException {
        Element element = Element.builder().id(1).value("value").version(1).build();
        stubFor(post(urlEqualTo("/cache/api"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(element.getId()))));
        stubFor(get(urlEqualTo("/cache/api/1"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(element))));

        elementService.saveElement(element);

        Mockito.verify(restTemplate, Mockito.times(4))
                .exchange(anyString(), (HttpMethod) any(), (HttpEntity<?>) any(), (Class<Object>) any());
    }
}
