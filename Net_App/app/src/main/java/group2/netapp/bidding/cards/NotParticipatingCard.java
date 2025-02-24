package group2.netapp.bidding.cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group2.netapp.FeedbackActivity;
import group2.netapp.R;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by mohit on 12/4/15.
 */
public class NotParticipatingCard extends Card {

    String auctionLocation,price,desc,idUser,start_time,end_time,expected_time,order_limit;
    int idAuction,index;
    String ratings,numRated;

    public NotParticipatingCard(Context context, JSONObject j,int i) {
        super(context, R.layout.not_participating_card);

        try {
            this.index=i;
            this.auctionLocation =j.getString("location");
            this.desc = j.getString("description");
            this.idUser = j.getString("idUser");
            String temp = j.getString("start_time");
            this.start_time = temp.substring(0,temp.length()-3);
            temp = j.getString("end_time");
            this.end_time = temp.substring(0,temp.length()-3);
            this.idAuction = j.getInt("idAuction");
            temp = j.getString("expctd_time");
            this.expected_time = temp.substring(0,temp.length()-3);
            this.order_limit = j.getString("orderLimit");
            this.price = j.getString("minPrice");
            this.ratings=j.getString("rating");
            this.numRated=j.getString("numRated");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        TextView auctionLocView = (TextView) parent.findViewById(R.id.notparticipatinglocation);
        TextView priceView = (TextView) parent.findViewById(R.id.notparticipatingprice);
        TextView descView = (TextView)parent.findViewById(R.id.notparticipatingdesc);
        RatingBar ratingsView = (RatingBar)parent.findViewById(R.id.notparticipatingratings);
        TextView numRatedView = (TextView)parent.findViewById(R.id.notparticipatingnumRated);
        TextView end_timeView = (TextView)parent.findViewById(R.id.notparticipatingend_time);
        TextView expected_timeView = (TextView)parent.findViewById(R.id.notparticipatingexpected_time);
        TextView orderLimit = (TextView)parent.findViewById(R.id.participatingorderlimit);

        auctionLocView.setText(auctionLocation);
        priceView.setText("₹" + price);
        descView.setText(desc);
        orderLimit.setText(order_limit);
        if(ratings == null || ratings.equals("null"))
        {
            ratings="0";
            numRated="0";

        }
        ratingsView.setRating(Float.parseFloat(ratings));
        numRatedView.setPaintFlags(numRatedView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        numRatedView.setText("rated by : "+numRated+" users");
        numRatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), FeedbackActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("id", idUser);
                mBundle.putInt("tag",1);
                mIntent.putExtras(mBundle);
                getContext().startActivity(mIntent);
            }
        });
        end_timeView.setText(String.valueOf(end_time));
        expected_timeView.setText(String.valueOf(expected_time));
    }

    public String getAuctionLocation() {
        return auctionLocation;
    }

    public void setAuctionLocation(String auctionLocation) {
        this.auctionLocation = auctionLocation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getIdAuction() {
        return idAuction;
    }

    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }

    public String getExpected_time() {
        return expected_time;
    }

    public void setExpected_time(String expected_time) {
        this.expected_time = expected_time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getNumRated() {
        return numRated;
    }

    public void setNumRated(String numRated) {
        this.numRated = numRated;
    }
}
