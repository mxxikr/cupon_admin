package com.mxxikr.couponadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CouponAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponAdminApplication.class, args);
    }

}
