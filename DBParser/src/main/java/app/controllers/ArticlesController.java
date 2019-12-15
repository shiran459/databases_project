package app.controllers;

import app.lib.ArticleLib;
import app.lib.UserLib;
import app.utils.Article;
import app.utils.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public String displayArticleSearchPage(Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

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
                                       Model model, HttpServletRequest request) {

        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

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
            default:{
                return "error";
            }
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
            List<Article> articleList = ArticleLib.searchArticlesByWords(words);
            model.addAttribute("articleList", articleList);
            return "articles/article_search_results";
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
            List<Article> articleList = ArticleLib.searchArticlesByTitle(title);
            model.addAttribute("articleList", articleList);
            return "articles/article_search_results";
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
    public String displayUploadPage(Model model,  HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

        return "articles/article_upload";
    }

    @GetMapping("/articles/upload/success")
    public String displayUploadSuccessPage(Model model,  HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

        return "articles/article_upload_success";
    }


    //=============================== ARTICLE DISPLAY ====================================//
    @GetMapping("/articles/{id}")
    public String displayArticle(@PathVariable String id, Model model, HttpServletRequest request) {
        try {
            User user = UserLib.extractUser(request);
            //Fetch Article
            Article article = ArticleLib.getArticleById(Integer.parseInt(id));

            //Read into string
            String content = Files.readString(Paths.get(article.path));
            //Wrap in model
            model.addAttribute("articleContent", content);
            model.addAttribute("article", article);
            model.addAttribute("user", user);

            return "articles/article_display";
        } catch (Exception e) {
            return "error";
        }
    }
}