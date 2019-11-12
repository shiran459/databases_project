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
import java.util.ArrayList;

@Controller
public class SearchController {

    //======================= ARTICLE MAPPINGS ========================//
    @GetMapping("/search_articles_by_word")
    public String searchArticlesByWord(@RequestParam(name = "word", required = true) String word,
                                       Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.searchArticlesByWord(word);
            model.addAttribute("articleIdList", articleIdList);
            return "search_articles";
        } catch (Exception e) {
            return "error";
        }
    }

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

    @GetMapping("/search_articles_by_title")
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

    @GetMapping("/search_articles_by_category")
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

    // TODO: Decide if keep or remove (need to decide where words are displayed in the website)
//
//    @GetMapping("/articles/{article_id}/words")
//    public String displayArticleWords(@PathVariable(value="article_id", required=true) int articleId,
//                                           Model model) {
//        try {
//            List<String> wordList = ArticleLib.getArticleWords(articleId);
//            model.addAttribute("wordList", wordList);
//            return "displayWords";
//        } catch (Exception e){
//            return "error";
//        }
//    }

    @GetMapping("/words/view_all_words")
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
