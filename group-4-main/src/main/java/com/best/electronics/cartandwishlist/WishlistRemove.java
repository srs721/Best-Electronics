package com.best.electronics.cartandwishlist;

import com.best.electronics.database.IDatabasePersistence;
import com.best.electronics.database.MySQLDatabasePersistence;
import com.best.electronics.model.WishListItem;
import com.best.electronics.repository.WishListRepository;

public class WishlistRemove implements CommandForProduct {
    WishListItem wishListItem;

    public WishlistRemove(WishListItem wishListItem){
        this.wishListItem = wishListItem;
    }
    @Override
    public void execute() {
        IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
        WishListRepository wishListRepository = new WishListRepository(databasePersistence);
        wishListRepository.removeProductFromWishlist(wishListItem);
    }
}
