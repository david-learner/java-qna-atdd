package com.hardlearner.javaqnaatdd.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static final Logger log =  LoggerFactory.getLogger(UserControllerTest.class);
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void userSignUp() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }
}
