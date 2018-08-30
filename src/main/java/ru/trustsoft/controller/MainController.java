/**
 * TerminalServerManager
 *    MainController.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.logging.Logger;


@Controller
public class MainController {

    private static final Logger logger = Logger.getLogger(String.valueOf(MainController.class));

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String welcomePage(Model model, Principal principal, HttpServletRequest request)
    {
        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
            logger.info(loginedUser.getUsername() + " login from ip:"+ request.getRemoteAddr());
        }
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {
        return "login";
    }

    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        return "logoutSuccessful";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model) {
        return "403Page";
    }

}
