package org.trident.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{

   public static final String EXTRA_SIGN_IN = "IN";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }

   public void onSignIn(View v)
   {
      Intent signInIntent = new Intent(this, ScanningActivity.class);
      signInIntent.putExtra(EXTRA_SIGN_IN, true);
      startActivity(signInIntent);
   }

   public void onSignOut(View v)
   {
      Intent signInIntent = new Intent(this, ScanningActivity.class);
      signInIntent.putExtra(EXTRA_SIGN_IN, false);
      startActivity(signInIntent);
   }

}
