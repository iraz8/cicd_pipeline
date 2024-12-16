package com.razvan.gitfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GitFetcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitFetcherApplication.class, args);
	}

}
