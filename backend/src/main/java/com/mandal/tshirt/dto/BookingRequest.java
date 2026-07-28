package com.mandal.tshirt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BookingRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "T-shirt size is required")
    @Pattern(regexp = "^(28|30|32|34|36|38|40|42|44|46|48|50)$",
             message = "Size must be a valid chest size in inches (e.g. 38, 40, 42)")
    private String size;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Enter a valid 10 digit phone number")
    private String phoneNumber;

    @NotBlank(message = "Sleeve type is required")
    @Pattern(regexp = "^(Half Sleeve|Full Sleeve)$", message = "Invalid sleeve type")
    private String sleeveType;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSleeveType() { return sleeveType; }
    public void setSleeveType(String sleeveType) { this.sleeveType = sleeveType; }
}
