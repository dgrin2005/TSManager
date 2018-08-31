/**
 * TerminalServerManager
 *    WebUtils.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.trustsoft.model.Bases;
import ru.trustsoft.model.Contragents;
import ru.trustsoft.model.ReconActParameters;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;
import ru.trustsoft.service.MessageByLocaleService;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.logging.Logger;

public class WebUtils {

    private static final Logger logger = Logger.getLogger(String.valueOf(WebUtils.class));

    public static String toString(User user, MessageByLocaleService messageByLocaleService) {
        StringBuilder sb = new StringBuilder();
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        sb.append("<p>").append(messageByLocaleService.getMessage("info.tostring.username")).append(": ").
                append(user.getUsername()).append("</p>");
        sb.append("<p>").append(messageByLocaleService.getMessage("info.tostring.isnonlocked")).append(": ").
                append(user.isAccountNonLocked()).append("</p>");
        if (authorities != null && !authorities.isEmpty()) {
            sb.append("<p>").append(messageByLocaleService.getMessage("info.tostring.authority")).append(": ");
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

    public static String toString(Contragents contragent, MessageByLocaleService messageByLocaleService) {
        StringBuilder sb = new StringBuilder();
        if (contragent != null) {
            sb.append(messageByLocaleService.getMessage("info.tostring.contragent")).append(": ").
                    append(contragent.getContragentname()).append(" (").
                    append(messageByLocaleService.getMessage("info.tostring.inn")).append(" ").
                    append(contragent.getInn()).append(")");
        } else {
            sb.append(messageByLocaleService.getMessage("info.tostring.contragentnotfound"));
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

    public static String getActFileName(User loginedUser, UsersRepo userRepo,
                                        ReconActParameters reconActParameters) throws Exception {
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

    public static String getArcBasename(Bases base)  throws Exception {
        String filename = "";
        {
            Contragents contragent = base.getContragentsByContragentid();
            if (contragent != null) {
                filename = contragent.getInn() + "_" + base.getId() + ".dt";
            }
        }
        return filename;
    }

    public static void downloadFile(User loginedUser, String fileName, HttpServletResponse response,
                                     ServletContext servletContext) throws IOException {
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

        if (new File(fileName).exists()) {
            File file = new File(fileName);
            response.setContentType(mediaType.getType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
            response.setContentLength((int) file.length());
            InputStream inputStream = new FileInputStream(file);
            int nRead;
            while ((nRead = inputStream.read()) != -1) {
                response.getWriter().write(nRead);
            }
            inputStream.close();
            logger.info(loginedUser.getUsername() + " : " + "Downloaded "+ fileName);
        } else {
            throw new IOException("File not found");
        }
    }

    public static void sendEmail(User loginedUser, JavaMailSender sender, String fromAddress, String textString,
                                 String subjectString, String fileName, String toAddress) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setTo(toAddress);
        helper.setReplyTo(fromAddress);
        helper.setFrom(fromAddress);
        helper.setText(textString);
        helper.setSubject(subjectString);
        FileSystemResource file = new FileSystemResource(fileName);
        helper.addAttachment(file.getFilename(), file);
        sender.send(message);
        logger.info(loginedUser.getUsername() + " : " + "Sended " + fileName + " to " + toAddress);
    }

    public static String getFileDate(String arc_catalog, String fileName) throws IOException {
        String dateCreated = "";
        fileName = arc_catalog + fileName;
        if (new File(fileName).exists()) {
            Path path = Paths.get(fileName);
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            dateCreated = df.format(attributes.creationTime().toMillis());
        }
        return dateCreated;
    }

}
