package com.csl343.group2.orderit.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.csl343.group2.orderit.R;
import com.csl343.group2.orderit.auction.DecideAuctionActivity;
import com.csl343.group2.orderit.auction.ServerFormActivity;
import com.csl343.group2.orderit.bidding.CurrAuctionActivity;
import com.csl343.group2.orderit.utilFragments.ServerConnect;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    Button sp,cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = (Button) findViewById(R.id.spBtn);
        cs = (Button) findViewById(R.id.csBtn);

        sp.setOnClickListener(this);
        cs.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.spBtn:
                Context context = getApplicationContext();
                CharSequence text = "Auction Started Successfully";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                i = new Intent(this, DecideAuctionActivity.class);
                startActivity(i);
                break;
            case R.id.csBtn:
                i = new Intent(this, CurrAuctionActivity.class);
                startActivity(i);
                break;
        }
    }
}
