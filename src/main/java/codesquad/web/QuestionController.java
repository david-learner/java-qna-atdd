package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "userService")
    UserService userService;

    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser, Model model) {
        model.addAttribute("user", loginUser);
        return "/qna/form";
    }

    @PostMapping()
    public String create(@LoginUser User loginUser, String writer, String title, String contents) {
        // TODO question title, contens가 비었을 때 처리 로직
        Question question = new Question(title, contents);
        qnaService.create(loginUser, question);
        return "/home";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
//        Optional<Question> maybeQuestion = qnaService.findById(id);
//        if (maybeQuestion.isPresent()) {
//            log.debug("maybeQuestion is {}", maybeQuestion.toString());
//            model.addAttribute("question", maybeQuestion.get());
//        }
//        log.debug("maybeQuestion is passed");
        qnaService.findById(id).ifPresent(question ->
                model.addAttribute("question", question)
        );
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginedUser, @PathVariable Long id, Model model) {
        qnaService.findById(id).ifPresent(question -> model.addAttribute("question", question));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginedUser, @PathVariable Long id, String title, String contents) {
        qnaService.update(loginedUser, id, new Question(title, contents));
        return String.format("redirect:/questions/%d", id);
    }
}
