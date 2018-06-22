package codesquad.domain;

import java.util.ArrayList;
import java.util.List;

public class Answers {
    List<Answer> answers = new ArrayList<>();

    public Answers() {

    }

    public Answers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> add(Answer answer) {
        answers.add(answer);
        return answers;
    }

    public int getSize() {
        return answers.size();
    }
}
