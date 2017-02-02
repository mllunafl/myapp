package com.example.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Address;
import com.example.domain.Email;
import com.example.domain.Person;
import com.example.repository.AddressRepository;
import com.example.repository.EmailRepository;
import com.example.repository.PersonRepository;

/**
 * Service Implementation for managing Person.
 */
@Service
@Transactional
public class PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonService.class);
    
    @Inject
    private PersonRepository personRepository;

    @Inject
    private EmailRepository emailRepository;
    
    @Inject
    private AddressRepository addressRepository;


    
//    public PersonService(PersonRepository personRepository, EmailRepository emailRepository,) {
//        this.personRepository = personRepository;
//    }
    

    /**
     * Save a person.
     *
     * @param person the entity to save
     * @return the persisted entity
     */
    public Person save(Person person) {
        log.debug("Request to save Person : {}", person);
        Person result = personRepository.save(person);
        return getPerson(person.getId());
    }

    /**
     *  Get all the people.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        log.debug("Request to get all People");
        List<Person> result = personRepository.findAll();

        return result;
    }


    /**
     *  get all the people where Address is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Person> findAllWhereAddressIsNull() {
        log.debug("Request to get all people where Address is null");
        return StreamSupport
            .stream(personRepository.findAll().spliterator(), false)
            .filter(person -> person.getAddress() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get one person by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Person findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        Person person = personRepository.findOne(id);
        return getPerson(id);
    }
    
    @Transactional
    public void update(Person person) {
        personRepository.save(person);
    }

    /**
     *  Delete the  person by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);
        Person person = this.getPerson(id);
        emailRepository.delete(person.getEmails());
        if (person.getAddress() != null) {
            addressRepository.delete(person.getAddress());
        }
        personRepository.delete(id);
    }
    
    @Transactional
    public Person updateAddress(Address address) {
        addressRepository.save(address);
        return getPerson(address.getPerson().getId());
    }

    @Transactional
    public Person deleteAddress(long personId, long addressId) {
        addressRepository.delete(addressId);
        Person person = personRepository.findOne(personId);
        person.setAddress(null);
        personRepository.save(person);

        return getPerson(personId);
    }

    @Transactional
    public Person addEmail(Email email) {
        emailRepository.save(email);
        
        Person person = personRepository.findOne(email.getPerson().getId());
        person.getEmails().add(email);
        personRepository.save(person);
        
        return getPerson(email.getPerson().getId());
    }

    @Transactional
    public Person deleteEmail(long personId, long emailId) {
        Email email = emailRepository.findOne(emailId);
        emailRepository.delete(emailId);
        
        Person person = personRepository.findOne(personId);
        person.getEmails().remove(email);
        personRepository.save(person);

        return getPerson(personId);
    }
    
    private Person getPerson(long id) {
        Person person = personRepository.findOne(id);
        // For the lazy to load
        person.getEmails().size();
        return person;
    }
}
