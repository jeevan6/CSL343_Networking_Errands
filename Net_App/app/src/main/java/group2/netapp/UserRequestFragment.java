package group2.netapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import it.gmariotti.cardslib.library.view.*;
import it.gmariotti.cardslib.library.internal.*;
//import it.gmariotti.cardslib.library.view.CardExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserRequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressDialog progressdialoglistview;
    private static int counter=1;
    private CardListView listView;
    ArrayList<Card> cards;
    CardArrayAdapter cardListAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AsyncTask asynctask;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserRequestFragment newInstance(String param1, String param2) {
        UserRequestFragment fragment = new UserRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserRequestFragment() {
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

        View infh = inflater.inflate(R.layout.fragment_user_request, container, false);

        cards = new ArrayList<Card>();
        cardListAdapter = new CardArrayAdapter(getActivity().getApplicationContext(),cards);

         listView = (CardListView) infh.findViewById(R.id.ReqCardList);
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
                ArrayList<NameValuePair> list1 = new ArrayList<NameValuePair>();
                list1.add(new BasicNameValuePair("tag", "1"));
                list1.add(new BasicNameValuePair("query", String.valueOf(counter)));
                if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
                    asynctask.cancel(true);
                asynctask = new get_requests(list1, "http://netapp.byethost33.com/get_broadcast.php",1).execute(null, null, null);
            }
        });

        EditText search = (EditText)infh.findViewById(R.id.SearchText);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0) {
                    ArrayList<NameValuePair> list1 = new ArrayList<NameValuePair>();
                    list1.add(new BasicNameValuePair("tag", "2"));
                    list1.add(new BasicNameValuePair("query", s.toString()));
                    if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
                        asynctask.cancel(true);
                    asynctask = new get_requests(list1, "http://netapp.byethost33.com/get_broadcast.php",0).execute(null, null, null);
                }
                else
                {
                    counter=1;
                    if(progressdialoglistview.isShowing())
                        progressdialoglistview.dismiss();
                    ArrayList<NameValuePair> list1 = new ArrayList<NameValuePair>();
                    list1.add(new BasicNameValuePair("tag", "1"));
                    list1.add(new BasicNameValuePair("query", String.valueOf(counter)));
                    if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
                        asynctask.cancel(true);
                    asynctask = new get_requests(list1, "http://netapp.byethost33.com/get_broadcast.php",1).execute(null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("tag","1"));
        nameValuePairs.add(new BasicNameValuePair("query", String.valueOf(1)));

        asynctask = new get_requests(nameValuePairs,"http://netapp.byethost33.com/get_broadcast.php",1).execute(null,null,null);
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
        ((HomeActivity) activity).onSectionAttached(4);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(asynctask.getStatus() == AsyncTask.Status.PENDING || asynctask.getStatus() == AsyncTask.Status.RUNNING)
            asynctask.cancel(true);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class get_requests extends AsyncTask<String,String,String>
    {
        public boolean running = true;
        private ArrayList<NameValuePair> list;
        private String host;
        HttpResponse response;
        private int purpose;
        private InputStream is;
        public get_requests(ArrayList<NameValuePair> l,String h,int purp)
        {
            list=l;
            host=h;
            purpose=purp;
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected  void onPreExecute()
        {
            if(purpose==1)
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
            List<UserRequests> posts = new ArrayList<UserRequests>();
            try {
                JsonParser parser = new JsonParser();
                JsonObject data = parser.parse(reader).getAsJsonObject();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                Type listType = new TypeToken<List<UserRequests>>() {
                }.getType();
                if (running)
                    posts = gson.fromJson(data.get("UserRequests"), listType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            cards.clear();
                if(posts==null){
                    Toast.makeText(getActivity().getApplicationContext(), "No requests found", Toast.LENGTH_SHORT).show();;
                }
            if (posts != null && running)
                for (int i = 0; i < posts.size(); ++i) {
                    UserRequests a = posts.get(i);

                    UserReqCard card = new UserReqCard(getActivity().getApplicationContext(),a.item, a.location);
                    CardHeader ch = new CardHeader(getActivity().getApplicationContext());
                    card.addCardHeader(ch);
                    CustomCardExpand expand = new CustomCardExpand(getActivity().getApplicationContext(), a.description, a.exptime, a.expdate);
                    card.addCardExpand(expand);
                    card.setSwipeable(true);

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

}

class CustomCardExpand extends CardExpand {

    //Use your resource ID for your inner layout
    private String description, time, date;
    public CustomCardExpand(Context context,String desc, String t, String d) {
        super(context, R.layout.expand_layout);
        description=desc;
        time=t;
        date=d;
    }
    public CustomCardExpand(Context context)
    {
        super(context, R.layout.expand_layout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve TextView elements
        TextView tx2 = (TextView) view.findViewById(R.id.description);
        TextView tx3 = (TextView) view.findViewById(R.id.time);
        TextView tx4 = (TextView) view.findViewById(R.id.date);
        //Set value in text views
//if(tx1!=null)
        tx2.setText(description);
        tx3.setText(time);
        tx4.setText(date);

    }
}

class UserReqCard extends Card{

    private String location_name, item_name;

    public UserReqCard(Context context, String item,String location){
        super(context, R.layout.user_request_card);
        item_name=item;
        location_name=location;
    }

    public UserReqCard(Context context){
        super(context, R.layout.user_request_card);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view){

        TextView location = (TextView)parent.findViewById(R.id.req_card_location);
        location.setText(location_name);

        TextView item = (TextView)parent.findViewById(R.id.itemName);
        item.setText(item_name);
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder().setupView(view);
        setViewToClickToExpand(viewToClickToExpand);

    }
}

