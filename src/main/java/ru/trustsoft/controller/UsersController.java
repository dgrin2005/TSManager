package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.model.UsersEntity;

@Controller
@RequestMapping(path="/")
public class UsersController {

    private UsersEntity findedUser = null;

    @GetMapping(path="/userslist")
    public String usersList(Model model) {

        UsersEntity user = new UsersEntity();
        if (findedUser != null) {
            user = findedUser;
        }
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("userform", user);

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"add"}, method = RequestMethod.POST)
    public String addUser(Model model, @ModelAttribute("userform") UsersEntity userform) {

        String username = userform.getUsername();
        String userpassword = userform.getUserpassword();
        String description = userform.getDescription();
        boolean isLocked = userform.getIslocked();

        try {
            UsersEntity user = new UsersEntity(username, userpassword, description, isLocked);
            userRepo.save(user);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error creating the user: " + ex.toString());
        }

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteUser(Model model, @ModelAttribute("userform") UsersEntity userform) {

        int userid = userform.getUserid();
        findedUser = null;

        try {
            UsersEntity user = userRepo.findByUserid(userid);
            userRepo.delete(user);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the user:" + ex.toString());
        }

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateUser(Model model, @ModelAttribute("userform") UsersEntity userform) {

        int userid = userform.getUserid();
        String username = userform.getUsername();
        String userpassword = userform.getUserpassword();
        String description = userform.getDescription();
        boolean isLocked = userform.getIslocked();
        findedUser = null;

        try {
            UsersEntity user = userRepo.findByUserid(userid);
            user.setUsername(username);
            user.setUserpassword(userpassword);
            user.setDescription(description);
            user.setIslocked(isLocked);
            userRepo.save(user);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error updating the user:" + ex.toString());
        }

        return "userslist";
    }

    @RequestMapping(value = { "/userslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("userform") UsersEntity userform) {

        int userid = userform.getUserid();

        try {
            findedUser = userRepo.findByUserid(userid);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error finding the user:" + ex.toString());
        }

        return "userslist";
    }

    // Private fields

    @Autowired
    private UsersRepo userRepo;

    private String errorMessage;

}