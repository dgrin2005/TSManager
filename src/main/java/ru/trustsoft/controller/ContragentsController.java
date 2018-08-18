package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

    private static int currentPage = 1;
    private static int pageSize = 5;

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

        page.ifPresent(p -> currentPage = p);
        size.ifPresent(s -> pageSize = s);

        Contragents contragent = new Contragents();
        if (findedContragent != null) {
            contragent = findedContragent;
        }
        //model.addAttribute("contragents", contragentRepo.findAll());
        List<Contragents> contragentPage = contragentRepo.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("contragents", contragentPage);
        model.addAttribute("contragentform", contragent);

        int totalPages = (((List<Contragents>)contragentRepo.findAll()).size() - 1) / pageSize + 1;
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pageSize", pageSize);
        }

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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
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
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        int contragentid = contragentform.getId();

        try {
            findedContragent = contragentRepo.findById(contragentid);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyinn"}, method = RequestMethod.POST)
    public String findByINNContragent(Model model, @ModelAttribute("contragentform") Contragents contragentform) {

        String inn = contragentform.getInn();

        try {
            findedContragent = contragentRepo.findByInn(inn);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") + ": " + ex.toString());
            return contragentsList(model, Optional.of(currentPage), Optional.of(pageSize));
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