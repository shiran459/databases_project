package app.controllers;

import app.lib.ArticleLib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * Controls pages belonging to the "Articles" section of the NavBar.
 */
@Controller
public class ArticlesController {

    //=================================== ARTICLE SEARCH PAGE ===================================//

    /**
     * Display article search page.
     * @param model
     * @return Article search page
     */
    @GetMapping("/articles/search")
    public String displayArticleSearchPage(Model model) {
        return "articles/article_search";
    }

    /**
     * Display article search results page.
     * @param model
     * @return Article search results page
     */
    @GetMapping("/articles/search/results")
    public String searchArticle(@RequestParam(name = "searchKey", required = true) String searchKey,
                                 @RequestParam(name = "searchInput", required = true) String searchInput,
                                       Model model) {
        switch (searchKey) {
            case "title": {
                return searchArticleByTitle(searchInput, model);
            }
            case "words": {
                return searchArticleByWords(searchInput, model);
            }
            case "category": {
                return searchArticleByCategory(searchInput, model);
            }
            default:
                return "error";
        }
    }

    /**
     * Displays article search results for searches made with words as key.
     * @param wordsString  Words to use as search key.
     * @param model
     * @return  Article search result page.
     */
    public String searchArticleByWords (String wordsString, Model model) {
        List<String> words = Arrays.asList(wordsString.split("-"));
        try {
            List<String> titleList = ArticleLib.searchArticlesByWords(words);
            model.addAttribute("titleList", titleList);
            return "article_search_results";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Displays article search results for searches made with title as key.
     * @param title Title to use as search key.
     * @param model
     * @return Article search result page.
     */
    public String searchArticleByTitle(String title, Model model) {
        try {
            List<String> titleList = ArticleLib.searchArticlesByTitle(title);
            model.addAttribute("titleList", titleList);
            return "article_search_results";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Displays article search results for searches made with category as key.
     * @param category Category to use as search key.
     * @param model
     * @return Article search result page.
     */
    public String searchArticleByCategory(String category, Model model) {
        try {
            List<String> titleList = ArticleLib.searchArticlesByCategory(category);
            model.addAttribute("titleList", titleList);
            return "article_search_results";
        } catch (Exception e) {
            return "error";
        }
    }

    //=============================== ARTICLE UPLOAD PAGE ====================================//
    @GetMapping("/articles/upload")
    public String displayUploadPage(Model model) {

        return "article_upload";
    }

    @GetMapping("/articles/upload/success")
    public String displayUploadSuccessPage(Model model) {

        return "article_upload_success";
    }
}