package com.example.mohamedahmedgomaa.restappservier.Model;

import java.util.List;

public class Request {

    private  String Phone,Name,Address, Total,status,Date,Time;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private List<Order> foods;

    public Request() {
    }

    public Request(String phone, String name, String address, String total,String date,String time, List<Order> foods) {
        this.Phone = phone;
        this.Name = name;
        this.Address = address;
        this.Total = total;
        this.foods = foods;
        this.Date = date;
        this.Time = time;
        status="0";
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
