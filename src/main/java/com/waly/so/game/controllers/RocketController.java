package com.waly.so.game.controllers;

import com.waly.so.game.models.Task;
import com.waly.so.game.services.ThreadManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RocketController {

    @Autowired
    private ThreadManagerService threadManagerService;

    @GetMapping("/rocket")
    public String index(Model model) {
        model.addAttribute("taskList", threadManagerService.getTaskList());
        return "rocket";
    }

    @PostMapping("/add")
    public String addTask() {
        threadManagerService.addTask();
        return "redirect:/";
    }
}
