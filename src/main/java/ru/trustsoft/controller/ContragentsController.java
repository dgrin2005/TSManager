package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model) {

        Contragents contragent = new Contragents();
        if (findedContragent != null) {
            contragent = findedContragent;
        }
        model.addAttribute("contragents", contragentRepo.findAll());
        model.addAttribute("contragentform", contragent);

        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"add"}, method = RequestMethod.POST)
    public String addContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();
        String inn = contragentform.getInn();

        try {
            Contragents contragent = new Contragents(contragentname, description, inn);
            contragentRepo.save(contragent);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.contragent"));
            return contragentsList(model);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.contragent") + ": " + ex.toString());
            return contragentsList(model);
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        int contragentid = contragentform.getId();
        findedContragent = null;

        try {
            Contragents contragent = contragentRepo.findById(contragentid);
            contragentRepo.delete(contragent);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.contragent"));
            return contragentsList(model);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.contragent") + ": " + ex.toString());
            return contragentsList(model);
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        int contragentid = contragentform.getId();
        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();
        String inn = contragentform.getInn();
        findedContragent = null;

        try {
            Contragents contragent = contragentRepo.findById(contragentid);
            contragent.setContragentname(contragentname);
            contragent.setDescription(description);
            contragent.setInn(inn);
            contragentRepo.save(contragent);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.contragent"));
            return contragentsList(model);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.contragent") + ": " + ex.toString());
            return contragentsList(model);
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        int contragentid = contragentform.getId();

        try {
            findedContragent = contragentRepo.findById(contragentid);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model);
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model);
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyinn"}, method = RequestMethod.POST)
    public String findByINNContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        String inn = contragentform.getInn();

        try {
            findedContragent = contragentRepo.findByInn(inn);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model);
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model);
        }
    }

    // Private fields

    @Autowired
    private Contragents findedContragent;

    @Autowired
    private ContragentsRepo contragentRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

}