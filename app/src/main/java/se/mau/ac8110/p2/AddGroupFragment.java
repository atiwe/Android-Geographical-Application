package se.mau.ac8110.p2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupFragment extends Fragment {
    private EditText etGroupName;
    private Button btnConfirmAddGroup;
    private TextView errorMessage;
    private Controller controller;

    public AddGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view) {
        etGroupName = view.findViewById(R.id.etGroupName);
        btnConfirmAddGroup = view.findViewById(R.id.btnConfirmAddGroup);
        btnConfirmAddGroup.setOnClickListener(new AddGroupButton());
        errorMessage = view.findViewById(R.id.tvErrorMessageAddGroup);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private class AddGroupButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String groupName = etGroupName.getText().toString();
            String[] groups = controller.getGroupsArray();
            boolean nameTaken = false;
            for (String group : groups) {
                if (groupName.equals(group)) {
                    nameTaken = true;
                }
            }
            if(groupName.isEmpty()){
                errorMessage.setText(getResources().getString(R.string.errorAddGroup));
                errorMessage.setVisibility(View.VISIBLE);
            } else if(nameTaken){
                errorMessage.setText(getResources().getString(R.string.groupNameTaken));
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                controller.registerGroup(etGroupName.getText().toString());
                controller.goGroupFragment(true);
                etGroupName.setText("");
            }
        }
    }
}
