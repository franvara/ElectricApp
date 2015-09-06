package com.uc3m.etrip;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        // Parse.com initialization code
        Parse.initialize(this, "8TUN8Pt1gNbKn41u8GwyY8coY3SyCoG4OyvFSPj8",
                "ytW53W5QtsXzpsL4XM82qxwKxEB3TEAnJt7eMDpx");

        ParseUser.enableAutomaticUser();

/*        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        //defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);*/
    }

}
