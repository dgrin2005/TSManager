package ru.trustsoft.utils;

import java.io.IOException;

public class ReconciliationAct implements UtilsConst {

    public void getReconciliationAct() throws IOException {

        // «C:\Program Files (x86)\1cv8\8.3.5.хххх\bin\1cv8.exe» ENTERPRISE /DisableStartupMessages
        // /FС:\путь к базе /N»ИмяПользователя» /P»ПарольПользователя» /Execute с:\путь к обработке\самаобработка.epf

        Runtime r = Runtime.getRuntime();
        String cmd = "\"C:\\Program Files (x86)\\1cv8\\common\\1cestart.exe\" ENTERPRISE /DisableStartupMessages" +
                " /F\"D:\\1S-Bases\\Траст-Софт\\TrSoft\\\" /NАдминистратор";
        r.exec(cmd);

    }
}
