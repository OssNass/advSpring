package io.ossnass.advSpring.test;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookTest {
    @Test
    @Order(1)
    void test() {
    }
}
