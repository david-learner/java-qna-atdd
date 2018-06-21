package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.constraints.Null;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, QuestionDto newQuestion) {
        Question question = newQuestion.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, QuestionDto updateQuestionDto) throws UnAuthorizedException {
        Optional<Question> question = questionRepository.findById(id);
        if (!question.isPresent()) {
            throw new NullPointerException("Question Update Error");
        }
        return question.get().update(loginUser, updateQuestionDto.toQuestion());
    }

    @Transactional
    public void delete(User loginUser, long questionId) throws CannotDeleteException {
        Optional<Question> question = questionRepository.findById(questionId);
        if (!question.isPresent()) {
            throw new CannotDeleteException("Requested question is not exist.");
        }
        question.get().delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
