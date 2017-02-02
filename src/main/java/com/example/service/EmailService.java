package com.example.service;

import com.example.domain.Email;
import com.example.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Implementation for managing Email.
 */
@Service
@Transactional
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    /**
     * Save a email.
     *
     * @param email the entity to save
     * @return the persisted entity
     */
    public Email save(Email email) {
        log.debug("Request to save Email : {}", email);
        Email result = emailRepository.save(email);
        return result;
    }

    /**
     *  Get all the emails.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Email> findAll() {
        log.debug("Request to get all Emails");
        List<Email> result = emailRepository.findAll();

        return result;
    }

    /**
     *  Get one email by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Email findOne(Long id) {
        log.debug("Request to get Email : {}", id);
        Email email = emailRepository.findOne(id);
        return email;
    }

    /**
     *  Delete the  email by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Email : {}", id);
        emailRepository.delete(id);
    }
}
