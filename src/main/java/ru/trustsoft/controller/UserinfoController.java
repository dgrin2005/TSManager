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
import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequestMapping(path="/")
public class UserinfoController {

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal, Locale locale) {

        //Calendar date1 = Calendar.getInstance();
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser, locale);
        Contragents contragent = null;
        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            contragent = user.getContragentsByContragentid();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error:" + ex.toString());
        }
        String contragentInfo = WebUtils.toString(contragent, locale);

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("contragent", contragentInfo);
        model.addAttribute("season", season);

        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"disconnect"}, method = RequestMethod.POST)
    public String disconnectTS(Model model, Principal principal, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        TerminalSessions ts = new TerminalSessions();
        try {
            ts.getSessions();
            ts.termineSession(loginedUser.getUsername());
        } catch (IOException ex) {
            model.addAttribute("errorMessage", "Error:" + ex.toString());
        }

        return userInfo(model, principal, locale);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"orderreconciliation"}, method = RequestMethod.POST)
    public String orderReconciliation(Model model, Principal principal, @ModelAttribute("season") Season season, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            Contragents contragent = user.getContragentsByContragentid();
            if (contragent != null) {
                String parameters1C = contragent.getInn() + ";" + LocalDate.now().toString() + ";" +
                        LocalDate.parse(season.getStartDate()) + ";" + LocalDate.parse(season.getEndDate()) + ";" +
                        loginedUser.getUsername();

                System.out.println(parameters1C);

                ReconciliationAct ra = new ReconciliationAct();
                ra.orderReconciliationAct(parameters1C);
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error:" + ex.toString());
        }
        return userInfo(model, principal, locale);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"getreconciliation"}, method = RequestMethod.POST)
    public String getReconciliation(HttpServletResponse response, Model model, Principal principal, @ModelAttribute("season") Season season, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            Contragents contragent = user.getContragentsByContragentid();
            if (contragent != null) {
                String filename = contragent.getInn() + "_" + WebUtils.toString(LocalDate.now()) + "_" +
                    WebUtils.toString(LocalDate.parse(season.getStartDate())) + "_" +
                    WebUtils.toString(LocalDate.parse(season.getEndDate())) + "_" +
                    loginedUser.getUsername() + ".pdf";

                System.out.println(filename);

                ReconciliationAct ra = new ReconciliationAct();
                ra.getReconciliationAct(filename, response, this.servletContext);
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error:" + ex.toString());
            return userInfo(model, principal, locale);
        }
        return "";
    }

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private ServletContext servletContext;

    private Season season = new Season();

    private String errorMessage;

}
