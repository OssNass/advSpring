package io.ossnass.advSpring.test;

import io.ossnass.advSpring.test.author.AuthorDto;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(1)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorTest {

    private final ArrayList<String> authrNames = new ArrayList<>();
    @LocalServerPort
    int localPort;
    ArrayList<AuthorDto> authorList = new ArrayList<>();
    private TestRestTemplate restTemplate;
    private String baseURI;

    public AuthorTest() {
    }

    @BeforeEach
    public void init() throws FileNotFoundException {
        baseURI = "http://localhost:" + localPort + "/api/v1/";
        restTemplate = new TestRestTemplate();
        var authorsFile = ResourceUtils.getFile("classpath:data/authors.csv");
        var scanner = new Scanner(authorsFile);
        while (scanner.hasNextLine()) {
            authrNames.add(scanner.nextLine());
        }
        scanner.close();
    }

    @Test
    @Order(1)
    void createTest() {
        for (int i = 0; i < 3; i++) {
            var name = authrNames.get(i);
            var author = new AuthorDto(null, name, null);
            authorList.add(author);
            var res = restTemplate.postForObject(baseURI + "authors", author, AuthorDto.class);
            assertThat(res.id()).isNotNull();
        }
        var res = restTemplate.getForObject(baseURI + "authors", AuthorDto[].class);
        assertThat(res).isNotNull();
        assertThat(res.length).isEqualTo(3);
    }

    @Test
    @Order(2)
    void readTest() {
        for (int i = 0; i < 3; i++) {
            var name = authrNames.get(i);

            var params = new HashMap<String, String>();
            params.put("filter", "name");
            params.put("filterOperation", "equals");
            params.put("filterValue", name);
            var res = restTemplate.getForObject(baseURI + "authors?filter={filter}&filterOperation={filterOperation}&filterValue={filterValue}", List.class, params);

            assertThat(res.size()).isEqualTo(1);
            assertThat(((LinkedHashMap<?, ?>) res.get(0)).get("name")).isEqualTo(name);
        }
    }

    @Test
    @Order(3)
    void updateTest() {
        var name = authrNames.get(3);
        var param = new HashMap<String, String>();
        param.put("id", "1");
        var res = restTemplate.getForObject(baseURI + "authors/ones/{id}", List.class, param);
        System.out.println(res.size());
        var author = new AuthorDto(Integer.parseInt(((LinkedHashMap<?, ?>) res.get(0)).get("id").toString()), name, null);
        restTemplate.put(baseURI + "authors", author);
        var res2 = restTemplate.getForObject(baseURI + "authors/ones/{id}", List.class, param);
        assertThat(((ArrayList<LinkedHashMap<?, ?>>) res2).get(0).get("name")).isEqualTo(name);
    }
}
