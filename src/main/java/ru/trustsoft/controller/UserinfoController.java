/**
 * TerminalServerManager
 *    UserinfoController.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

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
import java.util.Locale;

@Controller
@RequestMapping(path="/")
public class UserinfoController {

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

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser, messageByLocaleService);
        Contragents contragent = null;
        try {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            contragent = user.getContragentsByContragentid();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent") +
                    ": " + ex.toString());
        }
        String contragentInfo = WebUtils.toString(contragent, messageByLocaleService);

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("contragent", contragentInfo);
        model.addAttribute("raParameters", reconActParameters);

        return "userinfo";
    }

    @RequestMapping(value = { "/userinfo" }, params={"disconnect"}, method = RequestMethod.POST)
    public String disconnectTS(Model model, Principal principal, Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            TerminalSessions ts = new TerminalSessions(loginedUser);
            try {
                ts.termineSession(env.getProperty("tsmserveraddress"), loginedUser.getUsername());
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.disconnect"));
            } catch (IOException ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.disconnect") +
                        ": " + ex.toString());
            }

        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.disconnectdemo"));
        }
        return userInfo(model, principal, locale);
    }

    @RequestMapping(value = { "/userinfo" }, params={"orderreconciliation"}, method = RequestMethod.POST)
    public String orderReconciliation(Model model, Principal principal,
                                      @ModelAttribute("raParameters") ReconActParameters reconActParameters,
                                      Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            try {
                Users user = userRepo.findByUsername(loginedUser.getUsername());
                Contragents contragent = user.getContragentsByContragentid();
                if (contragent != null) {
                    String parameters1C = contragent.getInn() + ";" + LocalDate.now().toString() + ";" +
                            LocalDate.parse(reconActParameters.getStartDate()) + ";" +
                            LocalDate.parse(reconActParameters.getEndDate()) + ";" +
                            loginedUser.getUsername();

                    ReconciliationAct ra = new ReconciliationAct(loginedUser);
                    ra.orderReconciliationAct(env.getProperty("path_1c"), env.getProperty("path_1c_base"),
                            env.getProperty("path_epf"), env.getProperty("act_catalog"), parameters1C,
                            env.getProperty("username_1c_admin"), env.getProperty("password_1c_admin"));
                    model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.orderreconact"));
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
                }
            } catch (DateTimeException ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat") +
                        ": " + ex.toString());
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.orderreconact") +
                        ": " + ex.toString());
            }

        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.orderreconactdemo"));
        }
        return userInfo(model, principal, locale);
    }

    @RequestMapping(value = { "/userinfo" }, params={"getreconciliation"}, method = RequestMethod.POST)
    public String getReconciliation(HttpServletResponse response, Model model, Principal principal,
                                    @ModelAttribute("raParameters") ReconActParameters reconActParameters,
                                    Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String filename;
        try {
            filename = WebUtils.getActFileName(loginedUser, userRepo, reconActParameters);
        } catch (DateTimeException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat") +
                    ": " + ex.toString());
            return userInfo(model, principal, locale);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact") +
                    ": " + ex.toString());
            return userInfo(model, principal, locale);
        }
        if (filename.isEmpty()) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return userInfo(model, principal, locale);
        }
        ReconciliationAct ra = new ReconciliationAct(loginedUser);
        try {
            ra.getReconciliationAct(env.getProperty("act_catalog"), filename, response, this.servletContext);
        } catch (IOException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact") +
                    ": " + ex.toString());
        }
        return userInfo(model, principal, locale);
    }

    @RequestMapping(value = { "/userinfo" }, params={"sendreconciliation"}, method = RequestMethod.POST)
    public String sendReconciliation(Model model, Principal principal,
                                     @ModelAttribute("raParameters") ReconActParameters reconActParameters,
                                     Locale locale) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String filename;
        try {
            filename = WebUtils.getActFileName(loginedUser, userRepo, reconActParameters);
        } catch (DateTimeException ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.dateformat") +
                    ": " + ex.toString());
            return userInfo(model, principal, locale);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getreconact") +
                    ": " + ex.toString());
            return userInfo(model, principal, locale);
        }
        if (filename.isEmpty()) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return userInfo(model, principal, locale);
        }
        ReconciliationAct ra = new ReconciliationAct(loginedUser);
        try {
            ra.sendActFromEmail(sender, env.getProperty("tsmemailaddress"), env.getProperty("act_catalog"), filename,
                    reconActParameters.getEmail(), messageByLocaleService.getMessage("table.reconciliation"),
                    messageByLocaleService.getMessage("table.reconciliation"));
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.sendreconact"));
        } catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.sendreconact") +
                    ": " + ex.toString());
        }
        return userInfo(model, principal, locale);
    }

}
