package group2.netapp.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.netapp.R;
import group2.netapp.auction.bidsTabs.AcceptedBids;
import group2.netapp.auction.bidsTabs.PostAcceptedBids;
import group2.netapp.auction.cards.PostBidCard;
import group2.netapp.utilFragments.ProgressFragment;
import group2.netapp.utilFragments.ServerConnect;
import it.gmariotti.cardslib.library.internal.Card;


public class AuctionActivity extends FragmentActivity implements BidRequestsFragment.BidRequestsListener, ServerConnect.OnResponseListener, AcceptedBids.BidAcceptListener, PostAcceptedBids.BidAcceptListener, AuctionDashboardFragment.AuctionDashboardListener, PostAuctionDashboardFragment.PostAuctionDashboardListener, ConfirmBidsFragment.ConfirmBidsListener{

    JSONObject auctionDetails;
    JSONArray pendingBids, runningBids, confirmedBids;
    String idUser;
    int isRunning;

    ArrayList<ArrayList<Card>> postAuctionBids;
    ArrayList<Integer> checkIndices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        idUser = sp.getString("id", "");
        Log.d("AuctionActivity","id user:"+idUser);
        setContentView(R.layout.activity_auction);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        loadData();
//        openDashboard();

    }

    public void loadData(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment progressFrag = new ProgressFragment();
        ft.add(R.id.auction_frame,progressFrag,"Progress");
        ft.commit();

        ServerConnect myServer=new ServerConnect(this);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id_user",idUser));
        Log.d("AuctionActivity",getString(R.string.IP)+"getAuction.php");
        myServer.execute(getString(R.string.IP)+"getAuction.php",nameValuePairs);

    }

    @Override
    public void onResponse(JSONArray j) {
        try {
            String tag = ((JSONObject)j.get(0)).getString("tag");
            if(tag.equals("loading")) {
                isRunning = j.getJSONObject(1).getInt("isRunning");
                if (isRunning == 0){
                    openServerForm();
                } else {
                    auctionDetails = j.getJSONObject(2);
                    if(isRunning==3){
                        confirmedBids = j.getJSONArray(3);
                        Log.d("AuctionActivity", auctionDetails.toString());
                        Log.d("AuctionActivity", confirmedBids.toString());
                        openConfirmBids();
                    }else {
                        pendingBids = j.getJSONArray(3);
                        runningBids = j.getJSONArray(4);
                        Log.d("AuctionActivity", auctionDetails.toString());
                        Log.d("AuctionActivity", pendingBids.toString());
                        Log.d("AuctionActivity", runningBids.toString() + " " + runningBids.length());
                        if (isRunning == 1) {
                            openDashboard();
                        } else if (isRunning == 2) {
                            postAuctionBids = new ArrayList<>();
                            checkIndices = new ArrayList<>();
                            for (int x = 0; x < runningBids.length(); ++x) {
                                postAuctionBids.add(new ArrayList<Card>());
                                checkIndices.add(-1);
                            }
                            Log.d("AuctionActivity", "postAuctionBid length:" + postAuctionBids.size());
                            openPostAuction();

                        }
                    }
                }
            }else if(tag.equals("bid_request")){
                boolean status = ((JSONObject)j.get(1)).getBoolean("status");
                if(status){
                    Toast.makeText(this, "Bid Request Accepted!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Bid Request can't be Accepted!", Toast.LENGTH_SHORT).show();
                }
                finish();
                startActivity(getIntent());
            }else if(tag.equals("bid_reject")){
                boolean status = ((JSONObject)j.get(1)).getBoolean("status");
                if(status){
                    Toast.makeText(this, "Bid Request Rejected!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Bid Request can't be Rejected!", Toast.LENGTH_SHORT).show();
                }
                finish();
                startActivity(getIntent());
            }else if(tag.equals("bid_new_category")){
                boolean status = ((JSONObject)j.get(1)).getBoolean("status");
                if(status){
                    Toast.makeText(this, "Bid Request Accepted in new Category!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Bid Request can't be Accepted!", Toast.LENGTH_SHORT).show();
                }
                finish();
                startActivity(getIntent());
            }else if(tag.equals("bid_confirm")){
                boolean status = ((JSONObject)j.get(1)).getBoolean("status");
                if(status){
                    Toast.makeText(this, "Bids successfully confirmed!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Bids can't be confirmed!", Toast.LENGTH_SHORT).show();
                }
                finish();
                startActivity(getIntent());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openDashboard(){
        Bundle args = new Bundle();
        args.putString("auction", auctionDetails.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment aDashFrag = new AuctionDashboardFragment();
        aDashFrag.setArguments(args);
        ft.replace(R.id.auction_frame,aDashFrag,"AuctionDashboard");
//        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "DashboardOpened");
    }

    public void openPostAuction(){
        Bundle args = new Bundle();
        args.putString("auction", auctionDetails.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment aPostDashFrag = new PostAuctionDashboardFragment();
        aPostDashFrag.setArguments(args);
        ft.replace(R.id.auction_frame,aPostDashFrag,"AuctionDashboard");
//        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "DashboardOpened");
    }

    public void openConfirmBids(){
        Bundle args = new Bundle();
        args.putString("auction", auctionDetails.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment cBidsFrag = new ConfirmBidsFragment();
        cBidsFrag.setArguments(args);
        ft.replace(R.id.auction_frame,cBidsFrag,"ConfirmBids");
//        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "ConfirmBidsOpened");
    }

    public void openServerForm(){
        Intent i = new Intent(this,ServerFormActivity.class);
        startActivity(i);
        finish();
        Log.d("AuctionActivity", "ServerForm");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reload) {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openBidRequestFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment bReqFrag = new BidRequestsFragment();
        ft.replace(R.id.auction_frame,bReqFrag,"BidRequests");
        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "BidRequests Opened");
    }

    @Override
    public void openBidRequest(JSONObject bid, boolean isRequest) {
        Bundle args = new Bundle();
        args.putString("bid", bid.toString());
        if(isRequest){
            args.putBoolean("isRequest",true);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment aucBids = new AucBidsFragment();
        aucBids.setArguments(args);
        ft.replace(R.id.auction_frame, aucBids, "AuctionBids");
        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "AucBids");
    }

    public JSONObject getAuctionDetails() {
        return auctionDetails;
    }

    public void setAuctionDetails(JSONObject auctionDetails) {
        this.auctionDetails = auctionDetails;
    }

    public JSONArray getPendingBids() {
        return pendingBids;
    }

    public void setPendingBids(JSONArray pendingBids) {
        this.pendingBids = pendingBids;
    }

    public JSONArray getRunningBids() {
        return runningBids;
    }

    public void setRunningBids(JSONArray runningBids) {
        this.runningBids = runningBids;
    }

    public JSONObject getRunningBidAt(int i){
        try {
            return runningBids.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getPendingBidAt(int i){
        try {
            return pendingBids.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray getConfirmedBids() {
        return confirmedBids;
    }

    public void setConfirmedBids(JSONArray confirmedBids) {
        this.confirmedBids = confirmedBids;
    }

    public int getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(int isRunning) {
        this.isRunning = isRunning;
    }

    public ArrayList<ArrayList<Card>> getPostAuctionBids() {
        return postAuctionBids;
    }

    public ArrayList<Card> obtainArrayBids(int tabPosition) {
        if(tabPosition >= 0){
            ArrayList<Card> c = postAuctionBids.get(tabPosition);
            c.clear();
            return c;
        }
        return null;
    }

    public void setCheckIndex(int index, int checkIndex){
        checkIndices.set(index,checkIndex);
    }

    public int getCheckIndex(int index){
        return checkIndices.get(index);
    }

    @Override
    public void confirmBids() {
        int sum = 0;
        for(int i : checkIndices){
            sum+=(i+1);
        }
        Toast.makeText(this,"Bids to confirm: "+sum,Toast.LENGTH_SHORT).show();

        if(sum>0){
            final ServerConnect myServer=new ServerConnect(this);
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Bids Confirmation")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                int j=0;
                                for(ArrayList<Card> a : postAuctionBids){
                                    for(int i = 0; i<=checkIndices.get(j);++i){
                                        nameValuePairs.add(new BasicNameValuePair("id_bid[]",((PostBidCard)a.get(i)).getBid().getString("idBid")));
                                        nameValuePairs.add(new BasicNameValuePair("id_bid_user[]",((PostBidCard)a.get(i)).getBid().getString("idUser")));
                                    }
                                    j++;
                                }
                                nameValuePairs.add(new BasicNameValuePair("id_auc",auctionDetails.getString("idAuction")));
                                nameValuePairs.add(new BasicNameValuePair("id_auc_user",auctionDetails.getString("idUser")));
                                Log.d("AuctionActivity",getString(R.string.IP)+"confirm_bids.php");
                                myServer.execute(getString(R.string.IP)+"confirm_bids.php",nameValuePairs);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }else{
            Toast.makeText(this,"Select at least 1 bid for confirmation",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void openConfirmedBid(JSONObject bid) {
        Bundle args = new Bundle();
        args.putString("bid", bid.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment cBids = new ConfirmBidsDisplayFragment();
        cBids.setArguments(args);
        ft.replace(R.id.auction_frame, cBids, "ConfirmedBids");
        ft.addToBackStack(null);
        ft.commit();
        Log.d("AuctionActivity", "ConfirmedBids");
    }
}
