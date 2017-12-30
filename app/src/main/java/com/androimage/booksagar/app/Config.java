package com.androimage.booksagar.app;

/**
 * Created by subha on 30-07-2016.
 */
public class Config {
    //public static final String GET_URL = "http://skhdroid.comli.com/jsonResponse.php";


//    public static final String GET_URL = "http://www.booksagar.com/appwebservices/get_books.php";
//
//    public static final String GET_URL_ID = "http://www.booksagar.com/appwebservices/get_book_by_id.php";
//
//
//    // server URL configuration
//    public static final String URL_REQUEST_SMS = "http://www.booksagar.com/appwebservices/request_sms.php";
//    public static final String URL_VERIFY_OTP = "http://www.booksagar.com/appwebservices/verify_otp.php";
//    public static final String URL_LOGIN = "http://www.booksagar.com/appwebservices/loginUser.php";
//    public static final String UPLOAD_URL ="http://www.booksagar.com/appwebservices/upload_img.php";
//    public static final String URL_ADDBOOK ="http://www.booksagar.com/appwebservices/add_book.php";
//    public static final String URL_SELLERDETAILS ="http://www.booksagar.com/appwebservices/get_seller_details.php";
//    public static final String URL_DELETE_BOOK ="http://www.booksagar.com/appwebservices/delete_book.php";
//
//
//    public static final String URL_REQUEST_OTP ="http://www.booksagar.com/appwebservices/requestOtp.php";
//
//    public static final String URL_SUBMIT_OTP ="http://www.booksagar.com/appwebservices/submitOtp.php";
//
//    public static final String URL_RESET_PWD ="http://www.booksagar.com/appwebservices/resetPwd.php";
//
//
//    public static final String URL_UPDATE_BOOK ="http://www.booksagar.com/appwebservices/update_book.php";
//
//
//
//    public static final String GET_URL = "http://www.booksagar.com/appwebservices/get_books.php";
//
//    public static final String GET_URL_ID = "http://www.booksagar.com/appwebservices/get_book_by_id.php";


    // server URL configuration

    public static final String URL_UPDATE_PROFILE = "http://www.booksagar.com/appwebservices/updateUser.php";

    public static final String URL_REQUEST_SMS = "http://www.booksagar.com/appwebservices/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://www.booksagar.com/appwebservices/verify_otp.php";
    public static final String URL_LOGIN = "http://www.booksagar.com/appwebservices/loginUser.php";
    public static final String UPLOAD_URL = "http://www.booksagar.com/appwebservices/upload_img.php";
    public static final String URL_ADDBOOK = "http://www.booksagar.com/appwebservices/add_book.php";


    public static final String URL_SELLERDETAILS = "http://www.booksagar.com/appwebservices/get_seller_details.php";
    public static final String URL_USERINTERESTED = "http://www.booksagar.com/appwebservices/intrestedUsers.php";

    public static final String URL_NOTIFYSELLER = "http://www.booksagar.com/appwebservices/send_notification.php";

    public static final String URL_DELETE_BOOK = "http://www.booksagar.com/appwebservices/delete_book.php";

    public static final String URL_UPDATE_SOLD = "http://www.booksagar.com/appwebservices/update_sold_status.php";


    public static final String URL_REQUEST_OTP = "http://www.booksagar.com/appwebservices/requestOtp.php";

    public static final String URL_SUBMIT_OTP = "http://www.booksagar.com/appwebservices/submitOtp.php";

    public static final String URL_USER_TOKEN = "http://www.booksagar.com/appwebservices/userToken.php";

    public static final String URL_RESET_PWD = "http://www.booksagar.com/appwebservices/resetPwd.php";

    public static final String URL_UPDATE_BOOK = "http://www.booksagar.com/appwebservices/update_book.php";

    public static final String GET_URL = "http://www.booksagar.com/appwebservices/get_books.php";

    public static final String GET_URL_ID = "http://www.booksagar.com/appwebservices/get_book_by_id.php";


    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "BokSagar";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";


    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
