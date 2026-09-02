package com.project.order.dto.client;

public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String address;
    private String city;
    private String postalCode;
    private boolean enabled;

    public UserDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserDto dto = new UserDto();
        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder firstName(String firstName) { dto.setFirstName(firstName); return this; }
        public Builder lastName(String lastName) { dto.setLastName(lastName); return this; }
        public Builder email(String email) { dto.setEmail(email); return this; }
        public Builder phoneNumber(String phoneNumber) { dto.setPhoneNumber(phoneNumber); return this; }
        public Builder role(String role) { dto.setRole(role); return this; }
        public Builder address(String address) { dto.setAddress(address); return this; }
        public Builder city(String city) { dto.setCity(city); return this; }
        public Builder postalCode(String postalCode) { dto.setPostalCode(postalCode); return this; }
        public Builder enabled(boolean enabled) { dto.setEnabled(enabled); return this; }
        public UserDto build() { return dto; }
    }
}
