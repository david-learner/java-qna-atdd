package codesquad.web;

import codesquad.Util.HtmlFormDataBuilder;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import javax.xml.ws.Response;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

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

        Question q1 = new Question("질문1 제목", "질문1 내용");
        Question q2 = new Question("질문2 제목", "질문2 내용");
        qnaService.create(defaultUser(), q1);
        qnaService.create(defaultUser(), q2);
        questions = Arrays.asList(q1, q2);

        builder  = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void list() {
        builder.addParameter("questions", questions);
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().getForEntity("/", String.class, request);
        log.debug("response body is {}", response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("질문1 제목"), is(true));
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
        builder.addParameter("title", "QuestionAT 질문 제목");
        builder.addParameter("contents", "QuestionAT 질문 내용");
        builder.addParameter("writer", loginedUser.getUserId());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginedUser).postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("질문하기"), is(true));
    }

    @Test
    public void show() {
        // import.sql에 이미 2개의 질문이 있으므로, questionId는 3
        int questionId = 3;
        builder.addParameter("question", questions.get(0));
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", questionId), String.class, request);

        log.debug("response body is {}", response.getBody());
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody().contains("질문1 내용"), is(true));
    }

    @Test
    public void updateForm() {
        int questionId = 3;
        builder.addParameter("question", questions.get(0));
        ResponseEntity<String> response = basicAuthTemplate(loginedUser).getForEntity(String.format("/questions/%d/form", questionId), String.class, builder.build());

        log.debug("response body is {}", response.getBody());
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody().contains("질문1 내용"), is(true));
    }

    @Test
    public void update() {
        // put method로 바꾸기
        int questionId = 3;
        builder.addParameter("title", "질문1 수정된 제목");
        builder.addParameter("contents", "질문1 수정된 내용");

        ResponseEntity<String> response =
                basicAuthTemplate(loginedUser).exchange("/questions/{id}",
                        HttpMethod.PUT, builder.build(), String.class, questionId);
        assertThat(response.getStatusCode(),is(HttpStatus.FOUND));
        ResponseEntity<String> questionShowResponse = template().getForEntity(String.format("/questions/%d", questionId), String.class);
        assertThat(questionShowResponse.getBody().contains("질문1 수정된 제목"), is(true));
    }
}
