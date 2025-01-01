package com.example.managepurchase.classes;

import java.util.Objects;

public class User {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String providerID;

        private String userId;

        // Constructor
        public User(String name, String email, String password, String phone, String serviceProviderNumber, String userId) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.providerID = serviceProviderNumber;
            this.userId = userId;
        }

        // Default Constructor
        public User() {
        }

        // Getters
        public String getName() {
            return name;
        }
        public String getUserId() {
            return userId;
        }
        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getPhone() {
            return phone;
        }

        public String getServiceProviderNumber() {
            return providerID;
        }

        // Setters
        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setServiceProviderNumber(String serviceProviderNumber) {
            this.providerID = serviceProviderNumber;
        }

        // toString Method
        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", serviceProviderNumber='" + providerID + '\'' +
                    ", userId='" + userId + '\'' +
                    '}';
        }

        // Equals and HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(email, user.email) &&
                    Objects.equals(phone, user.phone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, phone);
        }
}
