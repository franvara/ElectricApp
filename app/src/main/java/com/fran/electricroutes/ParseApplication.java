package com.fran.electricroutes;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

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
