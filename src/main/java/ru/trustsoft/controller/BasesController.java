package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;

@Controller
@RequestMapping(path="/")
public class BasesController {

    @GetMapping(path="/baseslist")
    public String basesList(Model model) {

        Bases base = new Bases();
        if (findedBase != null) {
            base = findedBase;
        }

        model.addAttribute("bases", baseRepo.findAll());
        model.addAttribute("contragents", contragentRepo.findAll());
        model.addAttribute("baseform", base);

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        String basename = baseform.getBasename();
        String description = baseform.getDescription();
        int contragentid = baseform.getContragentid();
        if (contragentid > 0) {
            Contragents contragent = contragentRepo.findById(contragentid);
            try {
                Bases base = new Bases(basename, description, contragent);
                baseRepo.save(base);
                contragent.getBasesById().add(base);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.base"));
                return basesList(model);
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.base") + ": " + ex.toString());
                return basesList(model);
            }

        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return basesList(model);
        }
    }

    @RequestMapping(value = { "/baseslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        int baseid = baseform.getId();
        findedBase = null;

        try {
            Bases base = baseRepo.findById(baseid);
            Contragents contragent = base.getContragentsByContragentid();
            baseRepo.delete(base);
            contragent.getBasesById().remove(base);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.base"));
            return basesList(model);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.base") + ": " + ex.toString());
            return basesList(model);
        }
    }

    @RequestMapping(value = { "/baseslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        int baseid = baseform.getId();
        String basename = baseform.getBasename();
        String description = baseform.getDescription();
        int contragentid = baseform.getContragentid();
        findedBase = null;
        if (contragentid > 0) {
            try {
                Bases base = baseRepo.findById(baseid);
                base.setBasename(basename);
                base.setDescription(description);
                base.setContragentsByContragentid(contragentRepo.findById(contragentid));
                baseRepo.save(base);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.base"));
                return basesList(model);
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.base") + ": " + ex.toString());
                return basesList(model);
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return basesList(model);
        }
    }

    @RequestMapping(value = { "/baseslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        int baseid = baseform.getId();

        try {
            findedBase = baseRepo.findById(baseid);
            findedBase.setContragentid(findedBase.getContragentsByContragentid().getId());
            if (findedBase == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.base"));
                return basesList(model);
            } else {
                return "redirect:/baseslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.base") + ": " + ex.toString());
            return basesList(model);
        }
    }

    // Private fields

    @Autowired
    private Bases findedBase;

    @Autowired
    private BasesRepo baseRepo;

    @Autowired
    private ContragentsRepo contragentRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

}