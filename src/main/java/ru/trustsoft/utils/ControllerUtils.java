package ru.trustsoft.utils;

import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ControllerUtils {

    public static Integer addPageAttributes(Model model, int totalSize, Integer currentPage, int pageSize) {
        int totalPages = (totalSize - 1) / pageSize + 1;
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pageSize", pageSize);
        }
        return  currentPage;
    }
}
