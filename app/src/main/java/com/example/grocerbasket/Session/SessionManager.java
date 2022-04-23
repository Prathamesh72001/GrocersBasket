package com.example.grocerbasket.Session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;


    public static final String SESSION_USER = "userLoginSession";
    public static final String SESSION_ADMIN = "adminLoginSession";
    public static final String SESSION_SELLER = "sellerLoginSession";
    public static final String SESSION_NOTIFICATION = "notifySession";
    public static final String SESSION_REMEMBERME = "rememberMe";
    public static final String SESSION_ADDRESS = "address";
    public static final String SESSION_CURRLOC = "currloc";
    public static final String SESSION_FORWHO = "who";

    //UserLoginSessionVar
    private static final String IS_LOGIN = "isloggedin";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONENO = "phoneno";

    //SellerLoginSessionVar
    private static final String IS_OPEN = "isOpen";
    public static final String KEY_SELLERFIRSTNAME = "fullname";
    public static final String KEY_SELLERLASTNAME = "lastname";
    public static final String KEY_SELLERSHOPNAME = "shopname";
    public static final String KEY_SELLERSHOPADD = "shopadd";
    public static final String KEY_SELLERDELFEE = "delfee";
    public static final String KEY_SELLEREMAIL = "email";
    public static final String KEY_SELLERPASSWORD = "password";
    public static final String KEY_SELLERPHONENO = "phoneno";

    //RememberMeSessionVar
    private static final String IS_REMEMBER = "isRemember";
    public static final String KEY_REMEMBERPASSWORD = "password";
    public static final String KEY_REMEMBERPHONENO = "phoneno";

    //AddressSessionVar
    public static final String KEY_ADD = "address";

    //CurrLocSessionVar
    public static final String KEY_CURRLOC = "currloc";

    //ForWhomSessionVar
    public static final String KEY_FORWHO = "who";

    //NotificationSession
    public static final String IS_NOTIFY = "false";

    //SessionManager
    public SessionManager(Context _context, String sessionName) {
        context = _context;
        userSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }


    //AdminLoginSession
    public void creatingAdminSession() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }


    //UserLoginSession
    public void creatingUserSession(String firstname, String lastname, String email, String password, String phoneno) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FIRSTNAME, firstname);
        editor.putString(KEY_LASTNAME, lastname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENO, phoneno);
        editor.putString(KEY_PASSWORD, password);


        editor.commit();
    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userdata = new HashMap<String, String>();

        userdata.put(KEY_FIRSTNAME, userSession.getString(KEY_FIRSTNAME, null));
        userdata.put(KEY_LASTNAME, userSession.getString(KEY_LASTNAME, null));
        userdata.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userdata.put(KEY_PHONENO, userSession.getString(KEY_PHONENO, null));
        userdata.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));


        return userdata;
    }


    //SellerLoginSession
    public void creatingSellerSession(String firstname, String lastname, String shopname, String shopadd, String delfee, String email, String password, String phoneno) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_SELLERFIRSTNAME, firstname);
        editor.putString(KEY_SELLERLASTNAME, lastname);
        editor.putString(KEY_SELLERSHOPNAME, shopname);
        editor.putString(KEY_SELLERSHOPADD, shopadd);
        editor.putString(KEY_SELLERDELFEE, delfee);
        editor.putString(KEY_SELLEREMAIL, email);
        editor.putString(KEY_SELLERPHONENO, phoneno);
        editor.putString(KEY_SELLERPASSWORD, password);


        editor.commit();
    }

    public HashMap<String, String> getSellerDetailFromSession() {
        HashMap<String, String> sellerdata = new HashMap<String, String>();

        sellerdata.put(KEY_SELLERFIRSTNAME, userSession.getString(KEY_SELLERFIRSTNAME, null));
        sellerdata.put(KEY_SELLERLASTNAME, userSession.getString(KEY_SELLERLASTNAME, null));
        sellerdata.put(KEY_SELLERSHOPNAME, userSession.getString(KEY_SELLERSHOPNAME, null));
        sellerdata.put(KEY_SELLERSHOPADD, userSession.getString(KEY_SELLERSHOPADD, null));
        sellerdata.put(KEY_SELLERDELFEE, userSession.getString(KEY_SELLERDELFEE, null));
        sellerdata.put(KEY_SELLEREMAIL, userSession.getString(KEY_SELLEREMAIL, null));
        sellerdata.put(KEY_SELLERPHONENO, userSession.getString(KEY_SELLERPHONENO, null));
        sellerdata.put(KEY_SELLERPASSWORD, userSession.getString(KEY_SELLERPASSWORD, null));


        return sellerdata;
    }

    public boolean checkIsOpen() {
        if (userSession.getBoolean(IS_OPEN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void OpenShop() {
        editor.putBoolean(IS_OPEN, true);
        editor.commit();
    }

    public void CloseShop() {
        editor.putBoolean(IS_OPEN, false);
        editor.commit();
    }


    //ForAll
    public boolean checkLogin() {
        if (userSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }


    //RememberMe Session
    public void creatingRememberMeSession(String phoneno, String password) {
        editor.putBoolean(IS_REMEMBER, true);

        editor.putString(KEY_REMEMBERPHONENO, phoneno);
        editor.putString(KEY_REMEMBERPASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailFromSession() {
        HashMap<String, String> userdata = new HashMap<String, String>();

        userdata.put(KEY_REMEMBERPHONENO, userSession.getString(KEY_REMEMBERPHONENO, null));
        userdata.put(KEY_REMEMBERPASSWORD, userSession.getString(KEY_REMEMBERPASSWORD, null));

        return userdata;
    }

    public boolean checkRememberMe() {
        if (userSession.getBoolean(IS_REMEMBER, false)) {
            return true;
        } else {
            return false;
        }
    }


    //CurrLoc Session
    public void creatingCurrLocSession(String Address) {

        editor.putString(KEY_CURRLOC, Address);

        editor.commit();
    }

    public HashMap<String, String> getCurrLocDetailFromSession() {
        HashMap<String, String> userdata = new HashMap<String, String>();

        userdata.put(KEY_CURRLOC, userSession.getString(KEY_CURRLOC, "Swagath Rd-Tilaknagar,Banglore-560041"));

        return userdata;
    }


    //Address Session
    public void creatingAddressSession(String Address) {

        editor.putString(KEY_ADD, Address);

        editor.commit();
    }

    public HashMap<String, String> getAddressDetailFromSession() {
        HashMap<String, String> userdata = new HashMap<String, String>();

        userdata.put(KEY_ADD, userSession.getString(KEY_ADD, "Swagath Rd-Tilaknagar,Banglore-560041"));

        return userdata;
    }


    //ForWhom Session
    public void creatingForWhomSession(String who) {

        editor.putString(KEY_FORWHO, who);

        editor.commit();
    }

    public HashMap<String, String> getForWhomDetailFromSession() {
        HashMap<String, String> userdata = new HashMap<String, String>();

        userdata.put(KEY_FORWHO, userSession.getString(KEY_FORWHO, "forWho"));

        return userdata;
    }


    //NotifySession
    public boolean checkIsSubscribed() {
        if (userSession.getBoolean(IS_NOTIFY, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void Subscribed() {
        editor.putBoolean(IS_NOTIFY, true);
        editor.commit();
    }

    public void UnSubscribed() {
        editor.putBoolean(IS_NOTIFY, false);
        editor.commit();
    }
}
