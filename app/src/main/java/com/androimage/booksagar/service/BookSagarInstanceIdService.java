package com.androimage.booksagar.service;

import android.util.Log;

import com.androimage.booksagar.helper.UpdateToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by subha on 9/24/2016.
 */

public class BookSagarInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", token);

        UpdateToken u = new UpdateToken(this, token, "");
        u.registerToken();
    }

}
