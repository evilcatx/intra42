package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.adapters.SpinnerAdapterCursusAccent;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Share;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.plumillonforge.android.chipview.ChipView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserOverviewFragment
        extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnLongClickListener {

    final static private String STATE_SELECTED_CURSUS = "selected_cursus";
    @Nullable
    UserActivity activity;
    Users user;
    Friends friendsRelation;
    SwipeRefreshLayout swipeRefreshLayout;
    ViewGroup layoutPhone;
    ImageButton imageButtonSMS;
    ViewGroup layoutMail;
    ViewGroup layoutLocation;
    ImageView imageViewProfile;
    ImageButton imageButtonFriends;
    ProgressBar progressBarFriends;
    ChipView chipViewTags;
    TextView textViewName;
    TextView textViewMobile;
    TextView textViewMail;
    TextView textViewPosition;
    TextView textViewWallet;
    TextView textViewCorrectionPoints;
    TextView textViewPiscine;
    View linePool;
    LinearLayout linearLayoutPool;
    Spinner spinnerCursus;
    LinearLayout linearLayoutGrade;
    View viewSeparatorGrade;
    TextView textViewGrade;
    TextView textViewLvl;
    ProgressBar progressBarLevel;
    TextView textViewNoCursusAvailable;
    LinearLayout linearLayoutCursus;
    TextView textViewCursusDate;
    AppClass app;

    Callback<Friends> checkFriend = new Callback<Friends>() {
        @Override
        public void onResponse(Call<Friends> call, Response<Friends> response) {
            friendsRelation = null;
            if (Tools.apiIsSuccessfulNoThrow(response)) {
                friendsRelation = response.body();
                setButtonFriends(1);
            } else if (response.code() == 404)
                setButtonFriends(1);
            else
                setButtonFriends(-1);
        }

        @Override
        public void onFailure(Call<Friends> call, Throwable t) {
            setButtonFriends(-1);
        }
    };

    Callback<Friends> addFriend = new Callback<Friends>() {
        @Override
        public void onResponse(Call<Friends> call, Response<Friends> response) {
            if (Tools.apiIsSuccessfulNoThrow(response)) {
                friendsRelation = response.body();
                setButtonFriends(1);
            } else {
                setButtonFriends(-1);
                if (response.code() == 102)
                    Toast.makeText(activity, R.string.friends_info_api_data_processing, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Friends> call, Throwable t) {
            setButtonFriends(-1);
        }
    };

    Callback<Void> removeFriend = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (Tools.apiIsSuccessfulNoThrow(response)) {
                friendsRelation = null;
                setButtonFriends(1);
            } else
                setButtonFriends(-1);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            setButtonFriends(-1);
        }
    };

    private OnFragmentInteractionListener mListener;

    public UserOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserOverviewFragment.
     */
    public static UserOverviewFragment newInstance() {

        return new UserOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();
        user = activity.user;

        app = (AppClass) activity.getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_overview, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutPhone = view.findViewById(R.id.layoutPhone);
        imageButtonSMS = view.findViewById(R.id.imageButtonSMS);
        layoutMail = view.findViewById(R.id.layoutMail);
        layoutLocation = view.findViewById(R.id.layoutLocation);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageButtonFriends = view.findViewById(R.id.imageButtonFriends);
        progressBarFriends = view.findViewById(R.id.progressBarFriends);
        chipViewTags = view.findViewById(R.id.chipViewTags);

        textViewName = view.findViewById(R.id.textViewName);
        textViewMobile = view.findViewById(R.id.textViewMobile);
        textViewMail = view.findViewById(R.id.textViewMail);
        textViewPosition = view.findViewById(R.id.textViewPosition);
        textViewWallet = view.findViewById(R.id.textViewWallet);
        textViewCorrectionPoints = view.findViewById(R.id.textViewCorrectionPoints);
        linePool = view.findViewById(R.id.viewPool);
        linearLayoutPool = view.findViewById(R.id.linearLayoutPool);
        textViewPiscine = view.findViewById(R.id.textViewPiscine);

        linearLayoutCursus = view.findViewById(R.id.linearLayoutCursus);
        textViewNoCursusAvailable = view.findViewById(R.id.textViewNoCursusAvailable);
        spinnerCursus = view.findViewById(R.id.spinnerCursus);
        linearLayoutGrade = view.findViewById(R.id.linearLayoutGrade);
        viewSeparatorGrade = view.findViewById(R.id.viewSeparatorGrade);
        textViewGrade = view.findViewById(R.id.textViewGrade);
        textViewLvl = view.findViewById(R.id.textViewLvl);
        progressBarLevel = view.findViewById(R.id.progressBarLevel);
        textViewCursusDate = view.findViewById(R.id.textViewCursusDate);

        setView();

        if (savedInstanceState != null && spinnerCursus != null)
            spinnerCursus.setSelection(savedInstanceState.getInt(STATE_SELECTED_CURSUS), false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setView();
    }

    void setView() {

        if (activity == null || activity.user == null || isDetached() || !isAdded())
            return;
        user = activity.user;

        swipeRefreshLayout.setOnRefreshListener(this);

        setButtonFriends(0);
        ApiService42Tools api = app.getApiService42Tools();
        api.getFriend(user.id).enqueue(checkFriend);

        String name = user.displayName + " - " + user.login;
        textViewName.setText(name);
        if (user.phone == null || user.phone.isEmpty())
            layoutPhone.setVisibility(View.GONE);
        else
            textViewMobile.setText(user.phone);
        textViewMail.setText(user.email);

        StringBuilder strLocation = new StringBuilder();
        if (user.location != null) {
            strLocation.append(user.location);
        } else
            strLocation.append(getString(R.string.user_unavailable));
        if (user.campus != null && !user.campus.isEmpty()) {
            strLocation.append(" - ");
            String sep = "";
            for (Campus c : user.campus) {
                strLocation.append(sep).append(c.name);
                sep = " | ";
            }
        }
        textViewPosition.setText(strLocation);
        textViewWallet.setText(String.valueOf(user.wallet));
        textViewCorrectionPoints.setText(String.valueOf(user.correction_point));

        String pool = "";
        if (user.pool_month != null && !user.pool_month.isEmpty())
            pool += user.pool_month.substring(0, 1).toUpperCase() + user.pool_month.substring(1) + " - ";
        if (user.pool_year != null)
            pool += user.pool_year;
        if (user.pool_year != null || user.pool_month != null) {
            textViewPiscine.setText(pool);
            linearLayoutPool.setVisibility(View.VISIBLE);
            linePool.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPool.setVisibility(View.GONE);
            linePool.setVisibility(View.GONE);
        }

        layoutPhone.setOnClickListener(this);
        imageButtonSMS.setOnClickListener(this);
        layoutMail.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);

        layoutPhone.setOnLongClickListener(this);
        imageButtonSMS.setOnLongClickListener(this);
        layoutMail.setOnLongClickListener(this);
        layoutLocation.setOnLongClickListener(this);

        CursusUsers selected = user.getCursusUsersToDisplay(getContext());
        if (selected != null && user.cursusUsers != null) {
            linearLayoutCursus.setVisibility(View.VISIBLE);
            textViewNoCursusAvailable.setVisibility(View.GONE);
            SpinnerAdapterCursusAccent adapterUserCursus = new SpinnerAdapterCursusAccent(getActivity(), user.cursusUsers);
            spinnerCursus.setAdapter(adapterUserCursus);
            spinnerCursus.setOnItemSelectedListener(this);

            for (int i = 0; i < user.cursusUsers.size(); i++) {
                if (user.cursusUsers.get(i).cursusId == selected.cursusId)
                    spinnerCursus.setSelection(i, false);
            }
        } else {
            linearLayoutCursus.setVisibility(View.GONE);
            textViewNoCursusAvailable.setVisibility(View.VISIBLE);
        }

        UserImage.setImage(getContext(), user, imageViewProfile);
        Tag.setTagUsers(getContext(), user.groups, chipViewTags);

        swipeRefreshLayout.setRefreshing(false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (spinnerCursus != null)
            outState.putInt(STATE_SELECTED_CURSUS, spinnerCursus.getSelectedItemPosition());
    }

    @Override
    public void onClick(View v) {
        if (v == layoutPhone) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.phone));
            startActivity(intent);
        } else if (v == imageButtonSMS) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + user.phone));
            startActivity(sendIntent);
        } else if (v == layoutMail) {
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?to=" + user.email);
            testIntent.setData(data);
            startActivity(testIntent);
        } else if (v == layoutLocation) {
            LocationHistoryActivity.openItWithUser(getContext(), user);
        } else if (v == imageButtonFriends) {
            ApiService42Tools api = app.getApiService42Tools();

            setButtonFriends(0);
            if (friendsRelation == null) { //add
                Call<Friends> friendsCall = api.addFriend(user.id);
                friendsCall.enqueue(addFriend);
            } else {
                api.deleteFriend(friendsRelation.id).enqueue(removeFriend);
            }
        }
    }

    /**
     * 0 -> loading;
     * <p>
     * 1 -> normal state;
     * <p>
     * -1 -> error on loading;
     *
     * @param state Current state;
     */
    void setButtonFriends(int state) {

        if (state == 0) {
            progressBarFriends.setVisibility(View.VISIBLE);
            imageButtonFriends.setVisibility(View.GONE);
        } else {
            progressBarFriends.setVisibility(View.GONE);
            imageButtonFriends.setVisibility(View.VISIBLE);
        }

        if (state == 1) {
            imageButtonFriends.setOnClickListener(this);
            imageButtonFriends.setOnLongClickListener(this);
            if (friendsRelation != null)
                imageButtonFriends.setColorFilter(Color.argb(255, 247, 202, 24));
            else
                imageButtonFriends.setColorFilter(Color.argb(255, 255, 255, 255));
            return;
        }

        imageButtonFriends.setOnClickListener(null);
        imageButtonFriends.setOnLongClickListener(null);

        if (state == -1)
            imageButtonFriends.setColorFilter(Color.argb(200, 150, 150, 150));
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (user.cursusUsers == null)
            return;
        CursusUsers userCursus = user.cursusUsers.get(position);
        if (activity != null) {
            activity.selectedCursus = userCursus;
        }

        if (userCursus.grade == null) {
            linearLayoutGrade.setVisibility(View.GONE);
            viewSeparatorGrade.setVisibility(View.GONE);
        } else {
            linearLayoutGrade.setVisibility(View.VISIBLE);
            viewSeparatorGrade.setVisibility(View.VISIBLE);
            textViewGrade.setText(userCursus.grade);
        }
        textViewLvl.setText(String.valueOf(userCursus.level));
        progressBarLevel.setProgress((int) (userCursus.level / 21.0 * 100.0));

        String dateInfo;

        if (userCursus.begin_at == null && userCursus.end_at == null)
            dateInfo = getString(R.string.user_overview_cursus_date_not_start_not_finised);
        else {
            if (userCursus.begin_at != null)
                dateInfo = DateTool.getDateLong(userCursus.begin_at);
            else
                dateInfo = getString(R.string.user_overview_cursus_date_not_start);

            dateInfo += " • ";

            if (userCursus.end_at != null)
                dateInfo += DateTool.getDateLong(userCursus.end_at);
            else
                dateInfo += getString(R.string.user_overview_cursus_date_not_finished);
        }
        textViewCursusDate.setText(dateInfo);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRefresh() {
        if (activity != null)
            activity.refresh(new Runnable() {
                @Override
                public void run() {
                    setView();
                }
            });
    }

    @Override
    public boolean onLongClick(View v) {

        if (v == layoutPhone)
            dialogCopyOrShare(user.phone);
        else if (v == imageButtonSMS)
            dialogCopyOrShare(user.phone);
        else if (v == layoutMail)
            dialogCopyOrShare(user.email);
        else if (v == layoutLocation)
            dialogCopyOrShare(user.location);
        else
            return false;
        return true;
    }

    void dialogCopyOrShare(final String string) {
        final Context context = getContext();
        CharSequence action[] = new CharSequence[]{
                context.getString(R.string.copy),
                context.getString(R.string.navigation_share)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(string);
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    Share.copyString(context, string);
                else
                    Share.shareString(activity, string);
            }
        });
        builder.show();
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
        void onFragmentInteraction(Uri uri);
    }
}
