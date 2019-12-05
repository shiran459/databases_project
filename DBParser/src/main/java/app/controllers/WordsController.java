package app.controllers;

import app.lib.ArticleLib;
import app.lib.WordLib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controls pages belonging to the "Words" section of the NavBar.
 */
@Controller
public class WordsController {

    @GetMapping("/words/view_all")
    public String displayAllWords(Model model) {
        try {
            List<String> wordList = WordLib.getAllWords();
            model.addAttribute("wordList", wordList);
            return "words/display_words";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/search")
    public String searchWords(Model model) {
        try {
            return "words/words_search";
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
            return "words/display_words";
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

            return "words/display_contexts";
        } catch (Exception e) {
            return "error";
        }
    }
}
