package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trustsoft.repo.BasesRepo;
import ru.trustsoft.model.BasesEntity;

@Controller
@RequestMapping(path="/")
public class BasesController {

/*
    @GetMapping("/create-base")
    @ResponseBody
    public String create(String basename, String description) {
        String baseId = "";
        try {
            BasesEntity base = new BasesEntity(basename, description);
            baseRepo.save(base);
            baseId = String.valueOf(base.getBaseid());
        }
        catch (Exception ex) {
            return "Error creating the base: " + ex.toString();
        }
        return "Base succesfully created with id = " + baseId;
    }

    @GetMapping("/delete-base")
    @ResponseBody
    public String delete(String basename) {
        try {
            BasesEntity base = baseRepo.findByBasename(basename);
            baseRepo.delete(base);
        }
        catch (Exception ex) {
            return "Error deleting the base:" + ex.toString();
        }
        return "Base succesfully deleted!";
    }

    @GetMapping("/get-by-name-base")
    @ResponseBody
    public String getByName(String basename) {
        String baseId = "";
        try {
            BasesEntity base = baseRepo.findByBasename(basename);
            baseId = String.valueOf(base.getBaseid());
        }
        catch (Exception ex) {
            return "Base not found";
        }
        return "The base id is: " + baseId;
    }

    @GetMapping("/get-by-id-base")
    @ResponseBody
    public String getById(int baseid) {
        String baseName = "";
        try {
            BasesEntity base = baseRepo.findByBaseid(baseid);
            baseName = base.toString();
        }
        catch (Exception ex) {
            return "Base not found";
        }
        return "The base name is: " + baseName;
    }

    @GetMapping("/update-base")
    @ResponseBody
    public String updateBase(String basename, String description) {
        try {
            BasesEntity base = baseRepo.findByBasename(basename);
            base.setDescription(description);
            baseRepo.save(base);
        }
        catch (Exception ex) {
            return "Error updating the base: " + ex.toString();
        }
        return "Base succesfully updated!";
    }

    @GetMapping(path="/all-base")
    @ResponseBody
    public Iterable<BasesEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return baseRepo.findAll();
    }
*/

    @GetMapping(path="/baseslist")
    public String basesList(Model model) {

        model.addAttribute("bases", baseRepo.findAll());

        return "baseslist";
    }

    @GetMapping(path="/addbase")
    public String showAddBasePage(Model model) {

        BasesEntity base = new BasesEntity();
        model.addAttribute("baseform", base);

        return "addbase";
    }

    @RequestMapping(value = { "/addbase" }, method = RequestMethod.POST)
    public String saveBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        String basename = baseform.getBasename();
        String description = baseform.getDescription();

        String baseId = "";
        try {
            BasesEntity base = new BasesEntity(basename, description);
            baseRepo.save(base);
            baseId = String.valueOf(base.getBaseid());
            //return "Base succesfully created with id = " + baseId;
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            //return "Error creating the base: " + ex.toString();
            model.addAttribute("errorMessage", "Error creating the base: " + ex.toString());
        }

        return "addbase";
    }

    @GetMapping(path="/removebase")
    public String showRemoveBasePage(Model model) {

        BasesEntity base = new BasesEntity();
        model.addAttribute("baseform", base);

        return "removebase";
    }

    @RequestMapping(value = { "/removebase" }, method = RequestMethod.POST)
    public String removeBase(Model model, @ModelAttribute("baseform") BasesEntity baseform) {

        String basename = baseform.getBasename();

        try {
            BasesEntity base = baseRepo.findByBasename(basename);
            baseRepo.delete(base);
            return "redirect:/baseslist";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting the base:" + ex.toString());
        }

        return "removebase";
    }

    // Private fields

    @Autowired
    private BasesRepo baseRepo;

    @Value("${error.message}")
    private String errorMessage;

}