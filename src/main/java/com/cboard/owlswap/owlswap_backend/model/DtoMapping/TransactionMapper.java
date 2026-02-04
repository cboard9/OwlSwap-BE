package com.cboard.owlswap.owlswap_backend.model.DtoMapping;

import com.cboard.owlswap.owlswap_backend.model.Dto.TransactionDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.owlswap.owlswap_backend.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper
{
    @Autowired
    UserMapper userMapper;
    @Autowired
    ItemToDtoFactory itemToDtoFactory;
    public TransactionDto transactionToDto(Transaction transaction) throws IllegalAccessException
    {

        return new TransactionDto(
                transaction.getTransactionId(),
                userMapper.userArchiveToDto(transaction.getBuyer()),
                userMapper.userArchiveToDto(transaction.getSeller()),
                itemToDtoFactory.toDto(transaction.getItem())

                );
    }
}
