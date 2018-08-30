/**
 * TerminalServerManager
 *    UsersManagement.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import org.springframework.security.core.userdetails.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class UsersManagement {

    private static final Logger logger = Logger.getLogger(String.valueOf(UsersManagement.class));

    private final User loginedUser;

    public UsersManagement(User loginedUser) {
        this.loginedUser = loginedUser;
    }

    public int isDisabledFromOS (String username) throws IOException{

        //wmic useraccount where name='username' getDisabled
        Runtime r = Runtime.getRuntime();
        Process p;
        String cmd = "wmic useraccount where name='" + username + "' get Disabled";
        logger.info(loginedUser.getUsername() + " : " + cmd);
        int isDisabled = 0;
        p = r.exec(cmd);
        String s;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((s = br.readLine()) != null) {
            logger.info(loginedUser.getUsername() + " : " + s);
            if (s.trim().toUpperCase().equals("TRUE")) {
                isDisabled = 1;
            }
            if (s.trim().toUpperCase().equals("FALSE")) {
                isDisabled = -1;
            }
        }
        br.close();
        return isDisabled;
    }

    public void setEnabledToOS (String username, String enabled) throws IOException {

        //net user username /active:yes/no
        Runtime r = Runtime.getRuntime();
        Process p;
        String cmd = "net user " + username + " /active:" + enabled;
        logger.info(loginedUser.getUsername() + " : " + cmd);
        Boolean isDisabled = false;
        p = r.exec(cmd);
    }
}
