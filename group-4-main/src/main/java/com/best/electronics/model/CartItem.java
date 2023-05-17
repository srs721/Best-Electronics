package com.best.electronics.model;

public class CartItem {

    private String identifier;
    private Integer cartItemId;
    private Integer quantity;
    private Integer userId;

    public CartItem()
    {

    }
    public CartItem(Integer cartItemId, String identifier, Integer quantity, Integer userId){
        this.cartItemId = cartItemId;
        this.userId = userId;
        this.identifier = identifier;
        this.quantity = quantity;
    }

    public CartItem(Integer cartItemId, String identifier, Integer userId){
        this.cartItemId = cartItemId;
        this.userId = userId;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
