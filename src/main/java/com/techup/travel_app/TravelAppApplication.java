package com.techup.travel_app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelAppApplication {

	public static void main(String[] args) {
		// โหลด .env ก่อน Spring Boot เริ่มทำงาน
		Dotenv dotenv = Dotenv.configure()
			.directory("./")
			.ignoreIfMissing()
			.load();
		
		// ใส่ค่าลง System Properties
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		// เริ่ม Spring Boot
		SpringApplication.run(TravelAppApplication.class, args);
	}

}
