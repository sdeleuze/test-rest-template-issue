package com.example.testresttemplateissue;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    public void success() {
        ResponseEntity<Post> entity = restTemplate.getForEntity("/foo", Post.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualTo(new Post("foo"));
    }

    // I am not sure to understand why TestRestTemplate should behave differently to RestTemplate (due to its NoOpResponseErrorHandler), this is confusing
    @Test
    public void errorHandlingWithTestRestTemplate() {
        ResponseEntity<Post> entity = restTemplate.getForEntity("/foo?converter=bar", Post.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void errorHandlingWithInjectedRestTemplateBuilder() {
        assertThatThrownBy(() -> builder.rootUri("http://localhost:" + port).build().getForEntity("/foo?converter=bar", Post.class)).isInstanceOf(HttpServerErrorException.class);
    }


}
