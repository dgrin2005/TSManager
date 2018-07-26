package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.ContragentssRepo;
import ru.trustsoft.model.ContragentsEntity;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

/*
    @GetMapping("/create-contragent")
    @ResponseBody
    public String create(String contragentname, String description) {
        String contragentId = "";
        try {
            ContragentsEntity contragent = new ContragentsEntity(contragentname, description);
            contragentRepo.save(contragent);
            contragentId = String.valueOf(contragent.getContragentid());
        }
        catch (Exception ex) {
            return "Error creating the contragent: " + ex.toString();
        }
        return "Contragent succesfully created with id = " + contragentId;
    }

    @GetMapping("/delete-contragent")
    @ResponseBody
    public String delete(String contragentname) {
        try {
            ContragentsEntity contragent = contragentRepo.findByContragentname(contragentname);
            contragentRepo.delete(contragent);
        }
        catch (Exception ex) {
            return "Error deleting the contragent:" + ex.toString();
        }
        return "Contragent succesfully deleted!";
    }

    @GetMapping("/get-by-name-contragent")
    @ResponseBody
    public String getByName(String contragentname) {
        String contragentId = "";
        try {
            ContragentsEntity contragent = contragentRepo.findByContragentname(contragentname);
            contragentId = String.valueOf(contragent.getContragentid());
        }
        catch (Exception ex) {
            return "Contragent not found";
        }
        return "The contragent id is: " + contragentId;
    }

    @GetMapping("/get-by-id-contragent")
    @ResponseBody
    public String getById(int contragentid) {
        String contragentName = "";
        try {
            ContragentsEntity contragent = contragentRepo.findByContragentid(contragentid);
            contragentName = contragent.toString();
        }
        catch (Exception ex) {
            return "Contragent not found";
        }
        return "The contragent name is: " + contragentName;
    }

    @GetMapping("/update-contragent")
    @ResponseBody
    public String updateContragent(String contragentname, String description) {
        try {
            ContragentsEntity contragent = contragentRepo.findByContragentname(contragentname);
            contragent.setDescription(description);
            contragentRepo.save(contragent);
        }
        catch (Exception ex) {
            return "Error updating the contragent: " + ex.toString();
        }
        return "Contragent succesfully updated!";
    }

    @GetMapping(path="/all-contragent")
    @ResponseBody
    public Iterable<ContragentsEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return contragentRepo.findAll();
    }
*/

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model) {

        model.addAttribute("contragents", contragentRepo.findAll());

        return "contragentslist";
    }

    @GetMapping(path="/addcontragent")
    public String showAddContragentPage(Model model) {

        ContragentsEntity contragent = new ContragentsEntity();
        model.addAttribute("contragentform", contragent);

        return "addcontragent";
    }

    @RequestMapping(value = { "/addcontragent" }, method = RequestMethod.POST)
    public String saveContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();

        String contragentId = "";
        try {
            ContragentsEntity contragent = new ContragentsEntity(contragentname, description);
            contragentRepo.save(contragent);
            contragentId = String.valueOf(contragent.getContragentid());
            //return "Contragent succesfully created with id = " + contragentId;
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            //return "Error creating the contragent: " + ex.toString();
            model.addAttribute("errorMessage", "Error creating the contragent: " + ex.toString());
        }

        //model.addAttribute("errorMessage", errorMessage);

        return "addcontragent";
    }

    @GetMapping(path="/removecontragent")
    public String showRemoveContragentPage(Model model) {

        ContragentsEntity contragent = new ContragentsEntity();
        model.addAttribute("contragentform", contragent);

        return "removecontragent";
    }

    @RequestMapping(value = { "/removecontragent" }, method = RequestMethod.POST)
    public String removeContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        String contragentname = contragentform.getContragentname();

        try {
            ContragentsEntity contragent = contragentRepo.findByContragentname(contragentname);
            contragentRepo.delete(contragent);
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the contragent:" + ex.toString());
        }

        return "removecontragent";
    }


    // Private fields

    @Autowired
    private ContragentssRepo contragentRepo;

    @Value("${error.message}")
    private String errorMessage;

}