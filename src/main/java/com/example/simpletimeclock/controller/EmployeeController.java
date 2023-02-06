package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.dto.LoginRequest;
import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.repository.EmployeeRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/login")
    public String loginPage(@RequestParam Optional<String> id, Model model){
        LoginRequest dto = new LoginRequest();
        if(id.isPresent()){
            dto.setId(id.get());
        }
        model.addAttribute("employee", dto);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("employee")LoginRequest loginRequest){
        Long id;
//        try{
//            id = Long.valueOf(loginRequest.getId());
//            System.out.println(loginRequest.getId());
//        }catch(Exception e){
//            System.out.println("Exception occured");
//            return "error";
//        }
        // need to grant http session
        return "redirect:/index";
    }

}
