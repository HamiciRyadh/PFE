package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import usthb.lfbservices.com.pfe.R;

/**
 * Created by ryadh on 06/05/18.
 */

public class FragmentNotifications extends Fragment {

    private static final String TAG = "FragmentNotifications";

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

        }

        initVariables();

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

    }

    public interface NotificationsActions {

    }
}
