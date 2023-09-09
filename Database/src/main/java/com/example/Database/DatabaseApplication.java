package com.example.Database;

import com.example.Database.index.IndexManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DatabaseApplication {
	public static void main(String[] args) {
		SpringApplication.run(DatabaseApplication.class, args);
	}
}
