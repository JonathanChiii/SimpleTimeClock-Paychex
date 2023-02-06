package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.dto.LoginRequest;
import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.repository.EmployeeRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeRepository employeeRepository;
    // Skipping the Service layer for simplicity purposes...

    @GetMapping("/new")
    public String newEmployee(Model model){
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "addEmployee";
    }
    @PostMapping("/new")
    public String saveEmployee(@ModelAttribute("employee") Employee employee, Model model){
        Employee e = employeeRepository.save(employee);
        model.addAttribute("employee", e);
        return "postRegister";
    }

    // Session Management
    // https://www.javainuse.com/spring/springboot_session
    @GetMapping("/login")
    public String loginPage(@RequestParam Optional<String> id, HttpSession session, Model model){
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("SESSION_MESSAGES");
        if(messages == null){ messages = new ArrayList<>();}
        model.addAttribute("sessionMessages", messages);

        LoginRequest dto = new LoginRequest();
        if(id.isPresent()){
            dto.setId(id.get());
        }
        model.addAttribute("employee", dto);

        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("employee")LoginRequest loginRequest, HttpServletRequest request){
        Long id = Long.valueOf(loginRequest.getId());
        Employee employee = employeeRepository.getReferenceById(id);
        System.out.println("request username");
        System.out.println(request.getAttribute("username"));
        System.out.println(request.getAttribute("password"));
        if(employee == null || !employee.getPassword().equals(loginRequest.getPassword())){
            return "error";
        }
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.getSession().getAttribute("MY_SESSION_MESSAGES");
        if (messages == null) {
            messages = new ArrayList<>();
            request.getSession().setAttribute("username", employee.getUsername());
            request.getSession().setAttribute("id", employee.getId());
        }
        messages.add(employee.toString());
        System.out.println(employee.toString());
        request.getSession().setAttribute("SESSION_MESSAGES", messages);
        return "redirect:/index";
    }

}
