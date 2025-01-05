package com.example.managepurchase.classes;

public class Appointment {
        private User user;
        private String date;
        private String time;

        // Constructor
        public Appointment(User user, String date, String time) {
            this.user = user;
            this.date = date;
            this.time = time;
            }
        public Appointment() {
            this.user = new User();
            this.date = "";
            this.time = "";
        }


        // Getters
        public User getUser() {
            return user;
            }

        public String getClientName(){
            return user.getName();
        }
        public String getClientPhone(){
            return user.getPhone();
        }
        public boolean isAvailable() {
            return true;
        }
        public String getDate() {
            return date;
            }

            public String getTime() {
            return time;
            }

            public String getFormattedDate() {
            return date + " " + time;
            }

            public String getFormattedTime() {
            return time;
            }
            public void setUser(User user){
            this.user=user;
            }
            public void setDate(String date){
            this.date=date;
            }

}
