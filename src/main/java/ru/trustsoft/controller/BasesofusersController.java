package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Basesofusers;
import ru.trustsoft.model.TablePageSize;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.repo.BasesofusersRepo;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.service.MessageByLocaleService;
import ru.trustsoft.utils.BaseArchive;
import ru.trustsoft.utils.ControllerUtils;
import ru.trustsoft.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path="/")
public class BasesofusersController {

    private static Integer currentPage = 1;
    private static Integer currentPageSize = 5;
    private static String currentOrder = "basename_a";

    @RequestMapping(value = { "/basesofuserslist" }, method = RequestMethod.GET)
    public String basesofusersList(Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size,
                                   @RequestParam("order") Optional<String> order) {

        page.ifPresent(p -> currentPage = p);
        size.ifPresent(s -> currentPageSize = s);
        order.ifPresent(o -> currentOrder = o);

        Basesofusers baseofusers = new Basesofusers();
        if (findedBaseofusers != null) {
            baseofusers = findedBaseofusers;
        }
        model.addAttribute("tablePageSize", tablePageSize);
        model.addAttribute("currentPageSize", currentPageSize);

        model.addAttribute("bases", baseRepo.findAllBaseAsc());
        model.addAttribute("users", userRepo.findAllUserAsc());

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users user = userRepo.findByUsername(loginedUser.getUsername());

        List<Basesofusers> basesofusersPage;

        if (user.isAdm()) {
            currentPage = ControllerUtils.addPageAttributes(model, ((List<Basesofusers>)baseofusersRepo.findAll()).size(), currentPage, currentPageSize);
            switch (currentOrder) {
                case "username_d" : {
                    basesofusersPage = baseofusersRepo.findPaginatedUserDesc(PageRequest.of(currentPage - 1, currentPageSize));
                    break;
                }
                case "username_a" : {
                    basesofusersPage = baseofusersRepo.findPaginatedUserAsc(PageRequest.of(currentPage - 1, currentPageSize));
                    break;
                }
                case "basename_d" : {
                    basesofusersPage = baseofusersRepo.findPaginatedBaseDesc(PageRequest.of(currentPage - 1, currentPageSize));
                    break;
                }
                default: {
                    basesofusersPage = baseofusersRepo.findPaginatedBaseAsc(PageRequest.of(currentPage - 1, currentPageSize));
                }
            }

            model.addAttribute("basesofusers", basesofusersPage);
            model.addAttribute("basesarc", baseRepo.findAllBaseAsc());
            model.addAttribute("isadm", 1);
        } else {
            currentPage = ControllerUtils.addPageAttributes(model, baseofusersRepo.findBasesByUser(user.getId()).size(), currentPage, currentPageSize);
            switch (currentOrder) {
                case "basename_d" : {
                    basesofusersPage = baseofusersRepo.findBasesByUserPaginatedBaseDesc(PageRequest.of(currentPage - 1, currentPageSize), user.getId());
                    break;
                }
                default: {
                    basesofusersPage = baseofusersRepo.findBasesByUserPaginatedBaseAsc(PageRequest.of(currentPage - 1, currentPageSize), user.getId());
                }
            }


            model.addAttribute("basesofusers", basesofusersPage);
            model.addAttribute("basesarc", baseRepo.findAllBaseAscByUser(user.getId()));
            model.addAttribute("isadm", 0);
        }

        model.addAttribute("baseofusersform", baseofusers);
        model.addAttribute("baseofusersarc", baseofusers);

        return "basesofuserslist";
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

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
                return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.record") + ": " + ex.toString());
                return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
            }

        } else {
            if (baseid > 0) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.user"));
            } else {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.base"));
            }
        }
        return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

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
            return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.record") + ": " + ex.toString());
            return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
        }
    }


    @RequestMapping(value = { "/basesofuserslist" }, params={"findbyid"}, method = RequestMethod.POST)
    public String findByIdBase(Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize, @ModelAttribute("baseofusersform") Basesofusers baseofusersform) {

        int baseofusersid = baseofusersform.getId();

        try {
            findedBaseofusers = baseofusersRepo.findById(baseofusersid);
            findedBaseofusers.setBaseid(findedBaseofusers.getBasesByBaseid().getId());
            findedBaseofusers.setUserid(findedBaseofusers.getUsersByUserid().getId());
            if (findedBaseofusers == null) {
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.notfound.record"));
                return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
            } else {
                return "redirect:/basesofuserslist";
            }
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.find.record") + ": " + ex.toString());
            return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
        }
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"archivebase"}, method = RequestMethod.POST)
    public String archiveBase(Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize, @ModelAttribute("baseofuserarc") Basesofusers baseofusersarc) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        //String userInfo = WebUtils.toString(loginedUser);

        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            try {
                int baseid = baseofusersarc.getBaseid();
                if (baseid > 0) {
                    Bases base = baseRepo.findById(baseid);
                    String path_1c_base = base.getPath();
                    String basename = WebUtils.getArcBasename(loginedUser, userRepo, base);
                    BaseArchive ba = new BaseArchive();
                    ba.removeOldArchive(env.getProperty("arc_catalog"), basename);
                    ba.archiveBase(env.getProperty("path_1c"), path_1c_base, env.getProperty("arc_catalog"), basename);
                    model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.archive.base"));
                    return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.base"));
                }
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.archive.record") + ": " + ex.toString());
                return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
            }
        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.archivebasedemo"));
        }
        return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"getarchive"}, method = RequestMethod.POST)
    public String getArchive(HttpServletResponse response, Model model, Principal principal, @ModelAttribute("tablePageSize") TablePageSize tablePageSize, @ModelAttribute("baseofuserarc") Basesofusers baseofusersarc) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            try {
                int baseid = baseofusersarc.getBaseid();
                if (baseid > 0) {
                    Bases base = baseRepo.findById(baseid);
                    String basename = WebUtils.getArcBasename(loginedUser, userRepo, base);
                    if (basename.isEmpty()) {
                        model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
                        return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
                    }
                    System.out.println(basename);
                    BaseArchive ba = new BaseArchive();
                    ba.getArchive(env.getProperty("arc_catalog"), basename, response, this.servletContext);
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.base"));
                    return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
                }
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getarchive") + ": " + ex.toString());
                return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
            }
        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.archivebasedemo"));
            return basesofusersList(model, principal, tablePageSize, Optional.of(currentPage), Optional.of(currentPageSize), Optional.of(currentOrder));
        }
        return "basesofuserslist";
    }

    // Private fields

    @Autowired
    private BasesofusersRepo baseofusersRepo;

    @Autowired
    private BasesRepo baseRepo;

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private Basesofusers findedBaseofusers;

    @Autowired
    private Environment env;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    MessageByLocaleService messageByLocaleService;

}