package ru.trustsoft.utils;

import java.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ReconciliationAct implements UtilsConst {

    public void orderReconciliationAct(String parameters1C) throws IOException {

        // «C:\Program Files (x86)\1cv8\8.3.5.хххх\bin\1cv8.exe» ENTERPRISE /DisableStartupMessages
        // /FС:\путь к базе /N»ИмяПользователя» /P»ПарольПользователя» /Execute с:\путь к обработке\самаобработка.epf

        Runtime r = Runtime.getRuntime();
        String cmd = PATH_1C + " ENTERPRISE /DisableStartupMessages" +
                " /F" + PATH_1C_BASE + " /NАдминистратор /Execute "+ PATH_EPF +
                " /C\"" + parameters1C + ";" + ACT_CATALOG + "\"" ;
        r.exec(cmd);

    }

    public void getReconciliationAct(String fileName, HttpServletResponse response, ServletContext servletContext) throws IOException {

        fileName = ACT_CATALOG + fileName;

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

        File file = new File(fileName);

        // Content-Type
        // application/pdf
        response.setContentType(mediaType.getType());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());

        // Content-Length
        response.setContentLength((int) file.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }

}
