package com.cboard.owlswap.owlswap_backend.model.Dto;

public class TransactionDto
{
    private int transactionId;
    private UserDto buyer;
    private UserDto seller;
    private ItemDto item;

    public TransactionDto() {
    }

    public TransactionDto(int transactionId, UserDto buyer, UserDto seller, ItemDto item) {
        this.transactionId = transactionId;
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public UserDto getBuyer() {
        return buyer;
    }

    public void setBuyer(UserDto buyer) {
        this.buyer = buyer;
    }

    public UserDto getSeller() {
        return seller;
    }

    public void setSeller(UserDto seller) {
        this.seller = seller;
    }

    public ItemDto getItem() {
        return item;
    }

    public void setItem(ItemDto item) {
        this.item = item;
    }
}
