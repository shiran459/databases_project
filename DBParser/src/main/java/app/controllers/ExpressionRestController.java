package app.controllers;

import app.lib.ExpressionLib;
import app.lib.UserLib;
import app.utils.Expression;
import app.utils.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@RestController
public class ExpressionRestController {

    @PostMapping("/expressions/add")
    public boolean addExpression(@RequestParam(name = "expressionString") String expressionString,
                           Model model, HttpServletRequest request) {
        try{
            User user = UserLib.extractUser(request);
            Expression expression = ExpressionLib.createExpression(expressionString, user.userId);
            ExpressionLib.insertExpression(expression);
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
