package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    private static final Logger log = LoggerFactory.getLogger(Question.class);

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this(0L, title, contents);
    }

    public Question(long id, String title, String contents) {
        super(id);
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isOwner(List<Answer> answers) {
        for (Answer answer : answers) {
            if (!answer.isOwner(writer)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDeleted() {
        return deleted;
    }


    public static void delete(QuestionRepository questionRepository, User loginUser, long id) throws CannotDeleteException {
        Optional<Question> question = questionRepository.findById(id);
        log.debug("question get");
        if (!question.isPresent()) {
            throw new NullPointerException("Question is not exist.");
        }
        log.debug("question is present");
        question.get().delete(loginUser);
    }

    public void delete(User loginedUser) throws CannotDeleteException {
        if (!isOwner(loginedUser)) {
            throw new CannotDeleteException("Mismatch owner.");
        }
        if (!isOwner(answers)) {
            throw new CannotDeleteException("Some answers are not yours.");
        }
        deleted = true;
    }

    public Question update(User loginedUser, Question question) throws UnAuthorizedException {
        if (!this.isOwner(loginedUser)) {
            throw new UnAuthorizedException();
        }
        title = question.getTitle();
        contents = question.getContents();
        return this;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
