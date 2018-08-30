/**
 * TerminalServerManager
 *    MediaTypeUtils.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.utils;

import javax.servlet.ServletContext;

import org.springframework.http.MediaType;

class MediaTypeUtils {

    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
        String mineType = servletContext.getMimeType(fileName);
        try {
            return MediaType.parseMediaType(mineType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}