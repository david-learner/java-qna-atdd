package codesquad.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswersTest {
    @Test
    public void addAnswer() {
        Answers answers = new Answers();
        User user = new User("learner", "password", "taewon", "eamil@email.com");
        Answer answer = new Answer(user, "요구사항을 잘 확인하자.");
        answers.add(answer);
        assertThat(answers.size() > 0, is(true));
    }
}
