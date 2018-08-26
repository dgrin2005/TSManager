/**
 * TerminalServerManager
 *    ContragentsController.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.0 dated 2018-08-23
 */

package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.TablePageSize;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.ControllerUtils;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path="/")
public class ContragentsController {

    private static Integer defaultPage = 1;
    private static Integer defaultPageSize = 5;
    private static String defaultOrder = "contragentname_a";

    @Autowired
    private Contragents findedContragent;

    @Autowired
    private ContragentsRepo contragentRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

    @GetMapping(path="/contragentslist")
    public String contragentsList(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size,
                                  @RequestParam("order") Optional<String> order) {

        Integer currentPage;
        Integer currentPageSize;
        String currentOrder;

        if (page.isPresent()) {
            currentPage = page.get() > 0 ? page.get() : defaultPage;
        } else {
            currentPage = defaultPage;
        }

        if (size.isPresent()) {
            currentPageSize = size.get() > 0 ? size.get() : defaultPageSize;
        } else {
            currentPageSize = defaultPageSize;
        }

        if (order.isPresent()) {
            currentOrder = order.get();
        } else {
            currentOrder = defaultOrder;
        }

        if (tablePageSize.getSize() == null) {
            tablePageSize.setSize(currentPageSize);
        }

        Contragents contragent = new Contragents();
        if (findedContragent != null) {
            contragent = findedContragent;
        }
        model.addAttribute("tablePageSize", tablePageSize);
        model.addAttribute("currentPageSize", currentPageSize);
        currentPage = ControllerUtils.addPageAttributes(model, ((List<Contragents>)contragentRepo.findAll()).size(),
                currentPage, currentPageSize);
        List<Contragents> contragentPage;
        switch (currentOrder) {
            case "contragentname_d" : {
                contragentPage = contragentRepo.findPaginatedContragentDesc(PageRequest.of(currentPage - 1,
                        currentPageSize));
                break;
            }
            default: {
                contragentPage = contragentRepo.findPaginatedContragentAsc(PageRequest.of(currentPage - 1,
                        currentPageSize));
            }
        }

        model.addAttribute("contragents", contragentPage);
        model.addAttribute("contragentform", contragent);
        return "contragentslist";
    }

    @RequestMapping(value = { "/contragentslist" }, params={"add"}, method = RequestMethod.POST)
    public String addContragent(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                @ModelAttribute("contragentform") Contragents contragentform,
                                @RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size,
                                @RequestParam("order") Optional<String> order) {

        String contragentname = contragentform.getContragentname();
        String description = contragentform.getDescription();
        String inn = contragentform.getInn();

        try {
            Contragents contragent = new Contragents(contragentname, description, inn);
            contragentRepo.save(contragent);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.add.contragent"));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.contragent") +
                    ": " + ex.toString());
        }
        return contragentsList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/contragentslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteContragent(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                   @ModelAttribute("delete") Integer contragentid,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size,
                                   @RequestParam("order") Optional<String> order) {

        findedContragent = null;
        try {
            Contragents contragent = contragentRepo.findById(contragentid);
            contragentRepo.delete(contragent);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.contragent"));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.contragent") +
                    ": " + ex.toString());
        }
        return contragentsList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/contragentslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateContragent(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                   @ModelAttribute("contragentform") Contragents contragentform,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size,
                                   @RequestParam("order") Optional<String> order) {

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
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.contragent") +
                    ": " + ex.toString());
        }
        return contragentsList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdContragent(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                     @ModelAttribute("contragentform") Contragents contragentform,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     @RequestParam("order") Optional<String> order) {

        int contragentid = contragentform.getId();
        try {
            findedContragent = contragentRepo.findById(contragentid);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") +
                    ": " + ex.toString());
        }
        return contragentsList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/contragentslist" }, params={"findbyinn"}, method = RequestMethod.POST)
    public String findByINNContragent(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                      @ModelAttribute("contragentform") Contragents contragentform,
                                      @RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size,
                                      @RequestParam("order") Optional<String> order) {

        String inn = contragentform.getInn();
        try {
            findedContragent = contragentRepo.findByInn(inn);
            if (findedContragent == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.contragent"));
                return contragentsList(model, tablePageSize, page, size, order);
            } else {
                return "redirect:/contragentslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.contragent") +
                    ": " + ex.toString());
            return contragentsList(model, tablePageSize, page, size, order);
        }
    }

}