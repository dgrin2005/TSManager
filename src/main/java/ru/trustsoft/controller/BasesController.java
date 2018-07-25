package ru.trustsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.trustsoft.dao.BasesDao;
import ru.trustsoft.model.BasesEntity;

import java.util.List;

@Controller
public class BasesController {

    @RequestMapping("/create-base")
    @ResponseBody
    public String create(String basename, String description) {
        String baseId = "";
        try {
            BasesEntity base = new BasesEntity(basename, description);
            baseDao.save(base);
            baseId = String.valueOf(base.getBaseid());
        }
        catch (Exception ex) {
            return "Error creating the base: " + ex.toString();
        }
        return "Base succesfully created with id = " + baseId;
    }

    @RequestMapping("/delete-base")
    @ResponseBody
    public String delete(String basename) {
        try {
            BasesEntity base = baseDao.findByBasename(basename);
            baseDao.delete(base);
        }
        catch (Exception ex) {
            return "Error deleting the base:" + ex.toString();
        }
        return "Base succesfully deleted!";
    }

    @RequestMapping("/get-by-name-base")
    @ResponseBody
    public String getByName(String basename) {
        String baseId = "";
        try {
            BasesEntity base = baseDao.findByBasename(basename);
            baseId = String.valueOf(base.getBaseid());
        }
        catch (Exception ex) {
            return "Base not found";
        }
        return "The base id is: " + baseId;
    }

    @RequestMapping("/get-by-id-base")
    @ResponseBody
    public String getById(int baseid) {
        String baseName = "";
        try {
            BasesEntity base = baseDao.findByBaseid(baseid);
            baseName = base.toString();
        }
        catch (Exception ex) {
            return "Base not found";
        }
        return "The base name is: " + baseName;
    }

    @RequestMapping("/update-base")
    @ResponseBody
    public String updateBase(String basename, String description) {
        try {
            BasesEntity base = baseDao.findByBasename(basename);
            base.setDescription(description);
            baseDao.save(base);
        }
        catch (Exception ex) {
            return "Error updating the base: " + ex.toString();
        }
        return "Base succesfully updated!";
    }

    // Private fields

    @Autowired
    private BasesDao baseDao;

}