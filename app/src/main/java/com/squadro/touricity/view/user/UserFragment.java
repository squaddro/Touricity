package com.squadro.touricity.view.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squadro.touricity.R;


public class UserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_page_view, container, false);
        TextView signUp = (TextView) v.findViewById(R.id.link_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                v.findViewById(R.id.signin_layout).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.signup_layout).setVisibility(View.VISIBLE);
            }
        });

        TextView login = (TextView) v.findViewById(R.id.link_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                v.findViewById(R.id.signin_layout).setVisibility(View.VISIBLE);
                v.findViewById(R.id.signup_layout).setVisibility(View.INVISIBLE);
            }
        });
        return v;
    }


}