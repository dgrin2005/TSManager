/**
 * TerminalServerManager
 *    TerminalSessions.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import org.springframework.security.core.userdetails.User;
import ru.trustsoft.model.TerminalSession;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TerminalSessions {

    private static final Logger logger = Logger.getLogger(String.valueOf(TerminalSession.class));
    private final ArrayList<TerminalSession> terminalSessions = new ArrayList<>();

    private final User loginedUser;

    public TerminalSessions(User loginedUser) {
        this.loginedUser = loginedUser;
    }

    public void getSessions(String tsmserveraddress) throws IOException {

        //   qwinsta /server:terminal.example.com
        Runtime r = Runtime.getRuntime();
        Process p;
        String cmd = "qwinsta /server:" + tsmserveraddress;
        logger.info(loginedUser.getUsername() + " : " + cmd);
        p = r.exec(cmd);
        String s;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while((s = br.readLine())!= null) {
            String[] words = s.trim().split(" ");
            TerminalSession terminalSession = new TerminalSession();
            int i = 0;
            String username = "";
            String id;
            for (String w: words) {
                if (i == 0) {
                    if (w.length() < 8) {
                        break;
                    }
                    if (!w.substring(0, 8).equals("rdp-tcp#")) {
                        break;
                    }
                }
                if (w.length() != 0 && i > 0) {
                    if (username.length() == 0) {
                        username = w;
                    } else {
                        id = w;
                        terminalSession.setUsername(username);
                        terminalSession.setId(id);
                        break;
                    }
                }
                i++;
            }
            if (username.length() != 0) {
                terminalSessions.add(terminalSession);
                logger.info(loginedUser.getUsername() + " : " + terminalSession.toString());
            }
        }
        br.close();

    }

    public void  termineSession(String tsmserveraddress, String username) throws IOException {

        //  rwinsta /server:terminal.example.com <session-id>
        for (TerminalSession ts: terminalSessions) {
            if (ts.getUsername().equals(username)) {
                Runtime r = Runtime.getRuntime();
                String cmd = "rwinsta " + ts.getId() +" /server:" + tsmserveraddress;
                logger.info(loginedUser.getUsername() + " : " + cmd);
                r.exec(cmd);
            }
        }
    }

    @Override
    public String toString() {
        return "TerminalSessions{" +
                "terminalSessions=" + terminalSessions +
                '}';
    }
}

