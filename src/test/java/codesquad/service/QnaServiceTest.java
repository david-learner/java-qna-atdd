package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Test
    public void delete() throws CannotDeleteException {
        long id = 3L;
        User learner = new User(id, "learner", "password", "taewon", "email@email.com");
        Question question = new Question(id, "서비스 테스트는 목을 이용하나요?", "mockito를 쓰면 될까요?");
        question.writeBy(learner);
        Optional<Question> maybeQuestion = Optional.of(question);
        when(questionRepository.findById(id)).thenReturn(maybeQuestion);

        DeleteHistories histories = mock(DeleteHistories.class);
        List<DeleteHistory> historyList = mock(List.class);
        doNothing().when(deleteHistoryService).saveAll(historyList);
        histories = qnaService.delete(learner, id);

        System.out.println(histories.size()>0);
    }
}
