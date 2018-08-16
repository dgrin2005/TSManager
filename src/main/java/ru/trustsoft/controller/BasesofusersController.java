package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Basesofusers;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.BasesofusersRepo;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.service.MessageByLocaleService;

import java.security.Principal;

@Controller
@RequestMapping(path="/")
public class BasesofusersController {

    private Basesofusers findedBaseofusers = null;

    @RequestMapping(value = { "/basesofuserslist" }, method = RequestMethod.GET)
    public String basesofusersList(Model model, Principal principal) {

        Basesofusers baseofusers = new Basesofusers();
        if (findedBaseofusers != null) {
            baseofusers = findedBaseofusers;
        }

        model.addAttribute("bases", baseRepo.findAll());
        model.addAttribute("users", userRepo.findAll());

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users user = userRepo.findByUsername(loginedUser.getUsername());
        if (user.isAdm()) {
            model.addAttribute("basesofusers", baseofusersRepo.findAll());
            model.addAttribute("isadm", 1);
        } else {
            model.addAttribute("basesofusers", baseofusersRepo.findBasesByUser(user.getId()));
            model.addAttribute("isadm", 0);
        }

        model.addAttribute("baseofusersform", baseofusers);


        return "basesofuserslist";
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, Principal principal, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

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
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.record"));
                return basesofusersList(model, principal);
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.record") + ": " + ex.toString());
                return basesofusersList(model, principal);
            }

        } else {
            if (baseid > 0) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.user"));
            } else {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.base"));
            }
        }
        return basesofusersList(model, principal);
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, Principal principal, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int baseofusersid = baseofusersform.getId();
        findedBaseofusers = null;

        try {
            Basesofusers baseofusers = baseofusersRepo.findById(baseofusersid);
            Users user = baseofusers.getUsersByUserid();
            Bases base = baseofusers.getBasesByBaseid();
            baseofusersRepo.delete(baseofusers);
            user.getBasesofusersById().remove(baseofusers);
            base.getBasesofusersById().remove(baseofusers);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.record"));
            return basesofusersList(model, principal);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.record") + ": " + ex.toString());
            return basesofusersList(model, principal);
        }
    }


    @RequestMapping(value = { "/basesofuserslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, Principal principal, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int baseofusersid = baseofusersform.getId();

        try {
            findedBaseofusers = baseofusersRepo.findById(baseofusersid);
            findedBaseofusers.setBaseid(findedBaseofusers.getBasesByBaseid().getId());
            findedBaseofusers.setUserid(findedBaseofusers.getUsersByUserid().getId());
            if (findedBaseofusers == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.record"));
                return basesofusersList(model, principal);
            } else {
                return "redirect:/basesofuserslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.record") + ": " + ex.toString());
            return basesofusersList(model, principal);
        }
    }

    // Private fields

    @Autowired
    private BasesofusersRepo baseofusersRepo;

    @Autowired
    private BasesRepo baseRepo;

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

}