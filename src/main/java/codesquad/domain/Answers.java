package codesquad.domain;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Answers {
    private static final int FIRST = 0;
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

    @Override
    public String toString() {
        return "Answers{" +
                "answers=" + Arrays.toString(answers.toArray()) +
                '}';
    }

    public boolean hasOtherOwner() {
        User writer = answers.get(FIRST).getWriter();
        for(int i = 1; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            if (!answer.getWriter().equals(writer)) {
                return true;
            }
        }
        return false;
    }
}
