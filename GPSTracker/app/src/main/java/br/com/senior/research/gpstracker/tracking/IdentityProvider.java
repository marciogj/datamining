package br.com.senior.research.gpstracker.tracking;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.provider.Settings;

import java.util.Random;

/**
 * Created by Marcio.Jasinski on 22/06/2016.
 */
public class IdentityProvider {
    private Context context;

    public IdentityProvider(Context context) {
        this.context = context;
    }

    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getUsername() {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        String username = generateUser(10);
        Account account;
        if (accounts.length > 0) {
            username = accounts[0].name.split("@")[0];
        }
        return username;
    }

    public String getEmail() {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            return accounts[0].name;
        }
        return null;
    }

    private String generateUser(int length){
        String alphabet = new String("abcdefghijklmnopqrstuvwxyz");
        int n = alphabet.length();
        String result = new String();
        Random r = new Random();

        for (int i=0; i<length; i++) {
            result = result + alphabet.charAt(r.nextInt(n));
        }

        return result;
    }
}
