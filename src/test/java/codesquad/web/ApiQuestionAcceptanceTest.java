package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");

    private String getUserId(QuestionDto questionDto, User writer) {
        Question question = questionDto.toQuestion();
        question.writeBy(writer);
        return question.getWriter().getUserId();
    }

    @Test
    public void create() {
        // id를 5L로 했을 때에는 5L이 db에 들어가지 않고 자동으로 생성되는 값이 들어간다.
        QuestionDto newQuestion = new QuestionDto("API 질문 제목", "API 질문 내용");

        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        long questionId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        newQuestion.setId(questionId);
        User writer = findByUserId(getUserId(newQuestion, LEARNER));
        // location을 요청하면 값이 JSON으로 나올테고, JSON은 새로 만들어진 객체 데이터인가?
        QuestionDto dbQuestion = basicAuthTemplate(writer).getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void show_not_login() {
        long questionId = 2;
        ResponseEntity<String> response = template().getForEntity(String.format("/api/questions/%d", questionId), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto questionDto = new QuestionDto(2L, "runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?", "설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다.");
        // response에서 리턴된 json형식을 객체로 다시 뽑아낼 수 있는가?
        QuestionDto dbQuestionDto = template().getForObject(String.format("/api/questions/%d", questionId), QuestionDto.class);
        assertThat(questionDto.equals(dbQuestionDto), is(true));
    }

    @Test
    public void update_owner_login() {
        QuestionDto newQuestion = new QuestionDto("Java8 Lambda에서 Exception 처리 방법은?", "Exception 로직을 만들어요.");
        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        long questionId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        QuestionDto updateQuestion = new QuestionDto(questionId, "Java100 Lambda Excption 처리는?", "Java 100은 예외가 없어요.");
        basicAuthTemplate(LEARNER).put(location, updateQuestion);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(updateQuestion, is(dbQuestion));
    }

    @Test
    public void upate_not_owner_login() {
        QuestionDto newQuestion = new QuestionDto("Kotlin에서 Exception 처리 방법은?", "Kotlin을 안 배워서 몰라요:(");
        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        long questionId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        QuestionDto updateQuestion = new QuestionDto(questionId, "Java100 Lambda Excption 처리는?", "Java 100은 예외가 없어요.");
        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(updateQuestion.equals(dbQuestion), is(false));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = new QuestionDto("Spring에서 Exception 처리 방법은?", "ExceptionHandler를 이용하면 돼!");
        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        basicAuthTemplate(LEARNER).delete(location);

        response = basicAuthTemplate(LEARNER).getForEntity(location, String.class);
        assertThat(response.getBody().contains("deleted question"),is(true));
    }
}
