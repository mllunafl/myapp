package com.example.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.domain.Address;
import com.example.domain.Email;
import com.example.domain.Person;
import com.example.domain.enumeration.Gender;
import com.example.service.PersonService;

@Controller
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonService personService;

    /**
     * Get a list of all the people. The person does not have references to 
     * its associated address or emails 
     */
    @GetMapping("/persons")
    public String getAll(Model model) {
        model.addAttribute("persons", personService.findAll());
        return "persons";
    }

    /**
     * Adds an empty Person to the model and returns to the web page for the 
     * user to add a new person
     */
    @GetMapping("/person")
    public String newPerson(Model model) {
        model.addAttribute("person", new Person());
        return "new-person";
    }

    /**
     * The post from the add person web page. Calls the person service to add the 
     * person and then returns the list of all people.
     * <p/> 
     * Uses the old school parameters instead of having spring bind to a Person object
     */
    @PostMapping("/person")
    public String addPerson(@RequestParam(value="name", required=true) String name,
            @RequestParam(value="dob", required=true) String dob,
            @RequestParam(value="gender", required=true) String gender,
            Model model) {
        Person person = new Person();
        person.setName(name);
        if(!StringUtils.isBlank(gender)){
            Gender genderEnum = Gender.valueOf(gender);
            person.setGender(genderEnum);        		
        };
        person.setDob(LocalDate.parse(dob));
        personService.save(person);

        model.addAttribute("persons", personService.findAll());
        return "persons";
    }

    /*
     * Calls the person service to get the person for the given id.
     * <p/>
     * Also has an optional action parameter  
     * <ul>
     *   <li>edit - returns to the edit web page</li>
     *   <li>delete - calls the person service to delete and returns to the list of people page</li>
     *   <li>&lt;empty$gt; - returns to the read-only view page</li>
     * </ul>
     */
    @GetMapping("/person/{id}")
    public String viewEditDeletePerson(@PathVariable("id") Long id, 
            @RequestParam(value="action", required=false) String action,
            Model model) {
        Person person = personService.findOne(id);
        model.addAttribute("person", person);
        if ("edit".equals(action)) {
            return "edit-person";
        } else if ("delete".equals(action)) {
            personService.delete(id);
            model.addAttribute("persons", personService.findAll());
            return "persons";
        } else {
            return "view-person";
        }
    }

    /**
     * This endpoint is called when updating. Returns to the read-only web page.
     */
    @PostMapping("/person/{id}")
    public String updatePerson(@PathVariable("id") Long id,
            Person person,
            Model model) {
        personService.update(person);
        model.addAttribute("person", personService.findOne(id));
        return "view-person";
    }

    /**
     * Adds an empty Address to the model and returns to the web page for the 
     * user to add a new address
     */
    @GetMapping("/person/{id}/address")
    public String newAddress(@PathVariable("id") Long id, Model model) {
        model.addAttribute("person", personService.findOne(id));
        model.addAttribute("address", new Address());
        return "new-address";
    }

    /**
     * The post from the add address web page. Calls the person service to add the 
     * address and then returns the read-only person view
     */
    @PostMapping("/person/{id}/address")
    public String addAddress(@PathVariable("id") Long id, 
            Address address, Model model) {
        Person person = personService.findOne(id);
        address.setPerson(person);
        person = personService.updateAddress(address);
        model.addAttribute("person", person);
        return "view-person";
    }

    /**
     * Deletes the address from the person. The url has both ids.
     */
    @GetMapping("/person/{id}/address/{addressId}")
    public String deleteAddress(@PathVariable("id") Long id,
            @PathVariable("addressId") Long addressId,
            Address address, Model model) {
        Person person = personService.deleteAddress(id, addressId);
        model.addAttribute("person", person);
        return "view-person";
    }

    /**
     * Adds an empty Email to the model and returns to the web page for the 
     * user to add a new email
     */
    @GetMapping("/person/{id}/email")
    public String newEmail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("person", personService.findOne(id));
        model.addAttribute("email", new Email());
        return "new-email";
    }

    /**
     * The post from the add email web page. Calls the person service to add the 
     * email and then returns the read-only person view
     */
    @PostMapping("/person/{id}/email")
    public String addEmail(@PathVariable("id") Long id, 
            @RequestParam(value="email", required=true) String emailAddress,
            Model model) {
        Email email = new Email();
        email.setEmail(emailAddress);
        Person person = personService.findOne(id);
        email.setPerson(person);
        person = personService.addEmail(email);
        model.addAttribute("person", personService.findOne(id));
        return "view-person";
    }

    /**
     * Deletes the email from the person. The url has both ids.
     */
    @GetMapping("/person/{id}/email/{emailId}")
    public String deleteEmail(@PathVariable("id") Long id,
            @PathVariable("emailId") Integer emailId,
            Address address, Model model) {
        personService.deleteEmail(id, emailId);
        model.addAttribute("person", personService.findOne(id));
        return "view-person";
    }


    /**
     * If any Exceptions are thrown this method will be run. The method logs
     * the exeception stack trace and show the exception message in the 
     * error web page.
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleDefaultErrors(final Exception exception, final HttpServletRequest request, final HttpServletResponse resp) {
        log.warn(exception.getMessage() + "\n" + stackTraceAsString(exception));
        return new ModelAndView("error", "message", exception.getMessage());
    }

    private String stackTraceAsString(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
}

