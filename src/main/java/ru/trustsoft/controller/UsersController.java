/**
 * TerminalServerManager
 *    UsersController.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.TablePageSize;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.ControllerUtils;
import ru.trustsoft.utils.TerminalSessions;
import ru.trustsoft.utils.UsersManagement;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path="/")
public class UsersController {

    private static Integer defaultPage = 1;
    private static Integer defaultPageSize = 5;
    private static String defaultOrder = "username_a";

    @Autowired
    private Users findedUser;

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private ContragentsRepo contragentRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

    @Autowired
    private Environment env;

    @GetMapping(path="/userslist")
    public String usersList(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                            @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size,
                            @RequestParam("order") Optional<String> order) {

        Integer currentPage;
        Integer currentPageSize;
        String currentOrder;

        if (page.isPresent()) {
            currentPage = page.get() > 0 ? page.get() : defaultPage;
        } else {
            currentPage = defaultPage;
        }

        if (size.isPresent()) {
            currentPageSize = size.get() > 0 ? size.get() : defaultPageSize;
        } else {
            currentPageSize = defaultPageSize;
        }

        currentOrder = order.orElseGet(() -> defaultOrder);

        if (tablePageSize.getSize() == null) {
            tablePageSize.setSize(currentPageSize);
        }

        Users user = new Users();
        if (findedUser != null) {
            user = findedUser;
        }
        model.addAttribute("tablePageSize", tablePageSize);
        model.addAttribute("currentPageSize", currentPageSize);
        currentPage = ControllerUtils.addPageAttributes(model, ((List<Users>)userRepo.findAll()).size(), currentPage,
                currentPageSize);
        List<Users> userPage;
        switch (currentOrder) {
            case "contragentname_d" : {
                userPage = userRepo.findPaginatedContragentDesc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            case "contragentname_a" : {
                userPage = userRepo.findPaginatedContragentAsc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            case "username_d" : {
                userPage = userRepo.findPaginatedUserDesc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            default: {
                userPage = userRepo.findPaginatedUserAsc(PageRequest.of(currentPage - 1, currentPageSize));
            }
        }
        model.addAttribute("users", userPage);
        model.addAttribute("contragents", contragentRepo.findAllContragentAsc());
        model.addAttribute("userform", user);

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"add"}, method = RequestMethod.POST)
    public String addUser(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                          @ModelAttribute("userform") Users userform,
                          @RequestParam("page") Optional<Integer> page,
                          @RequestParam("size") Optional<Integer> size,
                          @RequestParam("order") Optional<String> order) {

        String username = userform.getUsername();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String userpassword = passwordEncoder.encode(userform.getUserpassword());
        String description = userform.getDescription();
        boolean locked = userform.isLocked();
        boolean adm = userform.isAdm();
        int contragentid = userform.getContragentid();
        if (contragentid > 0) {
            Contragents contragent = contragentRepo.findById(contragentid);
            try {
                Users user = new Users(username, userpassword, description, locked, adm, contragent);
                userRepo.save(user);
                contragent.getUsersById().add(user);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.user"));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.user") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
        }
        return usersList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/userslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteUser(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("delete") Integer userid,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

        findedUser = null;
        try {
            Users user = userRepo.findById(userid);
            Contragents contragent = user.getContragentsByContragentid();
            userRepo.delete(user);
            contragent.getUsersById().remove(user);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.user"));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.user") +
                    ": " + ex.toString());
        }
        return usersList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/userslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateUser(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("userform") Users userform,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

        int userid = userform.getId();
        String username = userform.getUsername();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String userpassword = passwordEncoder.encode(userform.getUserpassword());
        String description = userform.getDescription();
        boolean locked = userform.isLocked();
        boolean adm = userform.isAdm();
        int contragentid = userform.getContragentid();
        findedUser = null;

        if (contragentid > 0) {
            try {
                Users user = userRepo.findById(userid);
                user.setUsername(username);
                user.setUserpassword(userpassword);
                user.setDescription(description);
                user.setLocked(locked);
                user.setAdm(adm);
                user.setContragentsByContragentid(contragentRepo.findById(contragentid));
                userRepo.save(user);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.user"));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.user") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
        }
        return usersList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/userslist" }, params={"enableuser"}, method = RequestMethod.POST)
    public String enableUser(Model model, Principal principal,
                             @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("enableuser") Integer userid,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (userid> 0) {
            Users managedUser = userRepo.findById(userid);
            if (managedUser == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.user"));
            } else {

                UsersManagement um = new UsersManagement(loginedUser);
                try {
                    um.setEnabledToOS(managedUser.getUsername(), "yes");
                    if (um.isDisabledFromOS(managedUser.getUsername()) == -1) {
                        managedUser.setLocked(false);
                        userRepo.save(managedUser);
                        model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.user"));
                    } else {
                        model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.noenable.user"));
                    }
                } catch (IOException ex) {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.noenable.user") +
                            ": " + ex.toString());
                }

            }
        }

        return usersList(model, tablePageSize, page, size, order);

    }

    @RequestMapping(value = { "/userslist" }, params={"disableuser"}, method = RequestMethod.POST)
    public String disableUser(Model model, Principal principal,
                              @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                              @ModelAttribute("disableuser") Integer userid,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("order") Optional<String> order) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (userid> 0) {
            Users managedUser = userRepo.findById(userid);
            if (managedUser == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.user"));
            } else {

                UsersManagement um = new UsersManagement(loginedUser);
                try {
                    um.setEnabledToOS(managedUser.getUsername(), "no");
                    if (um.isDisabledFromOS(managedUser.getUsername()) == 1) {
                        managedUser.setLocked(true);
                        userRepo.save(managedUser);
                        model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.user"));
                    } else {
                        model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.nodisable.user"));
                    }
                } catch (IOException ex) {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.nodisable.user") +
                            ": " + ex.toString());
                }

            }
        }

        return usersList(model, tablePageSize, page, size, order);

    }

    @RequestMapping(value = { "/userslist" }, params={"refreshusersstatus"}, method = RequestMethod.POST)
    public String refreshUsersStatus(Model model, Principal principal,
                                     @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     @RequestParam("order") Optional<String> order) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        ArrayList<Users> usersArrayList = (ArrayList<Users>)userRepo.findAll();
        UsersManagement um = new UsersManagement(loginedUser);
        for (Users user: usersArrayList) {
            int isDisabled = 0;
            try {
                isDisabled = um.isDisabledFromOS(user.getUsername());
                if (isDisabled == 1) {
                    user.setLocked(true);
                    userRepo.save(user);
                } else {
                    if (isDisabled == -1) {
                        user.setLocked(false);
                        userRepo.save(user);
                    }
                }
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.refreshstatus.user"));
            } catch (IOException ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.norefreshstatus.user") +
                        ": " + ex.toString());
            }

        }
        return usersList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/userslist" }, params={"disconnect"}, method = RequestMethod.POST)
    public String disconnectTS(Model model, Principal principal,
                               @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                               @ModelAttribute("disconnect") Integer userid,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size,
                               @RequestParam("order") Optional<String> order) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (userid> 0) {
            Users managedUser = userRepo.findById(userid);
            if (managedUser == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.user"));
            } else {
                TerminalSessions ts = new TerminalSessions(loginedUser);
                try {
                    ts.termineSession(env.getProperty("tsmserveraddress"), managedUser.getUsername());
                    model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.disconnect"));
                } catch (IOException ex) {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.disconnect") +
                            ": " + ex.toString());
                }

            }
        }
        return usersList(model, tablePageSize, page, size, order);
    }


}