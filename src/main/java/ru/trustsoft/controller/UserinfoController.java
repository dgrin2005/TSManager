package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.ReconActParameters;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.ReconciliationAct;
import ru.trustsoft.utils.TerminalSessions;
import ru.trustsoft.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Controller
@RequestMapping(path="/")
public class UserinfoController {

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser, locale);
        Contragents contragent = null;
        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            contragent = user.getContragentsByContragentid();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent")+ ": " + ex.toString());
        }
        String contragentInfo = WebUtils.toString(contragent, locale);

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("contragent", contragentInfo);
        model.addAttribute("raParameters", reconActParameters);

        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"disconnect"}, method = RequestMethod.POST)
    public String disconnectTS(Model model, Principal principal, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            TerminalSessions ts = new TerminalSessions();
            try {
                ts.getSessions(env.getProperty("tsmserveraddress"));
                ts.termineSession(env.getProperty("tsmserveraddress"), loginedUser.getUsername());
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.disconnect"));
            } catch (IOException ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.disconnect")+ ": " + ex.toString());
            }

        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.disconnectdemo"));
        }
        return userInfo(model, principal, locale);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"orderreconciliation"}, method = RequestMethod.POST)
    public String orderReconciliation(Model model, Principal principal, @ModelAttribute("raParameters") ReconActParameters reconActParameters,
                                      Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {

            try {
                Users user = userRepo.findByUsername(loginedUser.getUsername());
                Contragents contragent = user.getContragentsByContragentid();
                if (contragent != null) {
                    String parameters1C = contragent.getInn() + ";" + LocalDate.now().toString() + ";" +
                            LocalDate.parse(reconActParameters.getStartDate()) + ";" + LocalDate.parse(reconActParameters.getEndDate()) + ";" +
                            loginedUser.getUsername();

                    System.out.println(parameters1C);

                    ReconciliationAct ra = new ReconciliationAct();
                    ra.orderReconciliationAct(env.getProperty("path_1c"), env.getProperty("path_1c_base"),
                            env.getProperty("path_epf"), env.getProperty("act_catalog"), parameters1C);
                    model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.orderreconact"));
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
                }
            } catch (DateTimeException ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat")+ ": " + ex.toString());
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.orderreconact")+ ": " + ex.toString());
            }

        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.orderreconactdemo"));
        }

        return userInfo(model, principal, locale);
        //return "redirect:/userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"getreconciliation"}, method = RequestMethod.POST)
    public String getReconciliation(HttpServletResponse response, Model model, Principal principal,
                                    @ModelAttribute("raParameters") ReconActParameters reconActParameters, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String filename;
        try {
            filename = WebUtils.getActFileName(loginedUser, userRepo, reconActParameters);
        } catch (DateTimeException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat")+ ": " + ex.toString());
            return userInfo(model, principal, locale);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact")+ ": " + ex.toString());
            return userInfo(model, principal, locale);
        }
        if (filename.isEmpty()) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return userInfo(model, principal, locale);
        }
        System.out.println(filename);
        ReconciliationAct ra = new ReconciliationAct();
        try {
            ra.getReconciliationAct(env.getProperty("act_catalog"), filename, response, this.servletContext);
        } catch (IOException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact")+ ": " + ex.toString());
            return userInfo(model, principal, locale);
        }
        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"sendreconciliation"}, method = RequestMethod.POST)
    public String sendReconciliation(Model model, Principal principal, @ModelAttribute("raParameters") ReconActParameters reconActParameters,
                                     Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String filename;
        try {
            filename = WebUtils.getActFileName(loginedUser, userRepo, reconActParameters);
        } catch (DateTimeException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat")+ ": " + ex.toString());
            return userInfo(model, principal, locale);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact")+ ": " + ex.toString());
            return userInfo(model, principal, locale);
        }
        if (filename.isEmpty()) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return userInfo(model, principal, locale);
        }
        System.out.println(filename);
        ReconciliationAct ra = new ReconciliationAct();
        try {
            ra.sendEmail(sender, env.getProperty("tsmemailaddress"), env.getProperty("act_catalog"), filename, reconActParameters.getEmail());
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.sendreconact"));
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.sendreconact")+ ": " + ex.toString());
        }
        return userInfo(model, principal, locale);
    }

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    ReconActParameters reconActParameters;

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    MessageByLocaleService messageByLocaleService;


}
