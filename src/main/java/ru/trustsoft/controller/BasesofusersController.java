package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.model.BasesEntity;

@Controller
@RequestMapping(path="/")
public class BasesController {

    private BasesEntity findedBase = null;

    @GetMapping(path="/baseslist")
    public String basesList(Model model) {

        BasesEntity base = new BasesEntity();
        if (findedBase != null) {
            base = findedBase;
        }
        model.addAttribute("bases", baseRepo.findAll());
        model.addAttribute("baseform", base);

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        String basename = baseform.getBasename();
        String description = baseform.getDescription();

        try {
            BasesEntity base = new BasesEntity(basename, description);
            baseRepo.save(base);
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error creating the base: " + ex.toString());
        }

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        int baseid = baseform.getBaseid();
        findedBase = null;

        try {
            BasesEntity base = baseRepo.findByBaseid(baseid);
            baseRepo.delete(base);
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the base:" + ex.toString());
        }

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        int baseid = baseform.getBaseid();
        String basename = baseform.getBasename();
        String description = baseform.getDescription();
        findedBase = null;

        try {
            BasesEntity base = baseRepo.findByBaseid(baseid);
            base.setBasename(basename);
            base.setDescription(description);
            baseRepo.save(base);
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error updating the base:" + ex.toString());
        }

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        int baseid = baseform.getBaseid();

        try {
            findedBase = baseRepo.findByBaseid(baseid);
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error finding the base:" + ex.toString());
        }

        return "baseslist";
    }

    // Private fields

    @Autowired
    private BasesRepo baseRepo;

    private String errorMessage;

}