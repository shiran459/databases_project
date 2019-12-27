package app.controllers;

import app.lib.ArticleLib;
import app.lib.GroupLib;
import app.lib.UserLib;
import app.lib.WordLib;
import app.utils.Article;
import app.utils.ArticleWord;
import app.utils.User;
import app.utils.Word;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * Controls pages belonging to the "Words" section of the NavBar.
 */
@Controller
public class WordsController {

    @GetMapping("/words/view_all")
    public String displayAllWords(Model model, HttpServletRequest request) {
        try {
            List<Word> wordList = WordLib.getAllWords();
            model.addAttribute("wordList", wordList);
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/all_words";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/search")
    public String searchWords(Model model, HttpServletRequest request) {
        try {
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/words_search";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/by_article/{articleId}")
    public String displayArticleWords(@PathVariable int articleId, Model model, HttpServletRequest request) {
        try {
            List<ArticleWord> wordList = ArticleLib.getArticleWords(articleId);
            model.addAttribute("wordList", wordList);
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/word_locations_in_article";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/by_group/{groupId}")
    public String displayGroupWords(@PathVariable int groupId, Model model, HttpServletRequest request) {
        try {
            Set<Word> words = GroupLib.getGroupWords(groupId);
            model.addAttribute("words", words);
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/word_locations_in_group";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/articles/{articleId}/context/{wordId}")
    public String displayWordContextsInArticle(@PathVariable int articleId, @PathVariable int wordId,
                                               Model model, HttpServletRequest request){
        try {
            List<String> contexts = WordLib.getContextsInArticle(wordId, articleId);
            Article article = ArticleLib.getArticleById(articleId);
            Word word = WordLib.getWordById(wordId);

            model.addAttribute("contexts", contexts);
            model.addAttribute("article", article);
            model.addAttribute("word", word);
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/display_article_contexts";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/words/context/{wordId}")
    public String displayWordContexts(@PathVariable int wordId,
                                               Model model, HttpServletRequest request){
        try {
            List<ArticleWord> articleWordList = WordLib.getAllContexts(wordId);
            Word word = WordLib.getWordById(wordId);

            model.addAttribute("articleWordList", articleWordList);
            model.addAttribute("word", word);
            User user = UserLib.extractUser(request);
            model.addAttribute("user", user);

            return "words/all_contexts";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
