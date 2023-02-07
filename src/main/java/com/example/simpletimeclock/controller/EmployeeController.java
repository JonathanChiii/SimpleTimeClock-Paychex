package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.dto.LoginRequest;
import com.example.simpletimeclock.dto.SignupRequest;
import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeRepository employeeRepository;
    // Skipping the Service layer for simplicity purposes...

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @GetMapping("/new")
    public String newEmployee(Model model){
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "signup";
    }
    @PostMapping("/new")
    public String saveEmployee(@ModelAttribute("employee") @Valid SignupRequest employeeDTO, Model model){
        String username = employeeDTO.getUsername();
        Optional<Employee> existingEmployee = employeeRepository.findByUsername(username);
        if(existingEmployee.isPresent()){
            logger.warn("Username: " + username + " is already taken.");
            model.addAttribute("error", "Username: " + username + " is already taken.");
            return "error";
        }
        Employee employee = new Employee();
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(employeeDTO.getPassword());
        model.addAttribute("employee", employeeRepository.save(employee));
        logger.info("Username: " + username + " is saved.");
        return "postRegister";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam Optional<String> id, Model model){
        model.addAttribute("id", id.orElse(""));
        return "login";
    }

    // Session Management
    // https://www.javainuse.com/spring/springboot_session
    // https://stackoverflow.com/questions/8105032/how-to-retrieve-user-session-attributes-like-username
    @PostMapping("/login")
    public String login(@ModelAttribute("employee") LoginRequest loginRequest, Model model, HttpServletRequest request){
        Long id;
        try{
            id = Long.valueOf(loginRequest.getId());
        }catch(Exception e){
            logger.info("ID must be integer value.");
            model.addAttribute("error", "ID must be integer value.");
            return "error";
        }
        Employee employee = employeeRepository.getReferenceById(id);
        if(employee == null || !employee.getPassword().equals(loginRequest.getPassword())){
            logger.info("ID: " + loginRequest.getId() + "and password: " + loginRequest.getPassword() + " do not match any records.");
            model.addAttribute("error", "This ID and password do not match any records.");
            return "error";
        }
        // If Authentication is successful, grant a Http Session to client
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.getSession().getAttribute("MY_SESSION_MESSAGES");
        if (messages == null) {
            messages = new ArrayList<>();
            request.getSession().setAttribute("MY_SESSION_MESSAGES", messages);
        }
        request.getSession().setAttribute("id", employee.getId());
        request.getSession().setAttribute("username", employee.getUsername());
        request.getSession().setAttribute("password", employee.getPassword());
        logger.info("Session user is: " + employee.toString());
        return "redirect:/index";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/employee/login";
    }
}
