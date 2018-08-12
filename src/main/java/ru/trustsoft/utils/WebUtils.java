package ru.trustsoft.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.Collection;

public class WebUtils {

    public static String toString(User user) {
        StringBuilder sb = new StringBuilder();

        sb.append("<p>Username: ").append(user.getUsername()).append("</p>");
        sb.append("<p>Is non locked: ").append(user.isAccountNonLocked()).append("</p>");

        Collection<GrantedAuthority> authorities = user.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            sb.append("<p>Authority: ");
            boolean first = true;
            for (GrantedAuthority a : authorities) {
                if (first) {
                    sb.append(a.getAuthority());
                    first = false;
                } else {
                    sb.append(", ").append(a.getAuthority());
                }
            }
            sb.append("</p>");
        }
        return sb.toString();
    }

    public static String toString(LocalDate date) {
        StringBuilder sb = new StringBuilder();

        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();

        sb.append(day < 10 ? "0" : "").append(day);
        sb.append(month < 10 ? "0" : "").append(month);
        sb.append(year);

        return sb.toString();
    }
}
