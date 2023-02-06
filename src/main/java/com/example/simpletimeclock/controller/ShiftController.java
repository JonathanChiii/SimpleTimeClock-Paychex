package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.model.Break;
import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.model.Shift;
import com.example.simpletimeclock.model.utility.Status;
import com.example.simpletimeclock.repository.BreakRepository;
import com.example.simpletimeclock.repository.EmployeeRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/index")
public class ShiftController {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    BreakRepository breakRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @GetMapping("")
    public String index(Model model, HttpServletRequest request){
        if(request.getSession().getAttribute("id") != null){
            Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
            Employee employee = employeeRepository.getReferenceById(id);
            Shift shift = new Shift();
            Break theBreak = new Break();
            // If the employee is on active shift
            if(employee.getIsOnShift().equals(Status.Active)){
                // Get the shift information
                shift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Active).orElse(new Shift());
                //if the shift is on active break;
                if(shift.getIsOnBreak().equals(Status.Active)){
                    // Get the break information
                    theBreak = breakRepository.findBreakByShiftAndIsActive(shift, Status.Active).orElse(new Break());
                }else{
                    // if the shift does not have break, assign the new break
                    theBreak.setShift(shift);
                }
            }else {
                // The employee is not on an active shift, assign the new shift
                shift.setEmployee(employee);
            }
            model.addAttribute("employee", employee);
            model.addAttribute("shift", shift);
            model.addAttribute("break", theBreak);
            logger.info("Session user: " + employee.toString());
            logger.info("Shift: "+ shift.toString());
            logger.info("Break: " + theBreak.toString());
            return "index";
        } else {
            logger.info("Not authorized.");
            model.addAttribute("error", "You are not authorized to this page.");
            return "error";
        }

    }

    @GetMapping("/newshift")
    public String startShift(HttpServletRequest request){
        System.out.println("start a shift is working");
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        Employee employee = employeeRepository.getReferenceById(id);
        employee.setIsOnShift(Status.Active);
        employeeRepository.save(employee);
        Shift shift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Inactive).orElse(new Shift());
        shift.setIsActive(Status.Active);
        shift.setEmployee(employee);
        shift.setStart(new Date());
        shiftRepository.save(shift);
        logger.info("User "+ employee + " started a shift.");
        logger.info("Shift: " + shift.toString());
        return "redirect:/index";
    }


}
