package org.citruscircuits.database_setup_2015_android;

import android.util.Log;

import com.dropbox.sync.android.DbxAccountManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by cu52805 on 1/19/2015.
 */
public class Utils {
    public static final int REQUEST_LINK_TO_DBX = 1;
    public static final String APP_KEY = "APP KEY HERE";
    public static final String APP_SECRET = "APP SECRET HERE";
    public static final String SCHEDULE_PATH = "/Database File/schedule.json";
    public static final String FILE_NAME = "champs_database.realm";
    public static void handleError(Exception e) {
        Log.e("test", e.getMessage() + "");
    }
    public static String generateRandomTeamName() {
        Random random = new Random();

        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String name = "";

        for(int u = 0; u < 8; u++) {
            String letter = alphabet[random.nextInt(alphabet.length)];

            if(u == 0) {
                letter = letter.toUpperCase();
            }

            name = name + letter;
        }
        name = name + " Robotics";

        return name;
    }

    public static String formatForDatabase(String unformattedTeam) {
        String[] nums = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        List<String> numbers = new ArrayList<>(Arrays.asList(nums));
        String formattedTeam = unformattedTeam;
        for (int i = 0; i < unformattedTeam.length(); i++) {
            String character = unformattedTeam.substring(i, i + 1);

            if (!numbers.contains(character)) {
                Log.e("test", "The character is '" + character + "'");
                formattedTeam = formattedTeam.replace(character, "");
            }
        }

        Log.e("test", "The team is now " + formattedTeam);

        return formattedTeam;
    }
}
