package io.ossnass.advSpring.test;

import io.ossnass.advSpring.test.author.AuthorDto;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(3)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteTest {

    @LocalServerPort
    int localPort;
    private TestRestTemplate restTemplate;
    private String baseURI;

    @BeforeEach
    public void init() {
        baseURI = "http://localhost:" + localPort + "/api/v1/";
        restTemplate = new TestRestTemplate();
    }

    @Test
    @Order(1)
    public void testDelete() {
        var param = new HashMap<String, String>();
        param.put("id", "1");
        restTemplate.delete(baseURI + "authors/{id}", param);
        var response = restTemplate.getForEntity(baseURI + "authors/ones/{id}", AuthorDto[].class, param);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(0);
    }
}
