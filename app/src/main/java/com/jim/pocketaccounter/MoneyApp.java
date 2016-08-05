package com.jim.pocketaccounter;

import android.app.Application;

public class MoneyApp extends Application {
  
  @Override
  public void onCreate() {
    TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "robotoRegular.ttf");
  }
}