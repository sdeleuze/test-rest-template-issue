package com.example.testresttemplateissue

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.HttpServerErrorException

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.springframework.boot.web.server.LocalServerPort

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinApplicationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var builder: RestTemplateBuilder

    @Test
    fun success() {
        val entity = restTemplate.getForEntity("/foo", Post::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isEqualTo(Post("foo"))
    }

    // Here is the issue with a low level HttpMessageNotReadableException thrown unlike in Javan seems to occur when Jackson Kotlin module + TestRestTemplate.NoOpResponseErrorHandler are used
    @Test
    fun errorHandlingWithTestRestTemplate() {
        val entity = restTemplate.getForEntity("/foo?converter=bar", Post::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun errorHandlingWithInjectedRestTemplateBuilder() {
        assertThatThrownBy { builder.rootUri("http://localhost:" + port).build().getForEntity("/foo?converter=bar", Post::class.java) }.isInstanceOf(HttpServerErrorException::class.java)
    }
}
