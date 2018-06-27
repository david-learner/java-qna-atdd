package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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

    @Embedded
    private Answers answers = new Answers();

    @Embedded
    private DeleteHistories histories = new DeleteHistories();

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

    public boolean isDeleted() {
        return deleted;
    }

    private DeleteHistories deleteAllAnswer(Answers answers) throws CannotDeleteException {
        return answers.deleteAll(writer);
    }

    public DeleteHistories delete(User loginedUser) throws CannotDeleteException {
        if (isDeleted()) {
            throw new CannotDeleteException("Already deleted.");
        }
        if (!isOwner(loginedUser)) {
            throw new CannotDeleteException("Mismatch owner.");
        }
        deleted = true;

        DeleteHistories histories = deleteAllAnswer(answers);
        histories.addHistory(new DeleteHistory(ContentType.QUESTION, getId(), writer, LocalDateTime.now()));
        return histories;
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
