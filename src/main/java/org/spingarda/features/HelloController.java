package org.spingarda.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Ivan on 19/11/2016.
 */
@Controller
@RequestMapping("/")
public class HelloController {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private Protector protector;

    @Autowired
    private Facebook facebook;

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }

        if (protector.isRunning()) {
            model.addAttribute("log", protector.getLog());
            return "running";
        }

        model.addAttribute("botParams", new ProtectorParams());
        model.addAttribute("accounts", facebook.pageOperations().getAccounts());
        return "hello";
    }

    @GetMapping("/log")
    public String getLog(Model model) throws Exception {
        model.addAttribute("log", protector.getLog());
        return "running";
    }

    @PostMapping
    public String runSpam(Model model, @ModelAttribute ProtectorParams params) throws Exception {
        model.addAttribute("log", protector.getLog());

        System.out.println(params);

        if (!protector.isRunning()) {
            protector.run(facebook, params);
        }
        return "running";
    }
}
