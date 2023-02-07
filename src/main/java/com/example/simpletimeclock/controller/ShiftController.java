package com.example.simpletimeclock.controller;

import com.example.simpletimeclock.dto.LoginRequest;
import com.example.simpletimeclock.model.Break;
import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.model.Shift;
import com.example.simpletimeclock.model.utility.BreakType;
import com.example.simpletimeclock.model.utility.Status;
import com.example.simpletimeclock.repository.BreakRepository;
import com.example.simpletimeclock.repository.EmployeeRepository;
import com.example.simpletimeclock.repository.ShiftRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
            Employee employee = employeeRepository.findById(id).orElse(null);
            if(employee == null){
                model.addAttribute("error", "Cannot find user session.");
                logger.warn("Cannot find user session. User ID:" + id);
                return "error";
            }

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

    @GetMapping("/startshift")
    public String startShift(HttpServletRequest request, Model model){
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            model.addAttribute("error", "Cannot find user session.");
            logger.warn("Cannot find user session. User ID:" + id);
            return "error";
        }
        employee.setIsOnShift(Status.Active);
        employeeRepository.save(employee);
        Optional<Shift> existingShift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Active);
        if(existingShift.isPresent()){
            model.addAttribute("error", "The user already has an active shift.");
            logger.warn("Cannot start a shift with existing shift.");
            return "error";
        }
        Shift shift = new Shift();
        shift.setIsActive(Status.Active);
        shift.setEmployee(employee);
        shift.setStart(new Date());
        shiftRepository.save(shift);
        logger.info("User "+ employee + " started a shift.");
        logger.info("Shift: " + shift.toString());
        return "redirect:/index";
    }

    @GetMapping("/endshift")
    public String endShift(HttpServletRequest request, Model model){
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            model.addAttribute("error", "Cannot find user session.");
            logger.warn("Cannot find user session. User ID:" + id);
            return "error";
        }
        employee.setIsOnShift(Status.Inactive);
        Optional<Shift> existingShift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Active);
        if(existingShift.isEmpty()){
            model.addAttribute("error", "The user does not have an active shift.");
            logger.warn("The user has no existing shift to end.");
            return "error";
        }
        Shift shift = existingShift.get();
        // If current shift has active break, need to end the break first
        if(shift.getIsOnBreak().equals(Status.Active)) {
            Break theBreak = breakRepository.findBreakByShiftAndIsActive(shift, Status.Active).orElse(new Break());
            theBreak.setIsActive(Status.Inactive);
            theBreak.setEnd(new Date());
            Double time = getDateDiff(theBreak.getStart(), theBreak.getEnd(), TimeUnit.MINUTES) / 60.0;
            theBreak.setTotalHours(time);
            breakRepository.save(theBreak);
        }
        shift.setIsActive(Status.Inactive);
        shift.setEnd(new Date());
        Double totalShiftHours = getShiftHours(shift);
        shift.setTotalHours(totalShiftHours);
        employee.setTotalHours(employee.getTotalHours() + totalShiftHours);
        shiftRepository.save(shift);
        employeeRepository.save(employee);
        logger.info("User "+ employee + " ended a shift.");
        logger.info("Shift: " + shift.toString());
        return "redirect:/index";
    }

    @GetMapping("/startbreak")
    public String startBreak(@ModelAttribute("type") String status, HttpServletRequest request, Model model){
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            model.addAttribute("error", "Cannot find user session.");
            logger.warn("Cannot find user session. User ID:" + id);
            return "error";
        }
        if(employee.getIsOnShift().equals(Status.Inactive)){
            model.addAttribute("error", "Unable to start a break on an inactive shift.");
            logger.warn("Unable to start a break on an inactive shift. Employee: " + employee.toString());
            return "error";
        }
        Shift shift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Active).orElse(new Shift());
        if(shift.getIsOnBreak().equals(Status.Active)){
            model.addAttribute("error", "This shift already has an active break.");
            logger.warn("This shift already has an active break. " + shift.toString());
            return "error";
        }
        shift.setIsOnBreak(Status.Active);
        shiftRepository.save(shift);
        Break theBreak = new Break();
        theBreak.setShift(shift);
        if(status.equalsIgnoreCase("Lunch")){
            theBreak.setType(BreakType.Lunch);
        }
        theBreak.setStart(new Date());
        theBreak.setIsActive(Status.Active);
        breakRepository.save(theBreak);
        logger.info("User "+ employee + " started a break.");
        logger.info("Shift: " + shift.toString());
        logger.info("Break: " + theBreak.toString());
        return "redirect:/index";
    }

    @GetMapping("/endbreak")
    public String endBreak(HttpServletRequest request, Model model){
        Long id = Long.valueOf(request.getSession().getAttribute("id").toString());
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            model.addAttribute("error", "Cannot find user session.");
            logger.warn("Cannot find user session. User ID:" + id);
            return "error";
        }
        if(employee.getIsOnShift().equals(Status.Inactive)){
            model.addAttribute("error", "Unable to end a break on an inactive shift.");
            logger.warn("Unable to end a break on an inactive shift. Employee: " + employee.getUsername());
        }
        Shift shift = shiftRepository.findShiftByEmployeeAndIsActive(employee, Status.Active).orElse(new Shift());
        if(shift.getIsOnBreak().equals(Status.Inactive)){
            model.addAttribute("error", "This shift does not have an active break.");
            logger.warn("This shift does not have an active break. " + shift.toString());
            return "error";
        }
        shift.setIsOnBreak(Status.Inactive);
        shiftRepository.save(shift);
        Break theBreak = breakRepository.findBreakByShiftAndIsActive(shift, Status.Active).orElse(new Break());
        theBreak.setIsActive(Status.Inactive);
        theBreak.setEnd(new Date());
        theBreak.setTotalHours(getBreakHours(theBreak));
        breakRepository.save(theBreak);
        logger.info("User "+ employee + " ended a break.");
        logger.info("Shift: " + shift.toString());
        logger.info("Break: " + theBreak.toString());
        return "redirect:/index";
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public Long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        if(date1 == null || date2 == null) return Long.valueOf(0);
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public Double getBreakHours(Break theBreak){
        Date start = theBreak.getStart();
        Date end = theBreak.getEnd();
        return getDateDiff(start, end, TimeUnit.MINUTES)/60.0;
    }

    public Double getShiftHours(Shift shift){
        Date start = shift.getStart();
        Date end = shift.getEnd();
        Double totalBreakHours = shiftRepository.getTotalBreakHours(shift.getId());
        if(totalBreakHours == null){
            totalBreakHours = Double.valueOf(0);
        }
        Double totalShiftHours = getDateDiff(start, end, TimeUnit.MINUTES)/60.0;
        return totalShiftHours - totalBreakHours;
    }
}
