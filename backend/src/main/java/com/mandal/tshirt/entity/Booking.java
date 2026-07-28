package com.mandal.tshirt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String size;
    private String sleeveType;
    private String phoneNumber;
    
    private LocalDateTime bookedOn;

    @PrePersist
    protected void onCreate() {
        if (bookedOn == null) {
            bookedOn = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getSleeveType() { return sleeveType; }
    public void setSleeveType(String sleeveType) { this.sleeveType = sleeveType; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDateTime getBookedOn() { return bookedOn; }
    public void setBookedOn(LocalDateTime bookedOn) { this.bookedOn = bookedOn; }
}
