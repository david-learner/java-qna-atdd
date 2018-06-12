package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void createForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void createForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }
}
