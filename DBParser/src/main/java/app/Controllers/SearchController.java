package app.Controllers;

import app.lib.ArticleLib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Controller
public class SearchController {

    //======================= ARTICLE SEARCH MAPPINGS ========================//
    @GetMapping("/search_articles_by_word")
    public String searchArticlesByWord(@RequestParam(name="word", required=true) String word,
                             Model model) {
        try {
//            List<Integer> articleIdList = ArticleLib.getArticlesByWord(word);
            List<Integer> articleIdList = new ArrayList<>();
            articleIdList.add(5);
            articleIdList.add(33);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e){
            return "error";
        }
    }

    @GetMapping("/search_articles_by_words")
    public String searchArticlesByWords(@RequestParam(name="words", required=true) String wordsString,
                             Model model) {
        List<String> words = Arrays.asList(wordsString.split("-"));
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByWords(words);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e){
            return "error";
        }
    }

    @GetMapping("/search_articles_by_title")
    public String searchArticlesByTitle(@RequestParam(name="title", required=true) String title,
                                        Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByTitle(title);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e){
            return "error";
        }
    }

    @GetMapping("/search_articles_by_category")
    public String searchArticlesByCategory(@RequestParam(name="category", required=true) String category,
                                        Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByCategory(category);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e){
            return "error";
        }
    }

    //=========================== DISPLAY MAPPINGS ==========================//

    @GetMapping("/articles/{article_id}/words")
    public String displayWords(@PathVariable(value="article_id") int articleId,
                                           Model model) {
        try {
            List<String> wordList = ArticleLib.getArticleById(articleId);
            model.addAttribute("wordList", wordList);
            return "displayWords";
        } catch (Exception e){
            return "error";
        }
    }
}
