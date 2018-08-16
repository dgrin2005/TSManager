package ru.trustsoft.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.ReconActParameters;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;

public class WebUtils {

    public static String toString(User user, Locale locale) {
        StringBuilder sb = new StringBuilder();
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        if (locale == Locale.ENGLISH) {
            sb.append("<p>Username: ").append(user.getUsername()).append("</p>");
            sb.append("<p>Is non locked: ").append(user.isAccountNonLocked()).append("</p>");
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
        } else {
            sb.append("<p>Пользователь: ").append(user.getUsername()).append("</p>");
            sb.append("<p>Не заблокирован: ").append(user.isAccountNonLocked()).append("</p>");
            if (authorities != null && !authorities.isEmpty()) {
                sb.append("<p>Права: ");
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
        }

        return sb.toString();
    }

    public static String toString(Contragents contragent, Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (locale == Locale.ENGLISH) {
            if (contragent != null) {
                sb.append("Contragent:").append(contragent.getContragentname()).
                        append("(INN ").append(contragent.getInn()).append(")");
            } else {
                sb.append("Contragent: Not found");
            }
        } else {
            if (contragent != null) {
                sb.append("Контрагент:").append(contragent.getContragentname()).
                        append("(ИНН ").append(contragent.getInn()).append(")");
            } else {
                sb.append("Контрагент: Не найден");
            }
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

    public static String getActFileName(User loginedUser, UsersRepo userRepo, ReconActParameters reconActParameters) throws Exception {
        String filename = "";

        if (loginedUser.getAuthorities().contains((GrantedAuthority) () -> "DEMO")) {
            filename = "DemoAct.pdf";
        } else {
            Users user = userRepo.findByUsername(loginedUser.getUsername());
            Contragents contragent = user.getContragentsByContragentid();
            if (contragent != null) {
                filename = contragent.getInn() + "_" + WebUtils.toString(LocalDate.now()) + "_" +
                        WebUtils.toString(LocalDate.parse(reconActParameters.getStartDate())) + "_" +
                        WebUtils.toString(LocalDate.parse(reconActParameters.getEndDate())) + ".pdf";
            }
        }
        return filename;
    }
}
