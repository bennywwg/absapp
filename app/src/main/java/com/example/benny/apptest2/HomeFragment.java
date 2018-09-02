package com.example.benny.apptest2;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    private Button logout_button;
    private View parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_home, container, false);

        assignVariables();

        setOnclicks();

        return parent;
    }

    private void assignVariables() {
        logout_button = (Button) parent.findViewById(R.id.logout_button);
    }

    public void setOnclicks(){
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    public void logout(){
        Util.deleteUserCredentials(getContext());
        Util.pageSwap(this.getActivity(), SplashScreen.class);
    }
}
