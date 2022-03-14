package com.nttdata.lagm.account.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Credit extends BankProduct {
	private String creditLimit;

}