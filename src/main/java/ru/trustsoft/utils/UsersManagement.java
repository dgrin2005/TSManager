package ru.trustsoft.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UsersManagement {

    public int isDisabledFromOS (String username) throws IOException{

        //wmic useraccount where name='username' getDisabled
        Runtime r = Runtime.getRuntime();
        Process p;
        String cmd = "wmic useraccount where name='" + username + "' get Disabled";
        System.out.println(cmd);
        int isDisabled = 0;
        p = r.exec(cmd);
        String s;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((s = br.readLine()) != null) {
            System.out.println(s);
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
        System.out.println(cmd);
        Boolean isDisabled = false;
        p = r.exec(cmd);
    }
}
