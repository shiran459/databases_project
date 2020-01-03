package app.controllers;

import app.lib.GroupLib;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@RestController
public class GroupsRestController {

    @PostMapping("/groups/add_word")
    public boolean addWord(@RequestParam(name = "groupId") int groupId,
                          @RequestParam(name = "wordId") int wordId,
                          Model model, HttpServletRequest request) {
        try{
            GroupLib.addWord(groupId,wordId);
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
