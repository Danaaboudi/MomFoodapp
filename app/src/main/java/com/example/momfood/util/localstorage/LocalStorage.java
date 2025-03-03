package com.example.momfood.util.localstorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class LocalStorage {


    public static final String KEY_HASH = "hash";
    public static final String RECIPE_SLIDER = "recipeSlider";
    public static final String KEY_USER = "User";
    public static final String KEY_USER_ADDRESS = "user_address";
    public static final String KEY_PREFERENCES = "preferences";
    public static final String USER_PREFERENCES = "user_preferences";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String SLIDER_IMAGE = "slider_image";
    public static final String ADVERTISE_IMAGE = "advertise_image";
    public static final String CATEGORY = "category";
    public static final String FAVORITE_CATEGORY = "fav_category";

    private static final String IS_USER_LOGIN = "IsUserLoggedIn";


    private static LocalStorage instance = null;
    SharedPreferences sharedPreferences;
    Editor editor;
    int PRIVATE_MODE = 0;
    Context _context;

    public LocalStorage(Context context) {
        sharedPreferences = context.getSharedPreferences("Preferences", 0);
    }

    public static LocalStorage getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalStorage.class) {
                if (instance == null) {
                    instance = new LocalStorage(context);
                }
            }
        }
        return instance;
    }

    public String getCart() {
        if (sharedPreferences.contains("CART"))
            return sharedPreferences.getString("CART", null);
        else return null;
    }


    public void setCart(String cart) {
        Editor editor = sharedPreferences.edit();
        editor.putString("CART", cart);
        editor.commit();
    }

    public void deleteCart() {
        Editor editor = sharedPreferences.edit();
        editor.remove("CART");
        editor.commit();
    }


    public String getOrder() {
        if (sharedPreferences.contains("ORDER"))
            return sharedPreferences.getString("ORDER", null);
        else return null;
    }


    public void setOrder(String order) {
        Editor editor = sharedPreferences.edit();
        editor.putString("ORDER", order);
        editor.commit();
    }

    public void deleteOrder() {
        Editor editor = sharedPreferences.edit();
        editor.remove("ORDER");
        editor.commit();
    }


}
