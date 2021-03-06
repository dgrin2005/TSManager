/**
 * TerminalServerManager
 *    MessageByLocalServiceImpl.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

    @Autowired
    private MessageSource messageSource;

    @Override
    public String getMessage(String id) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(id,null, locale);
    }
}
