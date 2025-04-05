package io.ossnass.advSpring.test;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.ossnass.advSpring"})
@TestClassOrder(ClassOrderer.OrderAnnotation.class)

public class AdvSpring {
    public static void main(String[] args) {
        SpringApplication.run(AdvSpring.class, args);
    }
}
