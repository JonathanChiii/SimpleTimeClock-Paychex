package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.repository.BreakRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShiftController {
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    BreakRepository breakRepository;

    @GetMapping("/index")
    public String index(){
        return "index";
    }


}
