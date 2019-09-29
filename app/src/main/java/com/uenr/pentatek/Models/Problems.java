package com.uenr.pentatek.Models;

public class Problems {

    public String customer_id;
    public String f_name;
    public String l_name;
    public String email;
    public String phone_number;

    public Problems(){

    }

    public Problems(String customer_id,String f_name, String l_name, String email, String phone_number) {

        this.customer_id = customer_id;
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.phone_number = phone_number;
    }


    public String getCustomer_id(){return customer_id; }

    public String getF_name(){return f_name; }

    public String getL_name(){return l_name; }

    public String getEmail(){return email; }

    public String getPhone_number(){return phone_number; }
}
