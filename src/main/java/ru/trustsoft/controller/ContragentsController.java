package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.ContragentssRepo;
import ru.trustsoft.model.ContragentsEntity;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

    private ContragentsEntity findedContragent = null;

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model) {

        ContragentsEntity contragent = new ContragentsEntity();
        if (findedContragent != null) {
            contragent = findedContragent;
        }
        model.addAttribute("contragents", contragentRepo.findAll());
        model.addAttribute("contragentform", contragent);

        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"add"}, method = RequestMethod.POST)
    public String addContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();

        try {
            ContragentsEntity contragent = new ContragentsEntity(contragentname, description);
            contragentRepo.save(contragent);
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error creating the contragent: " + ex.toString());
        }


        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        int contragentid = contragentform.getContragentid();
        findedContragent = null;

        try {
            ContragentsEntity contragent = contragentRepo.findByContragentid(contragentid);
            contragentRepo.delete(contragent);
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the contragent:" + ex.toString());
        }

        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        int contragentid = contragentform.getContragentid();
        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();
        findedContragent = null;

        try {
            ContragentsEntity contragent = contragentRepo.findByContragentid(contragentid);
            contragent.setContragentname(contragentname);
            contragent.setDescription(description);
            contragentRepo.save(contragent);
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error updating the contragent:" + ex.toString());
        }

        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("contragentform") ContragentsEntity contragentform) {

        int contragentid = contragentform.getContragentid();

        try {
            findedContragent = contragentRepo.findByContragentid(contragentid);
            return "redirect:/contragentslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error finding the contragent:" + ex.toString());
        }

        return "contragentslist";
    }

    // Private fields

    @Autowired
    private ContragentssRepo contragentRepo;

    private String errorMessage;

}