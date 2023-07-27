package com.fluxkart.assignment.services.impl;

import com.fluxkart.assignment.dto.IdentifyDto;
import com.fluxkart.assignment.entity.Contact;
import com.fluxkart.assignment.enums.LinkPrecedence;
import com.fluxkart.assignment.repo.ContactRepo;
import com.fluxkart.assignment.services.ContactService;
import com.fluxkart.assignment.webmodel.IdentifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    ContactRepo contactRepo;
    public IdentifyResponse identify(IdentifyDto identifyDto) {
        List<Contact> contactList = contactRepo.findByPhoneNumberOrEmail(identifyDto.getPhone(), identifyDto.getEmail());

        if (contactList.isEmpty()) {
            Contact newContact = contactRepo.save(Contact.builder()
                    .phoneNumber(identifyDto.getPhone())
                    .email(identifyDto.getEmail())
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .linkPrecedence(LinkPrecedence.PRIMARY)
                    .build());

            return buildIdentifyResponse(newContact, Collections.emptyList());
        }

        OptionalInt secondPrimaryIndex = IntStream.range(0, contactList.size())
                .filter(i -> LinkPrecedence.PRIMARY.equals(contactList.get(i).getLinkPrecedence()))
                .skip(1)
                .findFirst();


        if(secondPrimaryIndex.isPresent()) {
            contactList.get(secondPrimaryIndex.getAsInt()).setLinkPrecedence(LinkPrecedence.SECONDARY);
            contactList.get(secondPrimaryIndex.getAsInt()).setLinkedId(contactList.get(0).getId());
            contactRepo.save(contactList.get(secondPrimaryIndex.getAsInt()));
            return buildIdentifyResponse(contactList.get(0), contactList.subList(1, contactList.size()));
        }

        Optional<Contact> existingContact = contactList.stream()
                .filter(c -> {
                    String phoneNumber = c.getPhoneNumber();
                    String email = c.getEmail();
                    return Optional.ofNullable(phoneNumber).equals(Optional.ofNullable(identifyDto.getPhone())) &&
                            Optional.ofNullable(email).equals(Optional.ofNullable(identifyDto.getEmail()));
                })
                .findFirst();


        if (existingContact.isEmpty()) {
            Contact newSecondaryContact = contactRepo.save(Contact.builder()
                    .phoneNumber(identifyDto.getPhone())
                    .email(identifyDto.getEmail())
                    .updatedDate(new Date())
                    .linkedId(contactList.get(0).getId())
                    .linkPrecedence(LinkPrecedence.SECONDARY)
                    .build());
            contactList.add(newSecondaryContact);
        }
        if(contactList.get(0).getLinkPrecedence().name().equals(LinkPrecedence.SECONDARY.name())) {
            Optional<Contact> newPrimarySuspectContact = contactRepo.findById(contactList.get(0).getLinkedId());
            while(newPrimarySuspectContact.get().getLinkPrecedence().name().equals(LinkPrecedence.SECONDARY.name())) {
                newPrimarySuspectContact = contactRepo.findById(newPrimarySuspectContact.get().getLinkedId());
            }
            return buildIdentifyResponse(newPrimarySuspectContact.get(), contactList);
        }
        return buildIdentifyResponse(contactList.get(0), contactList.subList(1, contactList.size()));
    }
    private IdentifyResponse buildIdentifyResponse(Contact primaryContact, List<Contact> secondaryContacts) {
        Set<String> uniqueEmails = new HashSet<>();
        Set<String> uniquePhoneNumbers = new HashSet<>();
        Set<Integer> secondaryContactIds = new HashSet<>();
        if(primaryContact.getEmail() != null) {
            uniqueEmails.add(primaryContact.getEmail());
        }
        if(primaryContact.getPhoneNumber() != null) {
            uniquePhoneNumbers.add(primaryContact.getPhoneNumber());
        }

        secondaryContacts.forEach(contact -> {
            if(contact.getEmail() != null) {
                uniqueEmails.add(contact.getEmail());
            }
            if(contact.getPhoneNumber() != null) {
                uniquePhoneNumbers.add(contact.getPhoneNumber());
            }
            secondaryContactIds.add(contact.getId());
        });

        return IdentifyResponse.builder()
                .contact(
                        IdentifyResponse.Contact.builder()
                                .primaryContactId(primaryContact.getId())
                                .emails(new ArrayList<>(uniqueEmails))
                                .phoneNumbers(new ArrayList<>(uniquePhoneNumbers))
                                .secondaryContactIds(new ArrayList<>(secondaryContactIds))
                                .build()
                )
                .build();
    }
}
