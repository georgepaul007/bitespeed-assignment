package com.fluxkart.assignment.repo;

import com.fluxkart.assignment.entity.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactRepo extends CrudRepository<Contact, Integer> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Contact c WHERE (:phoneNumber is null OR c.phoneNumber = :phoneNumber) AND (:email is null OR c.email = :email)")
    Boolean existsByPhoneNumberAndEmail(String phoneNumber, String email);
    @Query("SELECT c FROM Contact c WHERE c.phoneNumber = :phoneNumber OR c.email = :email ORDER BY c.id")
    List<Contact> findByPhoneNumberOrEmail(String phoneNumber, String email);
}
