package com.cboard.owlswap.owlswap_backend.model;

import com.cboard.owlswap.owlswap_backend.model.orders.ListingStatus;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//entity maps this saying this is a table

@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;
    @NotNull(message = "Name is required...")
    private String name;
    String description;
    @NotNull(message = "Price is required...")
    private Double price;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    private String releaseDate;
    private boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;
    private String itemType;
    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("imageId ASC")
    private List<ItemImage> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false)
    private ListingStatus listingStatus = ListingStatus.AVAILABLE;

    @Column(name = "reserved_until")
    private java.time.LocalDateTime reservedUntil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by_order_id")
    private Order reservedByOrder;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;



    public Item() {
    }

/*    public Item(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, String image_name, String image_type, byte[] image_date) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.user = user;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        this.location = location;
        this.itemType = itemType;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }*/

    public Item(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, List<ItemImage> images) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.user = user;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        this.location = location;
        this.itemType = itemType;
        this.images = images;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }


    public List<ItemImage> getImages() {
        return images;
    }

    public void setImages(List<ItemImage> images) {
        this.images = images;
    }

    public void addImage(ItemImage image)
    {
        if(image == null) return;
        images.add(image);
        image.setItem(this);
    }

    public void removeImage(ItemImage image)
    {
        if(image == null) return;
        images.remove(image);
        image.setItem(null);
    }

    public ListingStatus getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(ListingStatus listingStatus) {
        this.listingStatus = listingStatus;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public Order getReservedByOrder() {
        return reservedByOrder;
    }

    public void setReservedByOrder(Order reservedByOrder) {
        this.reservedByOrder = reservedByOrder;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
