package io.ossnass.advSpring.test;

import io.ossnass.advSpring.test.author.SimpleAuthorDto;
import io.ossnass.advSpring.test.book.BookDto;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookTest {
    private final ArrayList<String> bookNames = new ArrayList<>();
    @LocalServerPort
    int localPort;
    ArrayList<BookDto> bookList = new ArrayList<>();
    private TestRestTemplate restTemplate;
    private String baseURI;


    @BeforeEach
    public void init() throws FileNotFoundException {
        baseURI = "http://localhost:" + localPort + "/api/v1/";
        restTemplate = new TestRestTemplate();
        var authorsFile = ResourceUtils.getFile("classpath:data/books.csv");
        var scanner = new Scanner(authorsFile);
        while (scanner.hasNextLine()) {
            bookNames.add(scanner.nextLine());
        }
        scanner.close();
    }

    @Test
    @Order(1)
    void createBooks() {
        for (int i = 0; i < 3; i++) {
            var title = bookNames.get(i);
            var authorsOfBook = new ArrayList<SimpleAuthorDto>();
            if (i == 0)
                authorsOfBook.add(new SimpleAuthorDto(1, ""));
            else {
                authorsOfBook.add(new SimpleAuthorDto(2, ""));
                authorsOfBook.add(new SimpleAuthorDto(3, ""));
            }
            var book = new BookDto(null, title, authorsOfBook);
            bookList.add(book);
            var res = restTemplate.postForObject(baseURI + "books", book, BookDto.class);
            assertThat(res.id()).isNotNull();
            assertThat(res.authors().size()).isGreaterThan(0);
        }
        var res = restTemplate.getForObject(baseURI + "books", BookDto[].class);
        assertThat(res).isNotNull();
        assertThat(res.length).isEqualTo(3);
    }

    @Test
    @Order(2)
    void readTest() {
        for (int i = 0; i < 2; i++) {
            var name = bookNames.get(i);

            var params = new HashMap<String, String>();
            params.put("filter", "title");
            params.put("filterOperation", "equals");
            params.put("filterValue", name);
            var res = restTemplate.getForObject(baseURI + "books?filter={filter}&filterOperation={filterOperation}&filterValue={filterValue}", BookDto[].class, params);

            assertThat(res.length).isEqualTo(1);
            assertThat(res[0].title()).isEqualTo(name);
        }
    }

    @Test
    @Order(3)
    void updateTest() {
        var name = bookNames.get(2);
        var param = new HashMap<String, String>();
        param.put("id", "1");
        var res = restTemplate.getForObject(baseURI + "books/ones/{id}", BookDto[].class, param);
        var book = new BookDto(res[0].id(), name, null);
        restTemplate.put(baseURI + "books", book);
        var res2 = restTemplate.getForObject(baseURI + "books/ones/{id}", BookDto[].class, param);
        assertThat(res2[0].title()).isEqualTo(name);
    }
}
