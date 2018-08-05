package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Basesofusers;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.BasesofusersRepo;
import ru.trustsoft.repo.UsersRepo;

@Controller
@RequestMapping(path="/")
public class BasesofusersController {

    private Basesofusers findedBaseofusers = null;

    @GetMapping(path="/basesofuserslist")
    public String basesofusersList(Model model) {

        Basesofusers baseofusers = new Basesofusers();
        if (findedBaseofusers != null) {
            baseofusers = findedBaseofusers;
        }

        model.addAttribute("bases", baseRepo.findAll());
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("basesofusers", baseofusersRepo.findAll());
        model.addAttribute("baseofusersform", baseofusers);

        return "basesofuserslist";
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int userid = baseofusersform.getUserid();
        int baseid = baseofusersform.getBaseid();
        if (userid > 0 && baseid > 0) {
            Users user = userRepo.findById(userid);
            Bases base = baseRepo.findById(baseid);
            try {
                Basesofusers baseofusers = new Basesofusers(user, base);
                baseofusersRepo.save(baseofusers);
                user.getBasesofusersById().add(baseofusers);
                base.getBasesofusersById().add(baseofusers);
                return "redirect:/basesofuserslist";
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "Error creating the record: " + ex.toString());
                return basesofusersList(model);
            }

        } else {
            return "redirect:/basesofuserslist";
        }
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int baseofusersid = baseofusersform.getId();
        findedBaseofusers = null;

        try {
            Basesofusers baseofusers = baseofusersRepo.findById(baseofusersid);
            Users user = baseofusers.getUsersByUserid();
            Bases base = baseofusers.getBasesByBaseid();
            baseofusersRepo.delete(baseofusers);
            user.getBasesofusersById().remove(baseofusers);
            base.getBasesofusersById().remove(baseofusers);
            return "redirect:/basesofuserslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the record:" + ex.toString());
            return basesofusersList(model);
        }
    }


    @RequestMapping(value = { "/basesofuserslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int baseofusersid = baseofusersform.getId();

        try {
            findedBaseofusers = baseofusersRepo.findById(baseofusersid);
            findedBaseofusers.setBaseid(findedBaseofusers.getBasesByBaseid().getId());
            findedBaseofusers.setUserid(findedBaseofusers.getUsersByUserid().getId());
            return "redirect:/basesofuserslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error finding the record:" + ex.toString());
            return basesofusersList(model);
        }
    }

    // Private fields

    @Autowired
    private BasesofusersRepo baseofusersRepo;

    @Autowired
    private BasesRepo baseRepo;

    @Autowired
    private UsersRepo userRepo;

    private String errorMessage;

}