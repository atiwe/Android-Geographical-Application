package se.mau.ac8110.p2;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private TextView tvInfo;
    private ListView lvGroups;
    private Controller controller;
    private TextView tvErrorFromServer;
    private String latestErrorMessage;
    private Button btnAddGroup;
    private Button btnChangeName;

    public GroupFragment() {
        // Required empty public constructor
    }

    public void runOnUIThread(Runnable runnable){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initializeComponents(view);
        return view;
    }

    public void onResume(){
        super.onResume();
        controller.getGroups();
        controller.setTab("Group");
        setErrorMessage(latestErrorMessage);
    }

    private void initializeComponents(View view) {
        btnAddGroup = (Button) view.findViewById(R.id.btnAddGroup);
        btnChangeName = (Button) view.findViewById(R.id.btnChangeName);
        btnAddGroup.setOnClickListener(new addGroupListener());
        btnChangeName.setOnClickListener(new addGroupListener());
        tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        lvGroups = (ListView) view.findViewById(R.id.lvGroups);
        tvErrorFromServer = view.findViewById(R.id.tvErrorFromServer);
    }

    public void setErrorMessage(final String errorMessage){
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                tvErrorFromServer.setText(errorMessage);
            }
        });
        latestErrorMessage = errorMessage;
    }

    public void setArrayAdapter(ArrayList<String> groupArray){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.fragment_list_view, R.id.tvGroup, groupArray);
        lvGroups.setAdapter(arrayAdapter);
        lvGroups.setOnItemClickListener(new GroupListener());
    }

    public void setTvInfo(String text){
        tvInfo.setText(text);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private class GroupListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String groupName = parent.getItemAtPosition(position).toString();
            controller.goDetailedFragment(groupName);
        }
    }

    private class addGroupListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.equals(btnAddGroup)){
                controller.goAddGroupFragment();
            } else if(v.equals(btnChangeName)){
                controller.goUsernameFragment();
            }
        }
    }
}
