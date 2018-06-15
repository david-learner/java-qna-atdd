package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");

    private String getUserId(QuestionDto question) {
        return question.toQuestion().getWriter().getUserId();
    }

    @Test
    public void create() {
        QuestionDto newQuestion = new QuestionDto("API 질문 제목", "API 질문 내용");
        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        User writer = findByUserId(getUserId(newQuestion));
        // location을 요청하면 값이 JSON으로 나올테고, JSON은 새로 만들어진 객체 데이터인가?
        QuestionDto dbQuestion = basicAuthTemplate(writer).getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }
}
