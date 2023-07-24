package com.fluxkart.assignment.repo;

import com.fluxkart.assignment.entity.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactRepo extends CrudRepository<Contact, Integer> {
    @Query("SELECT c FROM Contact c WHERE c.phoneNumber = :phoneNumber OR c.email = :email ORDER BY c.id")
    List<Contact> findByPhoneNumberOrEmail(String phoneNumber, String email);
}
