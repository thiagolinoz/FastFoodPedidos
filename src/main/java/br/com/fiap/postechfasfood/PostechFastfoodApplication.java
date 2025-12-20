package br.com.fiap.postechfasfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PostechFastfoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostechFastfoodApplication.class, args);
    }
}
