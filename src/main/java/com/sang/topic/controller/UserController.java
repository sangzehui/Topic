package com.sang.topic.controller;

import com.sang.topic.model.User;
import com.sang.topic.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arch on 2016/4/23.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    UserService userService = new UserService();

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Map<String, Object> create(User user) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean success = false;
        String message = "";
        int n = userService.getCountByUsername(user.getUsername());
        if (n == 0) {
            n = userService.insert(user);
            if (n > 0) {
                success = true;
            } else {
                message = "注册失败,原因不明";
            }
        } else {
            message = "用户名重复";
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
    }

    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    public Map<String, Object> valid(String username, String password, HttpSession httpSession) {
        Map<String, Object> resultMap = new HashMap<>();
        User u = userService.valid(username, password);
        if (u != null) {
            httpSession.setAttribute("sessionUser", u);
            resultMap.put("success", true);
        } else {
            resultMap.put("success", false);
            resultMap.put("message", "用户名或密码错误");
        }
        return resultMap;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession httpSession) {
        httpSession.invalidate();
        return new ModelAndView("redirect:/user/login");
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView editNew() {
        return new ModelAndView("user/editNew");
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Integer userId) {
        return new ModelAndView("user/show", "user", userService.get(userId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public Map<String, Object> update(@PathVariable Integer userId, User user, HttpSession httpSession) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean success = false;
        String message = "";
        User sessionUser = (User) httpSession.getAttribute("sessionUser");
        if (sessionUser != null && sessionUser.getId() == userId) {
            int n = userService.update(user);
            if (n > 0) {
                success = true;
            } else {
                message = "编辑失败";
            }
        }else{
            message = "请登录后再编辑";
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
    }

    @RequestMapping(value = "/{userId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable Integer userId) {
        return new ModelAndView("user/edit", "user", userService.get(userId));
    }
}
