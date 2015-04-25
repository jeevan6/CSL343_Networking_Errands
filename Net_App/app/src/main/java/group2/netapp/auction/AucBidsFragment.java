package group2.netapp.auction;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import group2.netapp.R;
import group2.netapp.utilFragments.ServerConnect;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class AucBidsFragment extends Fragment {

    TextView location,order;
    JSONObject bid;
    boolean isRequest;
    public AucBidsFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if(getArguments().getBoolean("isRequest",false)){
//            setHasOptionsMenu(true);
//
//            Log.d("AucBidFrag", "OnCreate Menu inflated");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_auc_bids, container, false);
        setUpView(v);
        return v;
    }

    public void setUpView(View v){
        location = (TextView) v.findViewById(R.id.auc_bidview_loc);
        order = (TextView) v.findViewById(R.id.auc_bidview_order);

        Bundle b = getArguments();
        isRequest = b.getBoolean("isRequest",false);
        if(isRequest) setHasOptionsMenu(true);

        try {
            bid = new JSONObject(new JSONTokener(b.getString("bid","")));
            location.setText("BidId: "+bid.getString("location"));
            order.setText("Order: " + bid.getJSONArray("orders").length()+" orders");
        } catch (JSONException e) {
            e.printStackTrace();
        }
     }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bid_requests,menu);
        Log.d("AucBidFrag", "Menu inflated");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.accept_bid: acceptBid();
                break;
            case R.id.reject_bid: rejectBid();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void acceptBid(){
        try {
            int auctionId = ((AuctionActivity)getActivity()).getAuctionDetails().getInt("idAuction");
            int bidId = bid.getInt("idBid");
            ServerConnect myServer=new ServerConnect(getActivity());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id_auc",auctionId+""));
            nameValuePairs.add(new BasicNameValuePair("id_bid",bidId+""));
            Log.d("AuctionActivity",getString(R.string.IP)+"accept_bid.php");
            myServer.execute(getString(R.string.IP)+"accept_bid.php",nameValuePairs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rejectBid(){
        try {
            int auctionId = ((AuctionActivity)getActivity()).getAuctionDetails().getInt("idAuction");
            int bidId = bid.getInt("idBid");
            ServerConnect myServer=new ServerConnect(getActivity());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id_auc",auctionId+""));
            nameValuePairs.add(new BasicNameValuePair("id_bid",bidId+""));
            Log.d("AuctionActivity",getString(R.string.IP)+"reject_bid.php");
            myServer.execute(getString(R.string.IP)+"reject_bid.php",nameValuePairs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

