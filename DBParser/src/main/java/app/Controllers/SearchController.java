package app.Controllers;

import app.lib.ArticleLib;
import app.lib.WordLib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class SearchController {

    //======================= ARTICLE MAPPINGS ========================//
    @GetMapping("/articles/search/")
    public String searchArticles(@RequestParam(name = "searchKey", required = true) String searchKey,
                                 @RequestParam(name = "searchInput", required = true) String searchInput,
                                       Model model){

    }


    /**
     * Displays article search results for searches made with a word as key.
     * @param word  Word to use as search key.
     * @param model
     * @return  Article search result page.
     */
    @GetMapping("/articles/search/by_word")
    public String searchArticlesByWord(@RequestParam(name = "word", required = true) String word,
                                       Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByWord(word);
            model.addAttribute("articleIdList", articleIdList);
            return "articles_search";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Displays article search results for searches made with words as key.
     * @param wordsString  Words to use as search key.
     * @param model
     * @return  Article search result page.
     */
    @GetMapping("/search_articles_by_words")
    public String searchArticlesByWords(@RequestParam(name = "words", required = true) String wordsString,
                                        Model model) {
        List<String> words = Arrays.asList(wordsString.split("-"));
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByWords(words);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
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
    @GetMapping("/articles/search/by_title")
    public String searchArticlesByTitle(@RequestParam(name = "title", required = true) String title,
                                        Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByTitle(title);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
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
    @GetMapping("/articles/search/by_category")
    public String searchArticlesByCategory(@RequestParam(name = "category", required = true) String category,
                                           Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByCategory(category);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e) {
            return "error";
        }
    }

    //=========================== WORD MAPPINGS ==========================//
    @GetMapping("/words/view_all")
    public String displayAllWords(int articleId,
                                  Model model) {
        try {
            List<String> wordList = WordLib.getAllWords();
            model.addAttribute("wordList", wordList);
            return "displayWords";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/by_article")
    public String displayWords(@RequestParam(value = "article_id", required = true) int articleId,
                               Model model) {
        try {
            List<String> wordList = ArticleLib.getArticleWords(articleId);
            model.addAttribute("wordList", wordList);
            return "displayWords";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/contexts")
    public String displayWordContexts(@RequestParam(value = "word_id", required = true) int wordId,
                                      Model model) {
        try {
            Object[] contexts = WordLib.getContexts(wordId);
            model.addAttribute("articleIdList", contexts[0]);
            model.addAttribute("titleList", contexts[1]);
            model.addAttribute("contextList", contexts[2]);

            String value = WordLib.getWordValue(wordId);
            model.addAttribute("wordValue", value);

            return "display_contexts";
        } catch (Exception e) {
            return "error";
        }
    }
}
