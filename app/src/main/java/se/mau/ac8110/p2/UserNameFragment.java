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
public class UserNameFragment extends Fragment {
    private EditText etUsernameInput;
    private TextView tvErrorUsername;
    private Controller controller;

    public UserNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_name, container, false);
        Button btnAddUsername = (Button) view.findViewById(R.id.btnAddUsername);
        etUsernameInput = (EditText) view.findViewById(R.id.etUsernameInput);
        btnAddUsername.setOnClickListener(new AddUsername());
        tvErrorUsername = (TextView) view.findViewById(R.id.tvErrorUsername);
        return view;
    }

    private class AddUsername implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(etUsernameInput.getText().toString().isEmpty()){
                tvErrorUsername.setVisibility(View.VISIBLE);
            }else {
                controller.setUsername(etUsernameInput.getText().toString());
                controller.showTabLayout();
                controller.goGroupFragment(false);
            }
        }
    }

    public void setController(Controller controller){
        this.controller = controller;
    }
}
