package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.NotificationListAdapter;
import usthb.lfbservices.com.pfe.adapters.TouchNotificationAdapter;
import usthb.lfbservices.com.pfe.models.Notification;

/**
 * Created by ryadh on 06/05/18.
 */

public class FragmentNotifications extends Fragment {

    private static final String TAG = "FragmentNotifications";


    private AppRoomDatabase db;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private NotificationListAdapter adapter;
    private List<Notification> notifications;
    private NotificationsActions implementation;
    private View rootView;
    private FragmentActivity fragmentBelongActivity;

    public FragmentNotifications() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        fragmentBelongActivity = getActivity();


        if (rootView != null) {
            initVariables();

            notifications= db.notificationDao().getAll();
            adapter = new NotificationListAdapter(notifications);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            if (adapter.getItemCount() ==0) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(R.string.no_notification_saved);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

            }

            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            ItemTouchHelper.Callback callback = new TouchNotificationAdapter(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

        }



        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (NotificationsActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NotificationsActions");
        }
    }

    public void initVariables() {

        recyclerView = rootView.findViewById(R.id.recyclerview_Notification);
        emptyView =rootView.findViewById(R.id.empty_list_notification);
        db = AppRoomDatabase.getInstance(getActivity());


    }

    public interface NotificationsActions {

    }
}






