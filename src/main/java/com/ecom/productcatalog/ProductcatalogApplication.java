package com.ecom.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.ecom.productcatalog.model")
@EnableJpaRepositories("com.ecom.productcatalog.repository")
public class ProductcatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductcatalogApplication.class, args);
	}

}