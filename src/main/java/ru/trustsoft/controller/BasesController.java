package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(path="/")
public class BasesController {

    private static int currentPage = 1;
    private static int pageSize = 5;

    @GetMapping(path="/baseslist")
    public String basesList(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

        page.ifPresent(p -> currentPage = p);
        size.ifPresent(s -> pageSize = s);

        Bases base = new Bases();
        if (findedBase != null) {
            base = findedBase;
        }

        //model.addAttribute("bases", baseRepo.findAll());
        List<Bases> basePage = baseRepo.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("bases", basePage);
        model.addAttribute("contragents", contragentRepo.findAll());
        model.addAttribute("baseform", base);

        int totalPages = (((List<Bases>)baseRepo.findAll()).size() - 1) / pageSize + 1;
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

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        String basename = baseform.getBasename();
        String description = baseform.getDescription();
        String path = baseform.getPath();
        String ipaddress = baseform.getIpaddress();
        int contragentid = baseform.getContragentid();
        if (contragentid > 0) {
            Contragents contragent = contragentRepo.findById(contragentid);
            try {
                Bases base = new Bases(basename, description, contragent, path, ipaddress);
                baseRepo.save(base);
                contragent.getBasesById().add(base);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.base"));
                return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.base") + ": " + ex.toString());
                return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
            }

        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
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
            return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.base") + ": " + ex.toString());
            return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
        }
    }

    @RequestMapping(value = { "/baseslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateBase(Model model, @ModelAttribute("baseform") Bases baseform) {

        int baseid = baseform.getId();
        String basename = baseform.getBasename();
        String description = baseform.getDescription();
        String path = baseform.getPath();
        String ipaddress = baseform.getIpaddress();
        int contragentid = baseform.getContragentid();
        findedBase = null;
        if (contragentid > 0) {
            try {
                Bases base = baseRepo.findById(baseid);
                base.setBasename(basename);
                base.setDescription(description);
                base.setPath(path);
                base.setIpaddress(ipaddress);
                base.setContragentsByContragentid(contragentRepo.findById(contragentid));
                baseRepo.save(base);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.update.base"));
                return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.base") + ": " + ex.toString());
                return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
            return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
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
                return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
            } else {
                return "redirect:/baseslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.base") + ": " + ex.toString());
            return basesList(model, Optional.of(currentPage), Optional.of(pageSize));
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