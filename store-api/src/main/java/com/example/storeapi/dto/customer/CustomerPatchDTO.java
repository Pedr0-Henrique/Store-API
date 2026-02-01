package com.example.storeapi.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class CustomerPatchDTO {
    @Size(max = 120)
    private String name;

    @Email
    @Size(max = 180)
    private String email;

    @Size(max = 20)
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
