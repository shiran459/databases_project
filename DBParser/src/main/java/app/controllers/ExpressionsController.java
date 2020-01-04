package app.controllers;

import app.lib.ExpressionLib;
import app.lib.UserLib;
import app.utils.Article;
import app.utils.Expression;
import app.utils.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
public class ExpressionsController {

    @GetMapping("/expressions/view")
    public static String displayExpressions(Model model, HttpServletRequest request) {
        try {
            User user = UserLib.extractUser(request);
            List<Expression> expressions = ExpressionLib.getExpressions(user.userId);

            model.addAttribute("user", user);
            model.addAttribute("expressions", expressions);
            return "expressions/display_expressions";
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/expressions/{expressionId}/locations")
    public static String displayExpressionLocations(@PathVariable("expressionId") int expressionId, Model model, HttpServletRequest request) {
        try {
            User user = UserLib.extractUser(request);
            Map<Article, List<Integer>> articleMap = ExpressionLib.searchExpressionInDB(expressionId);
            model.addAttribute("articleMap", articleMap);
            return "expressions/expression_locations";
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/expressions/create")
    public static String createExpression(Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);
        return "expressions/create_expression";
    }
}
