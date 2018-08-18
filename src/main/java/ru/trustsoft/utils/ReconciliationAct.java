package ru.trustsoft.utils;

import java.io.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ReconciliationAct {

    public void orderReconciliationAct(String path_1c, String path_1c_base, String path_epf,
                                       String act_catalog, String parameters1C) throws IOException, InterruptedException {

        // «C:\Program Files (x86)\1cv8\8.3.5.хххх\bin\1cv8.exe» ENTERPRISE /DisableStartupMessages
        // /FС:\путь к базе /N»ИмяПользователя» /P»ПарольПользователя» /Execute с:\путь к обработке\самаобработка.epf

        Runtime r = Runtime.getRuntime();
        String cmd = path_1c + " ENTERPRISE /DisableStartupMessages" +
                " /F" + path_1c_base + " /NАдминистратор /Execute "+ path_epf +
                " /C\"" + parameters1C + ";" + act_catalog + "\"" ;
        System.out.println(cmd);
        Process p = r.exec(cmd);
        //int exitVal = p.waitFor();

    }

    public void getReconciliationAct(String act_catalog, String fileName, HttpServletResponse response, ServletContext servletContext) throws IOException {
        fileName = act_catalog + fileName;
        WebUtils.downloadFile (fileName, response, servletContext);
    }

    public void sendEmail(JavaMailSender sender, String tsmemailaddress, String act_catalog, String fileName, String email) throws Exception {

        fileName = act_catalog + fileName;
        if (new File(fileName).exists()) {
            MimeMessage message = sender.createMimeMessage();
            // Enable the multipart flag!
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(email);
            helper.setReplyTo(tsmemailaddress);
            helper.setFrom(tsmemailaddress);
            helper.setText("Акт сверки");
            helper.setSubject("Акт сверки");
            FileSystemResource file = new FileSystemResource(fileName);
            helper.addAttachment(file.getFilename(), file);
            sender.send(message);
        } else {
            throw new IOException("File not found");
        }
    }

}
