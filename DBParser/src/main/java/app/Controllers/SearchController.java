package app.Controllers;

import app.lib.ArticleLib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;

@Controller
public class SearchController {

    @GetMapping("/search_word")
    public String searchWord(@RequestParam(name="word", required=true) String word,
                             Model model) {
        try {
            List<Integer> articleIdList = ArticleLib.getArticlesByWord(word);
            model.addAttribute("articleIdList", articleIdList);
            return "search_word";
        } catch (Exception e){
            return "error";
        }
    }

}
