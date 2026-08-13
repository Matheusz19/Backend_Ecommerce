package plataforma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class PlataformaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlataformaApplication.class, args);
    }

}