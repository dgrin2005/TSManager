package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.trustsoft.dao.ContragentssDao;
import ru.trustsoft.model.ContragentsEntity;

@Controller
public class ContragentsController {

    @RequestMapping("/create-contragent")
    @ResponseBody
    public String create(String contragentname, String description) {
        String contragentId = "";
        try {
            ContragentsEntity contragent = new ContragentsEntity(contragentname, description);
            contragentDao.save(contragent);
            contragentId = String.valueOf(contragent.getContragentid());
        }
        catch (Exception ex) {
            return "Error creating the contragent: " + ex.toString();
        }
        return "Contragent succesfully created with id = " + contragentId;
    }

    @RequestMapping("/delete-contragent")
    @ResponseBody
    public String delete(String contragentname) {
        try {
            ContragentsEntity contragent = contragentDao.findByContragentname(contragentname);
            contragentDao.delete(contragent);
        }
        catch (Exception ex) {
            return "Error deleting the contragent:" + ex.toString();
        }
        return "Contragent succesfully deleted!";
    }

    @RequestMapping("/get-by-name-contragent")
    @ResponseBody
    public String getByName(String contragentname) {
        String contragentId = "";
        try {
            ContragentsEntity contragent = contragentDao.findByContragentname(contragentname);
            contragentId = String.valueOf(contragent.getContragentid());
        }
        catch (Exception ex) {
            return "Contragent not found";
        }
        return "The contragent id is: " + contragentId;
    }

    @RequestMapping("/get-by-id-contragent")
    @ResponseBody
    public String getById(int contragentid) {
        String contragentName = "";
        try {
            ContragentsEntity contragent = contragentDao.findByContragentid(contragentid);
            contragentName = contragent.toString();
        }
        catch (Exception ex) {
            return "Contragent not found";
        }
        return "The contragent name is: " + contragentName;
    }

    @RequestMapping("/update-contragent")
    @ResponseBody
    public String updateContragent(String contragentname, String description) {
        try {
            ContragentsEntity contragent = contragentDao.findByContragentname(contragentname);
            contragent.setDescription(description);
            contragentDao.save(contragent);
        }
        catch (Exception ex) {
            return "Error updating the contragent: " + ex.toString();
        }
        return "Contragent succesfully updated!";
    }

    // Private fields

    @Autowired
    private ContragentssDao contragentDao;

}