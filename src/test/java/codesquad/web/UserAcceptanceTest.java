package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import codesquad.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import support.test.AcceptanceTest;

public class UserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<String> response;
    private HtmlFormDataBuilder builder;

    @Test
    public void createForm() throws Exception {
        response = template().getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        String userId = "testuser";

        builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", userId);
        builder.addParameter("password", "password");
        builder.addParameter("name", "자바지기");
        builder.addParameter("email", "javajigi@slipp.net");

        response = template().postForEntity("/users", builder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(userRepository.findByUserId(userId).isPresent(), is(true));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void list() throws Exception {
        response = template().getForEntity("/users", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultUser().getEmail()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        response = template().getForEntity(String.format("/users/%d/form", defaultUser().getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/users/%d/form", loginUser.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(loginUser.getEmail()), is(true));
    }

    @Test
    public void update_no_login() throws Exception {
        response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("_method", "put");
        builder.addParameter("_method", "put");
        builder.addParameter("password", "password2");
        builder.addParameter("name", "자바지기2");
        builder.addParameter("email", "javajigi@slipp.net");

        return template.postForEntity(String.format("/users/%d", defaultUser().getId()), builder.build(), String.class);
    }

    @Test
    public void update() throws Exception {
        response = update(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }
}
