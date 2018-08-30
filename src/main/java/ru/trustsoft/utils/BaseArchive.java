/**
 * TerminalServerManager
 *    BaseArchive.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import org.springframework.security.core.userdetails.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class BaseArchive {

    private static final Logger logger = Logger.getLogger(String.valueOf(BaseArchive.class));

    private final User loginedUser;

    public BaseArchive(User loginedUser) {
        this.loginedUser = loginedUser;
    }

    public boolean checkBase(String path_1c_base) {

        String fileName = path_1c_base + "\\1cv8.1cl";
        File file = new File(fileName);
        return file.exists();
    }

    public void archiveBase(String path_1c, String path_1c_base, String arc_catalog, String basename,
                            String username, String password)
            throws IOException {

        // "C:\Program Files (x86)\1cv8\common\1cestart.exe" CONFIG /F"D:\1C_Bases\buh" /N"Админ" /P"MyPassword"
        // /Out"C:\1c.log" /DumpIB"\\backup\1c\buh_%date%.dt"

        Runtime r = Runtime.getRuntime();
        String cmd = path_1c + " CONFIG /DisableStartupMessages" +
                " /F\"" + path_1c_base + "\" /N" + username + " /P" + password +
                " /DumpIB\"" + arc_catalog + basename + "\"";
        logger.info(loginedUser.getUsername() + " : " + cmd);
        Process p = r.exec(cmd);
    }

    public void removeOldArchive (String arc_catalog, String basename) {

        String fileName = arc_catalog + basename;
        File file = new File(fileName);

        if (file.delete()) {
            logger.info(loginedUser.getUsername() + " : " + "Archive " + fileName + " deleted");
        } else {
            logger.info(loginedUser.getUsername() + " : " + "Archive " + fileName + " not found");
        }

    }

    public void getArchive(String arc_catalog, String basename, HttpServletResponse response,
                           ServletContext servletContext) throws IOException {
        String fileName = arc_catalog + basename;
        WebUtils.downloadFile (loginedUser, fileName, response, servletContext);
    }

}
