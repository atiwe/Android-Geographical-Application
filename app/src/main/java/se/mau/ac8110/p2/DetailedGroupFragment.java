package se.mau.ac8110.p2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedGroupFragment extends Fragment {
    private TextView tvGroupName;
    private ListView lvMembers;
    private Button btnLeaveGroup;
    private Button btnJoinGroup;
    private Controller controller;
    private ArrayList<String> membersArray;
    private TextView tvNameTaken;

    public DetailedGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_group, container, false);
        initializeComponents(view);
        return view;
    }

    public void onResume(){
        super.onResume();
        controller.setTab("Group");
    }

    private void initializeComponents(View view) {
        tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
        lvMembers = (ListView) view.findViewById(R.id.lvMembers);
        btnLeaveGroup = (Button) view.findViewById(R.id.btnLeaveGroup);
        btnJoinGroup = (Button) view.findViewById(R.id.btnJoinGroup);
        btnLeaveGroup.setOnClickListener(new ButtonListener());
        btnJoinGroup.setOnClickListener(new ButtonListener());
        tvNameTaken = (TextView) view.findViewById(R.id.tvNameTaken);
    }

    private void setListView(ArrayList<String> members){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()).getApplicationContext(), R.layout.fragment_list_view, R.id.tvGroup, members);
        lvMembers.setAdapter(arrayAdapter);
    }

    public void setMembers(String groupName, String[] members) {
        tvGroupName.setText(groupName);
        membersArray = new ArrayList<>();
        Collections.addAll(membersArray, members);
        setListView(membersArray);
    }

    public void ableToJoinGroup(Boolean ableToJoin){
        if(ableToJoin){
            btnJoinGroup.setEnabled(true);
            btnLeaveGroup.setEnabled(false);
        }else{
            btnJoinGroup.setEnabled(false);
            btnLeaveGroup.setEnabled(true);
        }
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.equals(btnLeaveGroup)){
                Log.d(TAG, "leavegroup");
                controller.unregisterFromGroup();
                ableToJoinGroup(true);
                controller.requestGroupInfo(tvGroupName.getText().toString());
            }else if(v.equals(btnJoinGroup)){
                if(nameTaken()){
                    tvNameTaken.setVisibility(View.VISIBLE);
                }else {
                    controller.registerGroup(tvGroupName.getText().toString());
                    ableToJoinGroup(false);
                    controller.requestGroupInfo(tvGroupName.getText().toString());
                }
            }
        }
        private boolean nameTaken(){
            String username = controller.getUserName();
            for(String member : membersArray){
                if(username.equals(member)){
                    return true;
                }
            }
            return false;
        }
    }

    public void setController(Controller controller){
        this.controller = controller;
    }
}
