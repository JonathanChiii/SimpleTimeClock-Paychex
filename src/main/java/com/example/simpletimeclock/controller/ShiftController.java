package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.repository.BreakRepository;
import com.example.simpletimeclock.repository.EmployeeRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ShiftController {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    BreakRepository breakRepository;

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request){
        String username = request.getSession().getAttribute("username").toString();
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        System.out.println("Session User:" + username);
        Employee employee = employeeRepository.getReferenceById(id);
        model.addAttribute("employee", employee);
        return "index";
    }


}
