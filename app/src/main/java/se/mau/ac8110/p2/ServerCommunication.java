package se.mau.ac8110.p2;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

class ServerCommunication {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    private Controller controller;
    private String ip;
    private int port;
    private String id;
    private boolean sendingLocation;

    ServerCommunication(Controller controller, String ip, int port) {
        this.controller = controller;
        this.ip = ip;
        this.port = port;
        Thread thread = new ServerListener();
        thread.start();
    }

    private String registerGroup(String group, String member) {
        String message;

        JSONObject item = new JSONObject();
        try {
            item.put("type", "register");
            item.put("group", group);
            item.put("member", member);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message = item.toString();
        return message;
    }

    private String unregisterGroup() {
        String message;

        JSONObject item = new JSONObject();
        try {
            item.put("type", "unregister");
            item.put("ID", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message = item.toString();
        return message;
    }

    private String currentGroups() {
        String message;

        JSONObject item = new JSONObject();
        try {
            item.put("type", "groups");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message = item.toString();
        return message;
    }

    private String sendLocation(double longitude, double latitude) {
        String message;
        JSONObject item = new JSONObject();
        try {
            item.put("type", "location");
            item.put("id", id);
            item.put("longitude", "" + longitude);
            item.put("latitude", "" + latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message = item.toString();
        return message;
    }

    private String groupMembers(String groupName) {
        String message;
        JSONObject item = new JSONObject();
        try {
            item.put("type", "members");
            item.put("group", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message = item.toString();
        return message;
    }

    void disconnect() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void getGroups() {
        Thread thread = new RequestGroupsFromServer();
        thread.start();
    }

    void registerGroupOnServer(String group, String name) {
        Thread thread = new RegisterGroupThread();
        ((RegisterGroupThread) thread).setStrings(group, name);
        thread.start();
    }

    void unregisterGroupOnServer() {
        Thread thread = new UnRegisterGroupThread();
        thread.start();
    }

    void requestGroupInfo(String groupName) {
        Thread thread = new RequestGroupInfoFromServer(groupName);
        thread.start();
    }

    public class RequestGroupInfoFromServer extends Thread {
        private String groupName;

        RequestGroupInfoFromServer(String groupName) {
            this.groupName = groupName;
        }

        public void run() {
            try {
                dos.writeUTF(groupMembers(groupName));
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class UnRegisterGroupThread extends Thread {

        public void run() {
            try {
                dos.writeUTF(unregisterGroup());
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class RegisterGroupThread extends Thread {
        String group;
        String name;

        void setStrings(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public void run() {
            try {
                dos.writeUTF(registerGroup(group, name));
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class RequestGroupsFromServer extends Thread {
        public void run() {
            if (socket != null) {
                try {
                    dos.writeUTF(currentGroups());
                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ServerListener extends Thread {
        public void run() {
            InputStream is = null;
            try {
                socket = new Socket(ip, port);
                is = socket.getInputStream();
                dis = new DataInputStream(is);
                OutputStream os = socket.getOutputStream();
                dos = new DataOutputStream(os);

                readFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void readFromServer() {
            while (!socket.isClosed()) {
                try {
                    String message2 = dis.readUTF();
                    JSONObject jsonObject = new JSONObject(message2);
                    String type = jsonObject.getString("type");
                    Log.d(TAG, "Message from server, type: " + type);
                    switch (type) {
                        case "groups":
                            processJsonGroups(jsonObject);
                            break;
                        case "register":
                            processJsonRegister(jsonObject);
                            break;
                        case "unregister":
                            processJsonUnregister(jsonObject);
                            break;
                        case "locations":
                            processJsonLocations(jsonObject);
                            break;
                        case "location":
                            processJsonLocation(jsonObject);
                            break;
                        case "members":
                            processJsonMembers(jsonObject);
                            break;
                        case "exception":
                            controller.setServerError(jsonObject.toString());
                            break;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processJsonUnregister(JSONObject jsonObject) {
            controller.showMessage(jsonObject.toString());
        }

        private void processJsonMembers(JSONObject jsonObject) {
            try {
                String groupName = jsonObject.getString("group");
                JSONArray jsonArray = jsonObject.getJSONArray("members");
                String[] members = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    String memberJson = jsonArray.getString(i);
                    JSONObject jsonObjectMember = new JSONObject(memberJson);
                    members[i] = jsonObjectMember.getString("member");
                }
                controller.setMembers(groupName, members);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void processJsonLocations(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("location");
                MemberInfo[] memberInfo = new MemberInfo[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    String memberJson = jsonArray.getString(i);
                    JSONObject jsonObjectMember = new JSONObject(memberJson);
                    String memberName = jsonObjectMember.getString("member");
                    double memberLongitude = jsonObjectMember.getDouble("longitude");
                    double memberLatitude = jsonObjectMember.getDouble("latitude");
                    memberInfo[i] = new MemberInfo(memberName, memberLongitude, memberLatitude);
                }
                Log.d(TAG, "Locations from server: " + jsonObject.toString());
                controller.setGroupLocations(memberInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void processJsonLocation(final JSONObject jsonObject) {
            Log.d(TAG, jsonObject.toString());
        }

        private void processJsonGroups(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("groups");
                ArrayList<String> groupsArray = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String test = jsonArray.getString(i);
                    JSONObject jsonObject1 = new JSONObject(test);
                    groupsArray.add(jsonObject1.getString("group"));
                }
                controller.setListView(groupsArray);
                Log.d("ArrayList", "Setting ListView with: " + groupsArray.size() + " elements");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void processJsonRegister(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("id");
            sendingLocation = true;
            Thread thread = new SendLocationThread();
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void stopSendLocation() {
        sendingLocation = false;
    }

    private class SendLocationThread extends Thread {

        public void run() {
            while (sendingLocation) {
                double[] location = controller.getLocation();
                try {
                    dos.writeUTF(sendLocation(location[0], location[1]));
                    dos.flush();
                    sleep(20000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
