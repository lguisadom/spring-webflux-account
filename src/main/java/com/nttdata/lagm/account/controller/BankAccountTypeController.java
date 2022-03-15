package com.nttdata.lagm.account.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.lagm.account.dto.request.BankAccountTypeRequestDto;
import com.nttdata.lagm.account.model.BankAccountType;
import com.nttdata.lagm.account.service.BankAccounTypeService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bankAccountType")
public class BankAccountTypeController {

    @Autowired
    private BankAccounTypeService bankAccounTypeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private Mono<BankAccountType> create(@Valid @RequestBody BankAccountTypeRequestDto bankAccountTypeRequestDto) {
        return bankAccounTypeService.create(bankAccountTypeRequestDto);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private Flux<BankAccountType> findAll() {
        return bankAccounTypeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private Mono<BankAccountType> findById(@PathVariable("id") Integer id) {
        return bankAccounTypeService.findById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    private Mono<BankAccountType> update(@Valid @RequestBody BankAccountTypeRequestDto bankAccountTypeRequestDto) {
        return bankAccounTypeService.update(bankAccountTypeRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private Mono<Void> delete(@PathVariable("id") Integer id) {
        return bankAccounTypeService.delete(id);
    }
}
