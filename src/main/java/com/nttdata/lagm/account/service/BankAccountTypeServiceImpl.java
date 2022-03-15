package com.nttdata.lagm.account.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.account.dto.request.BankAccountTypeRequestDto;
import com.nttdata.lagm.account.model.BankAccountType;
import com.nttdata.lagm.account.repository.BankAccountTypeRepository;
import com.nttdata.lagm.account.util.Constants;
import com.nttdata.lagm.account.util.Converter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankAccountTypeServiceImpl implements BankAccounTypeService {
    @Autowired
    private BankAccountTypeRepository bankAccountTypeRepository;

    @Override
    public Mono<BankAccountType> create(BankAccountTypeRequestDto bankAccountTypeRequestDto) {
        BankAccountType bankAccountType = Converter.convertToBankAccountType(bankAccountTypeRequestDto);
        return checkBankAccountTypeNotExist(bankAccountType.getId())
                .mergeWith(checkBankAccountTypeValid(bankAccountType.getId()))
                .mergeWith(checkCommision(bankAccountType))
                .then(bankAccountTypeRepository.save(bankAccountType));
    }

    @Override
    public Flux<BankAccountType> findAll() {
        return bankAccountTypeRepository.findAll();
    }

    @Override
    public Mono<BankAccountType> findById(Integer id) {
        return bankAccountTypeRepository.findById(id);
    }

    @Override
    public Mono<BankAccountType> update(BankAccountTypeRequestDto bankAccountTypeRequestDto) {
        BankAccountType bankAccountType = Converter.convertToBankAccountType(bankAccountTypeRequestDto);
        return checkBankAccountTypeNotExist(bankAccountType.getId())
                .mergeWith(checkBankAccountTypeValid(bankAccountType.getId()))
                .mergeWith(checkCommision(bankAccountType))
                .then(bankAccountTypeRepository.save(bankAccountType));
    }

    @Override
    public Mono<Void> delete(Integer id) {
        return bankAccountTypeRepository.deleteById(id);
    }

    private Mono<Void> checkBankAccountTypeValid(Integer bankAccountTypeId) {
        if (bankAccountTypeId != Constants.ID_BANK_ACCOUNT_SAVING &&
            bankAccountTypeId != Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT &&
            bankAccountTypeId != Constants.ID_BANK_ACCOUNT_FIXED_TERM) {
            return Mono.error(new Exception("Id de tipo de cuenta bancaria " + bankAccountTypeId + " no es válido"));
        }
        return Mono.empty();
    }

    private Mono<Void> checkBankAccountTypeNotExist(Integer bankAccountTypeId) {
        return bankAccountTypeRepository.findById(bankAccountTypeId)
            .flatMap(bankAccountType ->
                Mono.error(new Exception(
                        "Tipo de Cuenta bancaria con id " + bankAccountTypeId + " ya se encuentra registrado")));
    }

    private Mono<Void> checkCommision(BankAccountType bankAccountType) {
        Integer bankAccountTypeId = bankAccountType.getId();
        String strCommision = bankAccountType.getCommision();

        if (bankAccountTypeId == Constants.ID_BANK_ACCOUNT_SAVING) {
            if (strCommision == null || strCommision.isEmpty()) {
                return Mono.error(new Exception("Es requerido ingresar una comisión para la cuenta: " + bankAccountType.getDescription()));
            } else {
                try {
                    BigDecimal commision = new BigDecimal(strCommision);
                    if (commision.signum() <= 0) {
                        return Mono.error(new Exception("Debe ingresar una comisión mayor a cero para la cuenta: " + bankAccountType.getDescription()));
                    }
                } catch (Exception e) {
                    return Mono.error(new Exception("Debe ingresar una comisión válida: " + bankAccountType.getDescription()));
                }
            }
        } else if ((bankAccountTypeId == Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT ||
                    bankAccountTypeId == Constants.ID_BANK_ACCOUNT_FIXED_TERM) &&
                    strCommision != null) {
            return Mono.error(new Exception("Cuenta de tipo " + bankAccountType.getDescription() + " no admite comisiones"));
        }

        return Mono.empty();
    }
}
