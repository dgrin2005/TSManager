/**
 * TerminalServerManager
 *    BasesController.java
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
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.TablePageSize;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.ContragentsRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.ControllerUtils;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path="/")
public class BasesController {

    private static Integer defaultPage = 1;
    private static Integer defaultPageSize = 5;
    private static String defaultOrder = "basename_a";

    @Autowired
    private Bases findedBase;

    @Autowired
    private BasesRepo baseRepo;

    @Autowired
    private ContragentsRepo contragentRepo;

    @Autowired
    MessageByLocaleService messageByLocaleService;

    @GetMapping(path="/baseslist")
    public String basesList(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
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

        Bases base = new Bases();
        if (findedBase != null) {
            base = findedBase;
        }
        model.addAttribute("tablePageSize", tablePageSize);
        model.addAttribute("currentPageSize", currentPageSize);
        currentPage = ControllerUtils.addPageAttributes(model, ((List<Bases>)baseRepo.findAll()).size(),
                currentPage, currentPageSize);
        List<Bases> basePage;

        switch (currentOrder) {
            case "contragentname_d" : {
                basePage = baseRepo.findPaginatedContragentDesc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            case "contragentname_a" : {
                basePage = baseRepo.findPaginatedContragentAsc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            case "basename_d" : {
                basePage = baseRepo.findPaginatedBaseDesc(PageRequest.of(currentPage - 1, currentPageSize));
                break;
            }
            default: {
                basePage = baseRepo.findPaginatedBaseAsc(PageRequest.of(currentPage - 1, currentPageSize));
            }
        }
        model.addAttribute("bases", basePage);
        model.addAttribute("contragents", contragentRepo.findAllContragentAsc());
        model.addAttribute("baseform", base);

        return "baseslist";
    }

    @RequestMapping(value = { "/baseslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                          @ModelAttribute("baseform") Bases baseform,
                          @RequestParam("page") Optional<Integer> page,
                          @RequestParam("size") Optional<Integer> size,
                          @RequestParam("order") Optional<String> order) {

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
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.base") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
        }
        return basesList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/baseslist" }, params={"update"}, method = RequestMethod.POST)
    public String updateBase(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("baseform") Bases baseform,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

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
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.update.base") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
        }
        return basesList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/baseslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                               @ModelAttribute("baseform") Bases baseform,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size,
                               @RequestParam("order") Optional<String> order) {

        int baseid = baseform.getId();
        try {
            findedBase = baseRepo.findById(baseid);
            findedBase.setContragentid(findedBase.getContragentsByContragentid().getId());
            if (findedBase == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.base"));
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.base") +
                    ": " + ex.toString());
        }
        return basesList(model, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/baseslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("delete") Integer baseid,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

        findedBase = null;
        try {
            Bases base = baseRepo.findById(baseid);
            Contragents contragent = base.getContragentsByContragentid();
            baseRepo.delete(base);
            contragent.getBasesById().remove(base);
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.base"));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.base") +
                    ": " + ex.toString());
        }
        return basesList(model, tablePageSize, page, size, order);
    }

}