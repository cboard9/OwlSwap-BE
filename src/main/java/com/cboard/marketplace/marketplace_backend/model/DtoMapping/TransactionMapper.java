package com.cboard.marketplace.marketplace_backend.model.DtoMapping;

import com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.marketplace.marketplace_backend.model.Transaction;
import com.cboard.marketplace.marketplace_common.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
