package ru.trustsoft.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaseArchive {

    public void archiveBase(String path_1c, String path_1c_base, String arc_catalog, String basename) throws IOException, InterruptedException {

        // "C:\Program Files (x86)\1cv8\common\1cestart.exe" CONFIG /F"D:\1C_Bases\buh" /N"Админ" /P"MyPassword"
        // /Out"C:\1c.log" /DumpIB"\\backup\1c\buh_%date%.dt"


        Runtime r = Runtime.getRuntime();
        String cmd = path_1c + " CONFIG /DisableStartupMessages" +
                " /F" + path_1c_base + " /NАдмин1С /P17YftDjcRji " +
                " /DumpIB\"" + arc_catalog + basename + "\"";
        System.out.println(cmd);
        Process p = r.exec(cmd);
        //int exitVal = p.waitFor();
    }

    public void removeOldArchive (String arc_catalog, String basename) {

        String fileName = arc_catalog + basename;
        File file = new File(fileName);

        if (file.delete()) {
            System.out.println("Archive " + fileName + " deleted");
        } else {
            System.out.println("Archive " + fileName + " not found");
        }

    }

    public void getArchive(String arc_catalog, String basename, HttpServletResponse response, ServletContext servletContext) throws IOException {
        String fileName = arc_catalog + basename;
        WebUtils.downloadFile (fileName, response, servletContext);
    }

}
