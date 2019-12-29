package sec.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.config.CustomUserDetailsService;
import sec.project.domain.User;
import sec.project.domain.Bank;
import sec.project.repository.SignupRepository;
import sec.project.repository.BankRepository;
import sec.project.repository.UserRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankRepository bankRepository;
    
    @Autowired
    private CustomUserDetailsService customDatabase;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadLoginPage() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginInto(@RequestParam String username, @RequestParam String password, Model model) {
        model.addAttribute("name", username);
        model.addAttribute("password", password);
        return openPersonalPage(username, password, model);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public String openWithUrl(@PathVariable String username, Model model) {
        List<User> list = userRepository.findAll();
        boolean b = false;
        for (User account : list) {
            if (account.getUsername().equals(username)) {
                b = true;
            }
        }
        if (b == true) {
            model.addAttribute("acc", userRepository.findByUsername(username));
            model.addAttribute("stories", bankRepository.findByUser(userRepository.findByUsername(username)));
            return "personalPage";
        }

        return "redirect:/login";
    }

    @RequestMapping(value = "/personalPage", method = RequestMethod.GET)
    public String openPersonalPage(String username, String password, Model model) {
        if (username.equals("admin")) {
            if (password.equals("1234")) {
                return "redirect:/admin";
            } else {
                model.addAttribute("error", "bad credentials, try again or create new account");
                return "login";
            }
        }
        List<User> list = userRepository.findAll();
        boolean b = false;
        for (User account : list) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                b = true;
            }
        }
        if (b == false) {
            model.addAttribute("error", "bad credentials, try again or create new account");
            return "login";
        }
        model.addAttribute("acc", userRepository.findByUsername(username));
        model.addAttribute("banks", bankRepository.findByUser(userRepository.findByUsername(username)));
        return "personalPage";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String loadRegistrationPage() {
        return "registration";
    }

    @RequestMapping(value = "/addBank/{username}", method = RequestMethod.POST)
    public String addNewStory(@PathVariable String username, @RequestParam String bankname, @RequestParam String number, Model model) {
        User acc = userRepository.findByUsername(username);
        Bank newStory = new Bank();
        newStory.setUser(acc);
        newStory.setName(bankname);
        newStory.setNumber(number);
        bankRepository.save(newStory);
        model.addAttribute("acc", userRepository.findByUsername(username));
        model.addAttribute("banks", bankRepository.findByUser(acc));

        return "personalPage";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String createNewAccount(@RequestParam String username, @RequestParam String password, Model model) throws Exception {
        User newAcc = new User();
        newAcc.setUsername(username);
        newAcc.setPassword(password);
        userRepository.save(newAcc);
        customDatabase.saveUserToDatabase(newAcc.getId(), username, password);
        model.addAttribute("info", "New account created!");
        return "done";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String loadAdminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Long id) {
        List<Bank> allS = bankRepository.findAll();
        for (Bank story : allS) {
            if (story.getUser().getId() == id) {
                bankRepository.delete(story.getId());
            }
        }
        userRepository.delete(id);
        return "redirect:/admin";
    }

}
