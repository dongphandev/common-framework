package com.tmoncorp.crawler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.tmoncorp.crawler.filemanagement.FileUtile;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's see the input from command:");
           

           TmonAdultCrawler crawler = (TmonAdultCrawler)ctx.getBean("tmonAdultCrawler");
           crawler.crawl("/Users/dongphan/Documents/Test");
        };
    }

}
