package codesquad.web;

import codesquad.Util.HtmlFormDataBuilder;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final String TITLE = "국내에서 Ruby on";
    private static final int ID = 1;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QnaService qnaService;

    private User loginedUser;
    private List<Question> questions;
    private HtmlFormDataBuilder builder;

    @Before
    public void setUp() {
        loginedUser = defaultUser();
        questions = questionRepository.findAll();
        builder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void list() {
        builder.addParameter("questions", questions);
        ResponseEntity<String> response = template().getForEntity("/", String.class, builder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("runtime 에 reflect"), is(true));
    }

    @Test
    public void createForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void createForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void create() {
        builder.addParameter("title", "질문 create 제목");
        builder.addParameter("contents", "질문 create 내용");
        builder.addParameter("writer", loginedUser.getUserId());

        ResponseEntity<String> response = basicAuthTemplate(loginedUser)
                .postForEntity("/questions", builder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        response = template().getForEntity("/", String.class);
        assertThat(response.getBody().contains("질문 create 제목"), is(true));
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", ID), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(TITLE), is(true));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate(loginedUser).getForEntity(String.format("/questions/%d/form", ID), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(TITLE), is(true));
    }

    @Test
    public void update() {
        int questionId = 3;
        Question question = new Question("1 수정 전 제목", "1 수정 전 내용");
        qnaService.create(loginedUser, question);
        builder.addParameter("title", "2 수정 후 제목");
        builder.addParameter("contents", "2 수정 후 내용");

        ResponseEntity<String> response =
                basicAuthTemplate(loginedUser).exchange("/questions/{id}",
                        HttpMethod.PUT, builder.build(), String.class, questionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        ResponseEntity<String> questionShowResponse = template().getForEntity(String.format("/questions/%d", questionId), String.class);
        assertThat(questionShowResponse.getBody().contains("2 수정 후 제목"), is(true));
    }

    @Test
    public void delete() {
        ResponseEntity<String> response = basicAuthTemplate(loginedUser).exchange("/questions/{id}",
                HttpMethod.DELETE, builder.build(), String.class, ID);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(TITLE), is(false));
    }
}
