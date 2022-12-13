package com.michaeladrummonds.aguafina.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.michaeladrummonds.aguafina.config.AuthenticatedUserService;
import com.michaeladrummonds.aguafina.models.Customer;
import com.michaeladrummonds.aguafina.models.Employee;
import com.michaeladrummonds.aguafina.models.User;
import com.michaeladrummonds.aguafina.models.dto.CustomerDto;
import com.michaeladrummonds.aguafina.repository.UserRepository;
import com.michaeladrummonds.aguafina.service.impl.CustomerServiceImpl;
import com.michaeladrummonds.aguafina.service.impl.EmployeeServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private AuthenticatedUserService authService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/customers")
    public ModelAndView listCustomers(@RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView mav = new ModelAndView("customers");

        User user = authService.getCurrentUser();

        Employee employee = employeeService.getEmployeeByEmail(user.getEmail());

        mav.addObject("employee", employee);

        if (keyword != null) {
            List<Customer> customers = customerService.getCustomerByKeyword(keyword);
            mav.addObject("customers", customers);
            mav.addObject("keyword", keyword);
            log.debug("There were " + customers.size() + " customers found with keyword: " + keyword);
        } else {
            List<Customer> customers = customerService.getAllCustomers();
            mav.addObject("customers", customers);
            mav.addObject("keyword", keyword);
            log.debug("There are " + customers.size() + " current customers.");
        }

        return mav;

    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/orders#add-new-customer")
    public String addNewCustomer(Model model) {
        CustomerDto customerDto = new CustomerDto();
        model.addAttribute("customer", customerDto);
        return "orders#add-new-customer";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/orders#add-new-customer")
    public String saveNewCustomer(Model model, @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
            BindingResult bindingResult) {
        for (ObjectError e : bindingResult.getAllErrors()) {
            log.debug(e.getDefaultMessage());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("customerDto", customerDto);
            return "orders#add-new-customer";
        } else {
            Customer customer = new Customer();
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setEmail(customerDto.getEmail());
            customer.setPhone(customerDto.getPhone());

            customerService.saveCustomer(customer);
        }

        log.debug("Customer created successfully");
        return "create_order";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE','CUSTOMER')")
    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Integer id, Model model) {
        User user = authService.getCurrentUser();

        Employee employee = employeeService.getEmployeeByEmail(user.getEmail());
        Customer customer = customerService.getCustomerById(id);

        try {
            if (user.getEmail().equals(customer.getEmail()) && employee == null) {
                model.addAttribute("customer", customer);
            } else if (user.getEmail().equals(employee.getEmail())) {
                model.addAttribute("customer", customer);
                model.addAttribute("employee", employee);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
        return "edit_customer";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE','CUSTOMER')")
    @PostMapping("/customers/{id}")
    public String updateCustomer(Model model, @PathVariable Integer id,
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult bindingResult) {

        for (ObjectError e : bindingResult.getAllErrors()) {
            log.debug(e.getDefaultMessage());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("customer", customer);
            return "edit_customer";
        } else {
            Customer existingCustomer = customerService.getCustomerById(id);
            existingCustomer.setId(customer.getId());
            existingCustomer.setFirstName(customer.getFirstName());
            existingCustomer.setLastName(customer.getLastName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setCity(customer.getCity());
            existingCustomer.setState(customer.getState());
            existingCustomer.setZipCode(customer.getZipCode());

            customerService.updateCustomer(existingCustomer);

            User user = authService.getCurrentUser();
            if (user.getEmail().equals(customer.getEmail())) {
                user.setEmail(customer.getEmail());
                user.setFirstName(customer.getFirstName());
                user.setLastName(customer.getLastName());
                userRepository.save(user);
            }

            log.debug(
                    existingCustomer.getFirstName() + " " + existingCustomer.getLastName() + " has just been updated.");
            return "redirect:/home";
        }
    }
}
