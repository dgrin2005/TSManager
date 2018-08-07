package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.repo.UsersRepo;

@Controller
@RequestMapping(path="/")
public class UsersController {

    private Users findedUser = null;

    @GetMapping(path="/userslist")
    public String usersList(Model model) {

        Users user = new Users();
        if (findedUser != null) {
            user = findedUser;
        }
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("contragents", contragentRepo.findAll());
        model.addAttribute("userform", user);

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"add"}, method = RequestMethod.POST)
    public String addUser(Model model, @ModelAttribute("userform") Users userform) {

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
                return "redirect:/userslist";
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "Error creating the user: " + ex.toString());
                return usersList(model);
            }
        } else {
                return "redirect:/userslist";
        }

    }

    @RequestMapping(value = { "/userslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteUser(Model model, @ModelAttribute("userform") Users userform) {

        int userid = userform.getId();
        findedUser = null;

        try {
            Users user = userRepo.findById(userid);
            Contragents contragent = user.getContragentsByContragentid();
            userRepo.delete(user);
            contragent.getUsersById().remove(user);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the user:" + ex.toString());
            return usersList(model);
        }
    }

    @RequestMapping(value = { "/userslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateUser(Model model, @ModelAttribute("userform") Users userform) {

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
                return "redirect:/userslist";
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "Error updating the user:" + ex.toString());
                return usersList(model);
            }
        } else {
            return "redirect:/userslist";
        }
    }

    @RequestMapping(value = { "/userslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("userform") Users userform) {

        int userid = userform.getId();

        try {
            findedUser = userRepo.findById(userid);
            findedUser.setContragentid(findedUser.getContragentsByContragentid().getId());
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error finding the user:" + ex.toString());
            return usersList(model);
        }
    }

    // Private fields

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private ContragentsRepo contragentRepo;

    private String errorMessage;

}