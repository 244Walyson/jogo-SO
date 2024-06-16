package com.waly.so.game.controllers;

import com.waly.so.game.services.OperatingSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
public class ProcessController {

    @Autowired
    private OperatingSystemService osService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("processes", osService.getProcesses());
        return "index";
    }

    @PostMapping("/create")
    public Mono<String> createProcess() {
        return osService.createProcess().then(Mono.just("redirect:/"));
    }

    @PostMapping("/execute")
    public Mono<String> executeProcess() {
        return osService.executeProcess().then(Mono.just("redirect:/"));
    }

    @PostMapping("/block")
    public Mono<String> blockProcess() {
        return osService.blockProcess().then(Mono.just("redirect:/"));
    }

    @PostMapping("/release")
    public Mono<String> releaseProcess() {
        return osService.releaseProcess().then(Mono.just("redirect:/"));
    }
}
