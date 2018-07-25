package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.trustsoft.dao.UsersDao;
import ru.trustsoft.model.UsersEntity;

import java.util.List;

@Controller
public class UsersController {

    @RequestMapping("/create-user")
    @ResponseBody
    public String create(String username, String userpassword, String description, boolean islocked) {
        String userId = "";
        try {
            UsersEntity user = new UsersEntity(username, userpassword, description, islocked);
            userDao.save(user);
            userId = String.valueOf(user.getUserid());
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }

    @RequestMapping("/delete-user")
    @ResponseBody
    public String delete(String username) {
        try {
            UsersEntity user = userDao.findByUsername(username);
            userDao.delete(user);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }
        return "User succesfully deleted!";
    }

    @RequestMapping("/get-by-name-user")
    @ResponseBody
    public String getByName(String username) {
        String userId = "";
        try {
            UsersEntity user = userDao.findByUsername(username);
            userId = String.valueOf(user.getUserid());
        }
        catch (Exception ex) {
            return "User not found";
        }
        return "The user id is: " + userId;
    }

    @RequestMapping("/get-by-id-user")
    @ResponseBody
    public String getById(int userid) {
        String userName = "";
        try {
            UsersEntity user = userDao.findByUserid(userid);
            userName = user.toString();
        }
        catch (Exception ex) {
            return "User not found";
        }
        return "The user name is: " + userName;
    }

    @RequestMapping("/get-by-locked-user")
    @ResponseBody
    public String getByLocked(boolean islocked) {
        String userName = "";
        try {
            List<UsersEntity> users = userDao.findByIslocked(islocked);
            userName = users.toString();
        }
        catch (Exception ex) {
            return "Users not found";
        }
        return "The users names are: " + userName;
    }

    @RequestMapping("/update-user")
    @ResponseBody
    public String updateUser(String username, String userpassword, String description, boolean islocked) {
        try {
            UsersEntity user = userDao.findByUsername(username);
            user.setUserpassword(userpassword);
            user.setDescription(description);
            user.setIslocked(islocked);
            userDao.save(user);
        }
        catch (Exception ex) {
            return "Error updating the user: " + ex.toString();
        }
        return "User succesfully updated!";
    }

    // Private fields

    @Autowired
    private UsersDao userDao;

}