/**
 * TerminalServerManager
 *    ReconciliationAct.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import java.io.*;
import java.util.logging.Logger;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ReconciliationAct {

    private static final Logger logger = Logger.getLogger(String.valueOf(ReconciliationAct.class));

    private final User loginedUser;

    public ReconciliationAct(User loginedUser) {
        this.loginedUser = loginedUser;
    }

    public void orderReconciliationAct(String path_1c, String path_1c_base, String path_epf,
                                       String act_catalog, String parameters1C,
                                       String username, String password) throws IOException {

        // «C:\Program Files (x86)\1cv8\8.3.5.хххх\bin\1cv8.exe» ENTERPRISE /DisableStartupMessages
        // /FС:\путь к базе /N»ИмяПользователя» /P»ПарольПользователя» /Execute с:\путь к обработке\самаобработка.epf

        Runtime r = Runtime.getRuntime();
        String cmd = path_1c + " ENTERPRISE /DisableStartupMessages" +
                " /F" + path_1c_base + " /N" + username + " /P" + password + " /Execute "+ path_epf +
                " /C\"" + parameters1C + ";" + act_catalog + "\"" ;
        logger.info(loginedUser.getUsername() + " : " + cmd);
        Process p = r.exec(cmd);
    }

    public void getReconciliationAct(String act_catalog, String fileName, HttpServletResponse response,
                                     ServletContext servletContext) throws IOException {
        fileName = act_catalog + fileName;
        WebUtils.downloadFile (loginedUser, fileName, response, servletContext);
    }

    public void sendActFromEmail(JavaMailSender sender, String tsmemailaddress, String act_catalog, String fileName,
                          String email, String textString, String subjectString) throws Exception {

        fileName = act_catalog + fileName;
        if (new File(fileName).exists()) {
            WebUtils.sendEmail(loginedUser, sender, tsmemailaddress, textString, subjectString, fileName, email);
        } else {
            throw new IOException("File not found");
        }
    }
}
