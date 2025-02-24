package group2.netapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;
//import it.gmariotti.cardslib.library.view.CardExpandableListView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceRateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressDialog progressdialoglistview;
    private static int counter = 1;
    private CardListView listView;
    ArrayList<Card> cards;
    CardArrayAdapter cardListAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AsyncTask asynctask;
    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static ServiceRateFragment newInstance(String param1, String param2) {
        ServiceRateFragment fragment = new ServiceRateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceRateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View infh = inflater.inflate(R.layout.fragment_service_rate, container, false);

        cards = new ArrayList<Card>();
        cardListAdapter = new CardArrayAdapter(getActivity().getApplicationContext(),cards);

        listView = (CardListView) infh.findViewById(R.id.ServiceReqCardList);
        if (listView != null) {
            listView.setAdapter(cardListAdapter);
        }

        progressdialoglistview = new ProgressDialog(getActivity());
        progressdialoglistview.setMessage("Loading");
        progressdialoglistview.setCanceledOnTouchOutside(true);
        progressdialoglistview.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialoglistview.setIndeterminate(true);

        Button LoadMore = new Button(getActivity().getApplicationContext());
        LoadMore.setText("Load More");

// Adding button to listview at footer
        listView.addFooterView(LoadMore);



        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String email = saved_values.getString("email",null);
                ArrayList<NameValuePair> list1 = new ArrayList<NameValuePair>();
                list1.add(new BasicNameValuePair("tag", "1"));          //tag = 1 for service rates
                list1.add(new BasicNameValuePair("counter", String.valueOf(counter)));
                list1.add(new BasicNameValuePair("email",email));
                if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
                    asynctask.cancel(true);
                asynctask = new get_review(list1, "http://netapp.byethost33.com/service_rate.php").execute(null, null, null);
            }
        });


        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String email = saved_values.getString("email",null);
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag","1"));
        nameValuePairs.add(new BasicNameValuePair("counter", String.valueOf(counter)));
        nameValuePairs.add(new BasicNameValuePair("email",email));

        asynctask = new get_review(nameValuePairs,"http://netapp.byethost33.com/service_rate.php").execute(null,null,null);
        return infh;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
        ((HomeActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
            asynctask.cancel(true);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class get_review extends AsyncTask<String,String,String>
    {
        public boolean running = true;
        private ArrayList<NameValuePair> list;
        private String host;
        HttpResponse response;
        private InputStream is;
        public get_review(ArrayList<NameValuePair> l,String h)
        {
            list=l;
            host=h;
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected  void onPreExecute()
        {
            progressdialoglistview.show();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(host);
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try {
                if(running)
                {
                    httppost.setEntity(new UrlEncodedFormEntity(list));

                    // Execute HTTP Post Request
                    response = httpclient.execute(httppost);
                    if (response != null) {
                        is = response.getEntity().getContent();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                        StringBuilder sb = new StringBuilder();
//
//                        String line = null;
//                        try {
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line + "\n");
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                is.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        String text = sb.toString();
//                        System.out.println("ssdvs" + text);
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
                // TODO Auto-generated catch block
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result) {
            if(running)
            {Reader reader = new InputStreamReader(is);
                List<ServiceUser> posts = new ArrayList<ServiceUser>();
                try {
                    JsonParser parser = new JsonParser();
                    JsonObject data = parser.parse(reader).getAsJsonObject();

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    Type listType = new TypeToken<List<ServiceUser>>() {
                    }.getType();
                    if (running)
                        posts = gson.fromJson(data.get("Ratings"), listType);          //EDIT THIS LINE
                } catch (Exception e) {
                    e.printStackTrace();
                }

                cards.clear();
                if(posts==null){
                    Toast.makeText(getActivity().getApplicationContext(),"No pending service reviews found",Toast.LENGTH_SHORT).show();;
                }
                if (posts != null && running )
                    for (int i = 0; i < posts.size(); ++i) {
                        ServiceUser a = posts.get(i);

                        ServiceUserReqCard card = new ServiceUserReqCard(getActivity().getApplicationContext(),a.location,a.start_time,a.end_time,a.name);

                        ServiceCustomCardExpand expand = new ServiceCustomCardExpand(getActivity().getApplicationContext(),a.idFeedback,i,a.idUser);

                        card.addCardExpand(expand);

                        cards.add(card);
                    }
                cardListAdapter.notifyDataSetChanged();
                if(progressdialoglistview.isShowing())
                    progressdialoglistview.dismiss();
                try {
                    reader.close();
                    is.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

    }
class add_review extends AsyncTask<String,String,String>
{
    private ArrayList<NameValuePair> list;
    private String host="http://netapp.byethost33.com/add_rate.php";
    private RatingBar rating;
    private EditText review;
    HttpResponse response;
    Context con;
    Button button;
    int index;
    String idUser;
    public add_review(Context context, ArrayList<NameValuePair> l ,Button b, RatingBar ratingbar , EditText reviewEditText,int id,String user)
    {
        list=l;
        rating=ratingbar;
        review=reviewEditText;
        con=context;
        button =b;
        index = id;
        idUser = user;
    }


    @Override
    protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(host);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try {

            httppost.setEntity(new UrlEncodedFormEntity(list));
            // Execute HTTP Post Request
            response = httpclient.execute(httppost);

        } catch (Exception e) {
            System.out.println(e);
            // TODO Auto-generated catch block
        }
        return null;
    }

    @Override
    protected void onPostExecute(String Result) {

        if(response!=null)
        {
            Toast.makeText(con,"Review submitted",Toast.LENGTH_SHORT).show();
            rating.setClickable(false);
            review.setClickable(false);
            button.setClickable(false);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("user", idUser));
            nameValuePairs.add(new BasicNameValuePair("message", "You got a new feedback"));
            new push_target(nameValuePairs).execute(null, null, null);
            cards.remove(index);
            cardListAdapter.notifyDataSetChanged();

        }
        else
            System.out.println("Review submission failed");

    }
}





class ServiceCustomCardExpand extends CardExpand {
    //Use your resource ID for your inner layout
    String idFeedback;
    Context con;
    int idf;
    String idUser;
    public ServiceCustomCardExpand(Context context,String id,int index,String User)
    {
        super(context, R.layout.service_rate_expand);
        idFeedback=id;
        con=context;
        idf = index;
        idUser = User;
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, final View view) {

        if (view == null) return;

        final RatingBar rating = (RatingBar) view.findViewById(R.id.serviceratingBar);
        final EditText review = (EditText) view.findViewById(R.id.servicereviewedit);
        final Button submit = (Button)view.findViewById(R.id.servicereviewsubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD REVIEW SUBMIT BUTTON FUNCTIONALITY HERE

                String stars = String.valueOf(Math.round(rating.getRating()));
                String reviewText = review.getText().toString();
                if(reviewText.length()>0 && Integer.parseInt(stars)!=0) {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("id", idFeedback));
                    nameValuePairs.add(new BasicNameValuePair("star", stars));
                    nameValuePairs.add(new BasicNameValuePair("tag", String.valueOf(1)));
                    nameValuePairs.add(new BasicNameValuePair("review", reviewText));
                    new add_review(con, nameValuePairs, submit, rating, review, idf, idUser).execute(null, null, null);
                }
                else{
                    Toast.makeText(getActivity(),"Please enter the rating and description",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
}
class ServiceUserReqCard extends Card {

    private String  location, end_time, start_time, ratename;


    public ServiceUserReqCard(Context context, String loc, String st, String et, String name){
        super(context, R.layout.service_rate_card);
        location = loc;
        start_time = st;
        end_time = et;
        ratename = name;
    }
    public ServiceUserReqCard(Context context){
        super(context, R.layout.service_rate_card);
    }



    @Override
    public void setupInnerViewElements(ViewGroup parent, View view){

        //Retrieve TextView elements
        TextView tx1 = (TextView) view.findViewById(R.id.servicelocation);
        TextView tx2 = (TextView) view.findViewById(R.id.starttime);
        TextView tx3 = (TextView) view.findViewById(R.id.endtime);
        TextView tx4 = (TextView) view.findViewById(R.id.rateuser);
        //Set value in text view
        tx1.setText(location);
        tx2.setText(start_time);
        tx3.setText(end_time);
        tx4.setText(ratename);
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder().setupView(view);
        setViewToClickToExpand(viewToClickToExpand);
    }
}

class ServiceUser{

    public String idFeedback;
    public String location;
    public String start_time;
    public String end_time;
    public String name;
    public String idUser;
}

class push_target extends AsyncTask<String,String,String>
{
    private ArrayList<NameValuePair> list;
    private String host="http://netapp.byethost33.com/targeted_broadcast.php";
    public push_target(ArrayList<NameValuePair> l)
    {
        list=l;
    }


    @Override
    protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(host);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try {

            httppost.setEntity(new UrlEncodedFormEntity(list));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            if(response!=null)
            {InputStream is = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String id = sb.toString();
            System.out.println(id);}

        } catch (Exception e) {
            System.out.println(e);
            // TODO Auto-generated catch block
        }
        return null;
    }


}