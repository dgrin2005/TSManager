/**
 * TerminalServerManager
 *    BasesofusersController.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.0 dated 2018-08-23
 */

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

    private static Integer defaultPage = 1;
    private static Integer defaultPageSize = 5;
    private static String defaultOrder = "basename_a";

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

    @RequestMapping(value = { "/basesofuserslist" }, method = RequestMethod.GET)
    public String basesofusersList(Model model, Principal principal,
                                   @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
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

        Basesofusers baseofusers = new Basesofusers();
        if (findedBaseofusers != null) {
            baseofusers = findedBaseofusers;
        }
        model.addAttribute("tablePageSize", tablePageSize);
        model.addAttribute("currentPageSize", currentPageSize);



        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users user = userRepo.findByUsername(loginedUser.getUsername());

        List<Basesofusers> basesofusersPage;

        if (user.isAdm()) {
            model.addAttribute("bases", baseRepo.findAllBaseAsc());
            model.addAttribute("users", userRepo.findAllUserAsc());

            currentPage = ControllerUtils.addPageAttributes(model, ((List<Basesofusers>)baseofusersRepo.findAll()).size(),
                    currentPage, currentPageSize);
            switch (currentOrder) {
                case "username_d" : {
                    basesofusersPage = baseofusersRepo.findPaginatedUserDesc(PageRequest.of(currentPage - 1,
                            currentPageSize));
                    break;
                }
                case "username_a" : {
                    basesofusersPage = baseofusersRepo.findPaginatedUserAsc(PageRequest.of(currentPage - 1,
                            currentPageSize));
                    break;
                }
                case "basename_d" : {
                    basesofusersPage = baseofusersRepo.findPaginatedBaseDesc(PageRequest.of(currentPage - 1,
                            currentPageSize));
                    break;
                }
                default: {
                    basesofusersPage = baseofusersRepo.findPaginatedBaseAsc(PageRequest.of(currentPage - 1,
                            currentPageSize));
                }
            }

            model.addAttribute("basesofusers", basesofusersPage);
            //model.addAttribute("basesarc", baseRepo.findAllBaseAsc());
            model.addAttribute("isadm", 1);
        } else {
            model.addAttribute("bases", null);
            model.addAttribute("users", null);

            currentPage = ControllerUtils.addPageAttributes(model, baseofusersRepo.findBasesByUser(user.getId()).size(),
                    currentPage, currentPageSize);
            switch (currentOrder) {
                case "basename_d" : {
                    basesofusersPage = baseofusersRepo.findBasesByUserPaginatedBaseDesc(
                            PageRequest.of(currentPage - 1, currentPageSize), user.getId());
                    break;
                }
                default: {
                    basesofusersPage = baseofusersRepo.findBasesByUserPaginatedBaseAsc(
                            PageRequest.of(currentPage - 1, currentPageSize), user.getId());
                }
            }


            model.addAttribute("basesofusers", basesofusersPage);
            //model.addAttribute("basesarc", baseRepo.findAllBaseAscByUser(user.getId()));
            model.addAttribute("isadm", 0);
        }

        model.addAttribute("baseofusersform", baseofusers);
        model.addAttribute("baseofusersarc", baseofusers);

        return "basesofuserslist";
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"add"}, method = RequestMethod.POST)
    public String addBase(Model model, Principal principal,
                          @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                          @ModelAttribute("baseofusersform") Basesofusers baseofusersform,
                          @RequestParam("page") Optional<Integer> page,
                          @RequestParam("size") Optional<Integer> size,
                          @RequestParam("order") Optional<String> order) {

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
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.add.record") +
                        ": " + ex.toString());
            }

        } else {
            if (baseid > 0) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.user"));
            } else {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.base"));
            }
        }
        return basesofusersList(model, principal, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"delete"}, method = RequestMethod.POST)
    public String deleteBase(Model model, Principal principal,
                             @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("delete") Integer baseofusersid,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users currentUser = userRepo.findByUsername(loginedUser.getUsername());
        if (currentUser.isAdm()) {
            findedBaseofusers = null;
            try {
                Basesofusers baseofusers = baseofusersRepo.findById(baseofusersid);
                Users user = baseofusers.getUsersByUserid();
                Bases base = baseofusers.getBasesByBaseid();
                baseofusersRepo.delete(baseofusers);
                user.getBasesofusersById().remove(baseofusers);
                base.getBasesofusersById().remove(baseofusers);
                model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.delete.record"));
            }
            catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.record") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.delete.record"));
        }
        return basesofusersList(model, principal, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"archivebase"}, method = RequestMethod.POST)
    public String archiveBase(Model model, Principal principal,
                              @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                              @ModelAttribute("archivebase") Integer baseofusersid,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("order") Optional<String> order) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users currentUser = userRepo.findByUsername(loginedUser.getUsername());
        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            try {
                Basesofusers baseofusersarc = baseofusersRepo.findById(baseofusersid);
                Bases base = baseofusersarc.getBasesByBaseid();
                Users user = baseofusersarc.getUsersByUserid();
                if (user == currentUser || loginedUser.getAuthorities().contains((GrantedAuthority) () -> "ADMIN")) {
                    String path_1c_base = base.getPath();
                    String basename = WebUtils.getArcBasename(loginedUser, userRepo, base);
                    BaseArchive ba = new BaseArchive();
                    if (ba.checkBase(path_1c_base)) {
                        model.addAttribute("errorMessage", messageByLocaleService.getMessage("info.archive.check"));
                    } else {
                        ba.removeOldArchive(env.getProperty("arc_catalog"), basename);
                        ba.archiveBase(env.getProperty("path_1c"), path_1c_base, env.getProperty("arc_catalog"), basename,
                                env.getProperty("username_1c_admin"), env.getProperty("password_1c_admin"));
                        model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.archive.base"));
                    }
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.user"));
                }
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.archive.record") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.archivebasedemo"));
        }
        return basesofusersList(model, principal, tablePageSize, page, size, order);
    }

    @RequestMapping(value = { "/basesofuserslist" }, params={"getarchive"}, method = RequestMethod.POST)
    public String getArchive(HttpServletResponse response, Model model, Principal principal,
                             @ModelAttribute("tablePageSize") TablePageSize tablePageSize,
                             @ModelAttribute("getarchive") Integer baseofusersid,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("order") Optional<String> order) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        Users currentUser = userRepo.findByUsername(loginedUser.getUsername());
        if (!loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            try {
                Basesofusers baseofusersarc = baseofusersRepo.findById(baseofusersid);
                Bases base = baseofusersarc.getBasesByBaseid();
                Users user = baseofusersarc.getUsersByUserid();
                if (user == currentUser || loginedUser.getAuthorities().contains((GrantedAuthority) () -> "ADMIN")) {
                    String basename = WebUtils.getArcBasename(loginedUser, userRepo, base);
                    if (basename.isEmpty()) {
                        model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.contragent"));
                        return basesofusersList(model, principal, tablePageSize, page, size, order);
                    }
                    System.out.println(basename);
                    BaseArchive ba = new BaseArchive();
                    ba.getArchive(env.getProperty("arc_catalog"), basename, response, this.servletContext);
                } else {
                    model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.no.user"));
                }
            } catch (Exception ex) {
                model.addAttribute("errorMessage", messageByLocaleService.getMessage("error.utils.getarchive") +
                        ": " + ex.toString());
            }
        } else {
            model.addAttribute("infoMessage", messageByLocaleService.getMessage("info.utils.archivebasedemo"));
        }
        return basesofusersList(model, principal, tablePageSize, page, size, order);
    }

}