package com.cboard.marketplace.marketplace_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "transaction")
public class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transaction_id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id")
    private UserArchive buyer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private UserArchive seller;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;
    //private String paymentType;
    //private double cost;

    public Transaction() {
    }

    public Transaction(int transaction_id, UserArchive buyer, UserArchive seller, Item item) {
        this.transaction_id = transaction_id;
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }
    /*    public Transaction(int transaction_id, UserArchive buyer, UserArchive seller, String paymentType, double cost) {
        this.transaction_id = transaction_id;
        this.buyer = buyer;
        this.seller = seller;
        this.paymentType = paymentType;
        this.cost = cost;
    }*/

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public UserArchive getBuyer() {
        return buyer;
    }

    public void setBuyer(UserArchive buyer) {
        this.buyer = buyer;
    }

    public UserArchive getSeller() {
        return seller;
    }

    public void setSeller(UserArchive seller) {
        this.seller = seller;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    /*    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }*/
}
