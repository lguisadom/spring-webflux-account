package com.nttdata.lagm.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bankAccountType")
public class BankAccountType {
    @Id
    private Integer id; // 1: saving | 2: current | 3: fixed term
    private String description;
    private String commision;
}
