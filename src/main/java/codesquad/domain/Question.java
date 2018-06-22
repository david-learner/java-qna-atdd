package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Embedded
    private Answers answers = new Answers();
//    private List<Answer> answers = new ArrayList<>();

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

//    public boolean isOwner(List<Answer> answers) {
//        for (Answer answer : answers) {
//            if (!answer.isOwner(writer)) {
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean isDeleted() {
        return deleted;
    }

//    private List<DeleteHistory> deleteAllAnswer(List<Answer> answers) throws CannotDeleteException {
//        List<DeleteHistory> histories = new ArrayList<>();
//
//        if (!isOwner(answers)) {
//            throw new CannotDeleteException("Some answers are not yours.");
//        }
//
//        for (Answer answer : answers) {
//            answer.delete();
//            histories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), writer, LocalDateTime.now()));
//        }
//
//        return histories;
//    }

    private List<DeleteHistory> deleteAllAnswer(Answers answers) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList<>();

        if (answers.hasOtherOwner()) {
            throw new CannotDeleteException("Some answers are not yours.");
        }

//        for (Answer answer : answers) {
//            answer.delete();
//            histories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), writer, LocalDateTime.now()));
//        }

        return histories;
    }

    public List<DeleteHistory> delete(User loginedUser) throws CannotDeleteException {
        if (isDeleted()) {
            throw new CannotDeleteException("Already deleted.");
        }
        if (!isOwner(loginedUser)) {
            throw new CannotDeleteException("Mismatch owner.");
        }
        deleted = true;

//        List<DeleteHistory> histories = deleteAllAnswer(answers);
        List<DeleteHistory> histories = deleteAllAnswer(answers);
        histories.add(new DeleteHistory(ContentType.QUESTION, getId(), writer, LocalDateTime.now()));
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
