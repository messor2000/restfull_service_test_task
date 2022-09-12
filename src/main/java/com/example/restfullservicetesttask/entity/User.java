package com.example.restfullservicetesttask.entity;


import com.example.restfullservicetesttask.util.IsAfter;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private long id;

    @Email
    @NotEmpty
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Past(message = "date of birth must be less than today")
    @IsAfter(current = "1900-01-01")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;

    private String address;

    private String number;
}
