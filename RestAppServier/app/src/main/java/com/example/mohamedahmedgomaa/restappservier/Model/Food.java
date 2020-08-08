package com.example.mohamedahmedgomaa.restappservier.Model;

public class Food {
    private  String  Name;
    private  String  image;
    private  String  description;
    private  String  price;
    private  String  discount;
    private  String  menuId;

    public Food() {
    }

    public Food(String name, String image, String description, String price, String discount, String menuId) {
        Name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.menuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
