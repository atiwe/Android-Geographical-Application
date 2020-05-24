package se.mau.ac8110.p2;

import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import static android.content.ContentValues.TAG;

public class Controller {
    private MainActivity mainActivity;
    private TabLayout tabLayout;
    private UserNameFragment userNameFragment;
    private GroupFragment groupFragment;
    private SupportMapFragment mapFragment;
    private DetailedGroupFragment detailedGroupFragment;
    private MapController mapController;
    private ServerCommunication serverCommunication;
    private AddGroupFragment addGroupFragment;
    private GPSHandler gpsHandler;
    private String currentFragment;
    private String currentGroup = "";
    private String userName = "";
    private boolean isInGroup = false;
    private String[] groups;

    Controller(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        serverCommunication = new ServerCommunication(this,"195.178.227.53", 7117);
        initializeUIComponents();
        gpsHandler = new GPSHandler(mainActivity);
    }

    private void initializeUIComponents() {
        tabLayout = mainActivity.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabListener());
        tabLayout.setVisibility(View.GONE);
        userNameFragment = new UserNameFragment();
        userNameFragment.setController(this);
        groupFragment = new GroupFragment();
        groupFragment.setController(this);
        detailedGroupFragment = new DetailedGroupFragment();
        detailedGroupFragment.setController(this);
        mapFragment = SupportMapFragment.newInstance();
        mapController = new MapController(mapFragment);
        mainActivity.setNewFragment(userNameFragment, false);
        addGroupFragment = new AddGroupFragment();
        addGroupFragment.setController(this);
        currentFragment = "groups";
    }

    void showTabLayout(){
        tabLayout.setVisibility(View.VISIBLE);
    }

    void showMessage(String message) {
        groupFragment.setTvInfo(message);
    }

    String getUserName(){
        return userName;
    }

    private void runOnUIThread(Runnable runnable){
        groupFragment.runOnUIThread(runnable);
    }

    void setListView(final ArrayList<String> groups){
        this.groups = new String[groups.size()];
        for(int i = 0; i<groups.size(); i++){
            this.groups[i] = groups.get(i);
        }
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                groupFragment.setArrayAdapter(groups);
            }
        });
    }

    String[] getGroupsArray(){
        return groups;
    }

    void goDetailedFragment(String groupName){
        requestGroupInfo(groupName);
        mainActivity.setNewFragment(detailedGroupFragment, true);
    }

    void goAddGroupFragment() {
        mainActivity.setNewFragment(addGroupFragment, true);
    }

    void exit() {
        unregisterFromGroup();
        serverCommunication.disconnect();
    }

    void registerGroup(String group) {
        currentGroup = group;
        if(isInGroup){
            serverCommunication.unregisterGroupOnServer();
        }
        serverCommunication.registerGroupOnServer(group, userName);
        isInGroup = true;
    }

    void goGroupFragment(Boolean backstack) {
        mainActivity.setNewFragment(groupFragment, backstack);
        serverCommunication.getGroups();
    }

    void goUsernameFragment(){
        mainActivity.setNewFragment(userNameFragment, true);
    }

    double[] getLocation(){
        return gpsHandler.getLocation();
    }

    void setGroupLocations(MemberInfo[] memberInfo) {
        mapController.setMarkers(memberInfo);
        if(currentFragment.equals("map")){
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mapController.updateMap();
                }
            });
        }
    }

    void requestGroupInfo(String groupName){
        serverCommunication.requestGroupInfo(groupName);
    }

    void getGroups(){
        serverCommunication.getGroups();
    }

    void setMembers(final String groupName, final String[] members) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                detailedGroupFragment.setMembers(groupName, members);
                if(groupName.equals(currentGroup)){
                    detailedGroupFragment.ableToJoinGroup(false);
                }else{
                    detailedGroupFragment.ableToJoinGroup(true);
                }
            }
        });
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    void unregisterFromGroup() {
        serverCommunication.unregisterGroupOnServer();
        serverCommunication.stopSendLocation();
        isInGroup = false;
        currentGroup = "";
        mapController.setMarkers(new MemberInfo[0]);
        mapController.updateMap();
    }

    void setTab(String tab){
        if(tab=="Group"){
            tabLayout.getTabAt(0).select();
        }else if(tab=="Map"){
            tabLayout.getTabAt(1).select();
        }
    }

    void setServerError(String error) {
        groupFragment.setErrorMessage(error);
    }

    private class TabListener implements TabLayout.BaseOnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(tab.getText().equals(mainActivity.getResources().getString(R.string.groups))){
                mainActivity.setNewFragment(groupFragment, true);
                currentFragment = "groups";
            }
            else if(tab.getText().equals(mainActivity.getResources().getString(R.string.map))){
                mainActivity.setNewFragment(mapFragment,true);
                currentFragment = "map";
                mapController.updateMap();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if(Objects.equals(tab.getText(), mainActivity.getResources().getString(R.string.groups)) && (!currentFragment.equals("groups"))){
                mainActivity.setNewFragment(groupFragment, true);
                currentFragment = "groups";
            }
        }
    }
}
