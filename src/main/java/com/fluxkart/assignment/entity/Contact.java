package com.fluxkart.assignment.entity;

import com.fluxkart.assignment.enums.LinkPrecedence;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "linked_id")
    private Integer linkedId;

    @Column(name = "link_precedence")
    private LinkPrecedence linkPrecedence;

    @Column(name = "created_date")
    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Date updatedDate;

    @Column(name = "deleted_date")
    private Date deletedDate;

}
