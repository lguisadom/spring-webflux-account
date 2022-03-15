package com.nttdata.lagm.account.dto.request;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class BankAccountTypeRequestDto {
    private Integer id; // 1: saving | 2: current | 3: fixed term
    @NotEmpty(message = "Debe ingresar un valor para el campo descripcion")
    private String description;
    private String commision;
}
