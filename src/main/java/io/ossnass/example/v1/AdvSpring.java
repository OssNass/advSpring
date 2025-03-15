package io.ossnass.example.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.ossnass.advSpring", "io.ossnass.example"})
public class AdvSpring {
    public static void main(String[] args) {
        SpringApplication.run(AdvSpring.class, args);
    }
}
