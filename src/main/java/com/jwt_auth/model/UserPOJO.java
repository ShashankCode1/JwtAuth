package com.jwt_auth.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserPOJO {

    private String username;
    private String email;
    private String password;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date lastUpdated;
}
