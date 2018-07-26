package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.model.UsersEntity;

import java.util.List;

@Controller
@RequestMapping(path="/")
public class UsersController {

/*
    @GetMapping("/create-user")
    @ResponseBody
    public String create(String username, String userpassword, String description, boolean islocked) {
        String userId = "";
        try {
            UsersEntity user = new UsersEntity(username, userpassword, description, islocked);
            userRepo.save(user);
            userId = String.valueOf(user.getUserid());
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }

    @GetMapping("/delete-user")
    @ResponseBody
    public String delete(String username) {
        try {
            UsersEntity user = userRepo.findByUsername(username);
            userRepo.delete(user);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }
        return "User succesfully deleted!";
    }

    @GetMapping("/get-by-name-user")
    @ResponseBody
    public String getByName(String username) {
        String userId = "";
        try {
            UsersEntity user = userRepo.findByUsername(username);
            userId = String.valueOf(user.getUserid());
        }
        catch (Exception ex) {
            return "User not found";
        }
        return "The user id is: " + userId;
    }

    @GetMapping("/get-by-id-user")
    @ResponseBody
    public String getById(int userid) {
        String userName = "";
        try {
            UsersEntity user = userRepo.findByUserid(userid);
            userName = user.toString();
        }
        catch (Exception ex) {
            return "User not found";
        }
        return "The user name is: " + userName;
    }

    @GetMapping("/get-by-locked-user")
    @ResponseBody
    public String getByLocked(boolean islocked) {
        String userName = "";
        try {
            List<UsersEntity> users = userRepo.findByIslocked(islocked);
            userName = users.toString();
        }
        catch (Exception ex) {
            return "Users not found";
        }
        return "The users names are: " + userName;
    }

    @GetMapping("/update-user")
    @ResponseBody
    public String updateUser(String username, String userpassword, String description, boolean islocked) {
        try {
            UsersEntity user = userRepo.findByUsername(username);
            user.setUserpassword(userpassword);
            user.setDescription(description);
            user.setIslocked(islocked);
            userRepo.save(user);
        }
        catch (Exception ex) {
            return "Error updating the user: " + ex.toString();
        }
        return "User succesfully updated!";
    }

    @GetMapping(path="/all-user")
    @ResponseBody
    public Iterable<UsersEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepo.findAll();
    }
*/

    @GetMapping(path="/userslist")
    public String usersList(Model model) {

        model.addAttribute("users", userRepo.findAll());

        return "userslist";
    }

    @GetMapping(path="/adduser")
    public String showAddUserPage(Model model) {

        UsersEntity user = new UsersEntity();
        model.addAttribute("userform", user);

        return "adduser";
    }

    @RequestMapping(value = { "/adduser" }, method = RequestMethod.POST)
    public String saveUser(Model model, @ModelAttribute("userform") UsersEntity userform) {

        String username = userform.getUsername();
        String userpassword = userform.getUserpassword();
        String description = userform.getDescription();
        Boolean islocked = userform.getIslocked();

        String userId = "";
        try {
            UsersEntity user = new UsersEntity(username, userpassword, description, islocked);
            userRepo.save(user);
            userId = String.valueOf(user.getUserid());
            //return "User succesfully created with id = " + userId;
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            //return "Error creating the user: " + ex.toString();
            model.addAttribute("errorMessage", "Error creating the user: " + ex.toString());
        }

        //model.addAttribute("errorMessage", errorMessage);

        return "adduser";
    }

    @GetMapping(path="/removeuser")
    public String showRemoveUserPage(Model model) {

        UsersEntity user = new UsersEntity();
        model.addAttribute("userform", user);

        return "removeuser";
    }

    @RequestMapping(value = { "/removeuser" }, method = RequestMethod.POST)
    public String removeUser(Model model, @ModelAttribute("userform") UsersEntity userform) {

        String username = userform.getUsername();

        try {
            UsersEntity user = userRepo.findByUsername(username);
            userRepo.delete(user);
            return "redirect:/userslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the user:" + ex.toString());
        }

        return "removeuser";
    }

    // Private fields

    @Autowired
    private UsersRepo userRepo;

    @Value("${error.message}")
    private String errorMessage;

}