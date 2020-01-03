package app.controllers;

import app.lib.GroupLib;
import app.lib.UserLib;
import app.utils.User;
import app.utils.WordGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Set;

@Controller
public class GroupsController {

    @GetMapping("/groups/view")
    public String displayGroups(Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        try{
            Set<WordGroup> groups = GroupLib.getGroupsByUser(user.userId);
            model.addAttribute("user", user);
            model.addAttribute("groups", groups);
            return "groups/display_groups";
        } catch(SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/groups/create")
    public String viewCreatePage(Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

        return "groups/create_group";
    }

    @GetMapping("/groups/create/submit")
    public String createGroup(@RequestParam(name = "groupName") String groupName,
                              Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        try{
            WordGroup group = GroupLib.createGroup(groupName, user.userId);
            model.addAttribute("user", user);
            model.addAttribute("group", group);
            model.addAttribute("groupName", groupName);
            return "groups/group_creation_result";
        } catch(SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
