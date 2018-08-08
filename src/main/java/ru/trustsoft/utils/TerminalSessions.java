package ru.trustsoft.utils;

import java.io.*;
import java.util.ArrayList;

public class TerminalSessions implements UtilsConst {

    private final ArrayList<TerminalSession> terminalSessions = new ArrayList<>();

    public void getSessions() throws IOException {

        //   qwinsta /server:terminal.example.com

        Runtime r = Runtime.getRuntime();
        Process p;
        String cmd = "qwinsta /server:" + SERVER_ADDRESS;
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
            }
        }
        br.close();

        for (TerminalSession terminalSession : terminalSessions) {
            System.out.println(terminalSession.getUsername() + "\\" + terminalSession.getId());
        }
    }

    public void  termineSession(String username) throws IOException {

        //  rwinsta /server:terminal.example.com <session-id>

        for (TerminalSession ts: terminalSessions) {
            if (ts.getUsername().equals(username)) {

                Runtime r = Runtime.getRuntime();
                String cmd = "rwinsta /server:" + SERVER_ADDRESS + " " + ts.getId();
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

