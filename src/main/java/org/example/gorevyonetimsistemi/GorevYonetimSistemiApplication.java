package org.example.gorevyonetimsistemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GorevYonetimSistemiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GorevYonetimSistemiApplication.class, args);
	}

}
