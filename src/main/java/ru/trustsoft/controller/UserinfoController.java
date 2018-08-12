package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.Season;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.utils.ReconciliationAct;
import ru.trustsoft.utils.TerminalSessions;
import ru.trustsoft.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping(path="/")
public class UserinfoController {

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {

        //Calendar date1 = Calendar.getInstance();
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
        model.addAttribute("season", season);

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

        return userInfo(model, principal);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"orderreconciliation"}, method = RequestMethod.POST)
    public String orderReconciliation(Model model, Principal principal, @ModelAttribute("season") Season season) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);

        try {

            Contragents contragent = null;
            try {
                Users user = userRepo.findByUsername(loginedUser.getUsername());
                contragent = user.getContragentsByContragentid();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (contragent != null) {
                String parameters1C = contragent.getInn() + ";" + LocalDate.now().toString() + ";" +
                        LocalDate.parse(season.getStartDate()) + ";" + LocalDate.parse(season.getEndDate()) + ";" +
                        loginedUser.getUsername();

                System.out.println(parameters1C);

                ReconciliationAct ra = new ReconciliationAct();
                try {
                    ra.orderReconciliationAct(parameters1C);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error:" + ex.toString());
            return userInfo(model, principal);
        }
        return userInfo(model, principal);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"getreconciliation"}, method = RequestMethod.POST)
    public void getReconciliation(HttpServletResponse response, Model model, Principal principal, @ModelAttribute("season") Season season) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);

        Contragents contragent = null;
        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            contragent = user.getContragentsByContragentid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contragent != null) {
            String filename = contragent.getInn() + "_" + WebUtils.toString(LocalDate.now()) + "_" +
                    WebUtils.toString(LocalDate.parse(season.getStartDate())) + "_" +
                    WebUtils.toString(LocalDate.parse(season.getEndDate())) + "_" +
                    loginedUser.getUsername() + ".pdf";

            System.out.println(filename);

            ReconciliationAct ra = new ReconciliationAct();
            try {
                ra.getReconciliationAct(filename, response, this.servletContext);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

/*
        model.addAttribute("userInfo", userInfo);
        if (contragent != null) {
            model.addAttribute("contragent", "Contragent:" + contragent.getContragentname() + " (" + contragent.getInn() + ")");
        } else {
            model.addAttribute("contragent", "Contragent: Not found");
        }
        model.addAttribute("season", season);

        return "userinfo";
        return userInfo(model, principal);
*/
        //return "redirect:/userinfo";
    }

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private ServletContext servletContext;

    private Season season = new Season();

    private String errorMessage;

}
