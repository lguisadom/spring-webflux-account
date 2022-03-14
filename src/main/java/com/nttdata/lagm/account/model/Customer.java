package com.nttdata.lagm.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    private String id;
    private String lastName;
    private String firstName;
    private String dni;
    private String phone;
    private String email;
    private Integer customerTypeId;
    private Integer customerProfileId;
}
