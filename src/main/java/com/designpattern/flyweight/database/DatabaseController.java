package com.designpattern.flyweight.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 데이터베이스 연결 풀 관리 컨트롤러
 */
@Controller
@RequestMapping("/database")
public class DatabaseController {
    
    @Autowired
    private DatabaseService databaseService;
    
    @GetMapping
    public String index(Model model) {
        model.addAttribute("poolSize", databaseService.getPoolSize());
        model.addAttribute("activeConnections", databaseService.getActiveConnections());
        model.addAttribute("availableConnections", databaseService.getAvailableConnections());
        return "database/index";
    }
    
    @PostMapping("/query")
    @ResponseBody
    public String executeQuery(@RequestParam String query) {
        return databaseService.executeQuery(query);
    }
    
    @PostMapping("/close-all")
    public String closeAllConnections() {
        databaseService.closeAllConnections();
        return "redirect:/database";
    }
    
    @GetMapping("/status")
    @ResponseBody
    public String getStatus() {
        return String.format("풀 크기: %d, 활성 연결: %d, 사용 가능 연결: %d", 
                databaseService.getPoolSize(),
                databaseService.getActiveConnections(),
                databaseService.getAvailableConnections());
    }
}
