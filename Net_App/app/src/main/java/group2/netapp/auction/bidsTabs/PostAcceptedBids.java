package group2.netapp.auction.bidsTabs;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import group2.netapp.R;
import group2.netapp.auction.AuctionActivity;
import group2.netapp.auction.cards.BidCard;
import group2.netapp.auction.cards.PostBidCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class PostAcceptedBids extends Fragment implements Card.OnCardClickListener, PostBidCard.PostBidCardListener{

    CardArrayRecyclerViewAdapter bidViewAdapter;
    CardRecyclerView bidView;
    BidAcceptListener bListener;

    JSONObject auc_category;
    JSONArray  acceptedBids;

    ArrayList<Card> cards;

    int tabPosition;

    public interface BidAcceptListener{
        public void openBidRequest(JSONObject bid, boolean isRequest);
    }

    public PostAcceptedBids() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bListener = (BidAcceptListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_accepted_bids, container, false);

        Bundle args = getArguments();
        try {
            auc_category = new JSONObject(new JSONTokener(args.getString("auction_category","")));
            tabPosition = args.getInt("tab",-1);
            setUpBidView(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        return v;
    }

    public void setUpBidView(View v) throws JSONException {
        if(auc_category!=null){
            acceptedBids = auc_category.getJSONArray("bids");
        }
        bidView = (CardRecyclerView) v.findViewById(R.id.auc_acc_bids_recyclerview);
        bidView.setHasFixedSize(true);

        bidViewAdapter = new CardArrayRecyclerViewAdapter(getActivity(), setBids());
        bidView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(bidView != null){
            bidView.setAdapter(bidViewAdapter);
//            bidView.scrollToPosition(acceptedBids.length()-1);
        }
    }

    public ArrayList<Card> setBids(){
        cards = ((AuctionActivity)getActivity()).obtainArrayBids(tabPosition);
        JSONObject bid;
        try {
            for(int i = 0; i< acceptedBids.length(); ++i) {
                bid = acceptedBids.getJSONObject(i);
                Log.d("AcceptedTab", "Location:" + bid.getString("location") + " Order:" + bid.getJSONArray("orders").length() + " items ordered");
                PostBidCard card;
                card = new PostBidCard(getActivity(),bid,this,i,((AuctionActivity)getActivity()).getCheckIndex(tabPosition));
                card.setOnClickListener(this);
                cards.add(card);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return cards;
    }

    @Override
    public void onClick(Card card, View view) {
        PostBidCard bCard = (PostBidCard) card;
        Log.d("Requests", "Fragment Added");
        bListener.openBidRequest(bCard.getBid(), false);


    }

    @Override
    public void onItemChecked(int index, boolean isChecked) {
        if(!isChecked){
            index = -1;
        }
        ((AuctionActivity)getActivity()).setCheckIndex(tabPosition,index);
        for(int i = 0;i<cards.size();++i){
            Log.d("PostAcceptedBids","onItem: "+i);
            Log.d("PostAcceptedBids",((PostBidCard) cards.get(i)).getBid().toString());
            ((PostBidCard) cards.get(i)).setCheckIndex(index);
//            if(i<=index) {
//                ((PostBidCard) cards.get(i)).setChecked(isChecked);
//            }else{
//                ((PostBidCard) cards.get(i)).setChecked(false);
//            }
        }
        bidViewAdapter.notifyDataSetChanged();
//        PostBidCard p = (PostBidCard) cards.get(index);
//        try {
//            Toast.makeText(getActivity(),"Bid clicked "+((PostBidCard)cards.get(index)).getBid().getString("location"),Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


}
