package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.utils.ReconciliationAct;
import ru.trustsoft.utils.TerminalSessions;
import ru.trustsoft.utils.WebUtils;

import java.io.IOException;
import java.security.Principal;

@Controller
public class UserinfoController {

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        Contragents contragent = null;
        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            contragent = user.getContragentsByContragentid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("userInfo", userInfo);
        if (contragent != null) {
            model.addAttribute("contragent", "Contragent:" + contragent.getContragentname() + " (" + contragent.getInn() + ")");
        } else {
            model.addAttribute("contragent", "Contragent: Not found");
        }

        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"disconnect"}, method = RequestMethod.POST)
    public String disconnectTS(Model model, Principal principal) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);

        TerminalSessions ts = new TerminalSessions();
        try {
            ts.getSessions();
            System.out.println("We have sessions");
            System.out.println(ts);
            ts.termineSession(principal.getName());
            System.out.println("Disconnecting");
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("userInfo", userInfo);

        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"reconciliation"}, method = RequestMethod.POST)
    public String reconciliation(Model model, Principal principal) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);

        ReconciliationAct ra = new ReconciliationAct();
        try {
            ra.getReconciliationAct();
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("userInfo", userInfo);

        return "userinfo";
    }

    @Autowired
    private UsersRepo userRepo;

}
