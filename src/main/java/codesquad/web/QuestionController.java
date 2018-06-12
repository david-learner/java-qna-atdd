package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.security.LoginUser;
import codesquad.service.UserService;
import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Resource(name = "userService")
    UserService userService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser, Model model) {
        model.addAttribute("user", loginUser);
        return "/qna/form";
    }
}
