package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.ControllerUtils;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

    private static Integer currentPage = 1;
    private static int pageSize = 5;
    private static String currentOrder = "contragentname_a";

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model, @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size,
                                  @RequestParam("order") Optional<String> order) {

        page.ifPresent(p -> currentPage = p);
        size.ifPresent(s -> pageSize = s);
        order.ifPresent(o -> currentOrder = o);

        Contragents contragent = new Contragents();
        if (findedContragent != null) {
            contragent = findedContragent;
        }
        List<Contragents> contragentPage;
        switch (currentOrder) {
            case "contragentname_d" : {
                contragentPage = contragentRepo.findPaginatedContragentDesc(PageRequest.of(currentPage - 1, pageSize));
                break;
            }
            default: {
                contragentPage = contragentRepo.findPaginatedContragentAsc(PageRequest.of(currentPage - 1, pageSize));
            }
        }

        model.addAttribute("contragents", contragentPage);
        model.addAttribute("contragentform", contragent);

        ControllerUtils.addPageAttributes(model, ((List<Contragents>)contragentRepo.findAll()).size(), currentPage, pageSize);

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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        int contragentid = contragentform.getId();

        try {
            findedContragent = contragentRepo.findById(contragentid);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyinn"}, method = RequestMethod.POST)
    public String findByINNContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        String inn = contragentform.getInn();

        try {
            findedContragent = contragentRepo.findByInn(inn);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize), Optional.of(currentOrder));
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