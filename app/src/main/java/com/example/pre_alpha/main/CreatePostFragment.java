package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CreatePostFragment extends Fragment {


    private static final int ERROR_DIALOG_REQUEST= 9001;
    private static final String TAG = "CreatePostFragment";

    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};
    String name, item;
    double latitude, longitude;
    int radius;
    TextView tvState;
    TextInputEditText etName;
    ImageView mapIv, checkPickLocation;
    Slider radiusSlider;
    Button next;
    TextInputLayout layoutItem;
    AutoCompleteTextView pickItem;
    ArrayAdapter<String> adapterItems;
    CreatePostFragment2 createPostFragment2 =new CreatePostFragment2();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvState=view.findViewById(R.id.message_state);
        etName = view.findViewById(R.id.name);
        mapIv=view.findViewById(R.id.select_location);
        radiusSlider=view.findViewById(R.id.radius_slider);
        next=view.findViewById(R.id.next);
        adapterItems = new ArrayAdapter<>(getActivity(),R.layout.list_item,items);
        pickItem = view.findViewById(R.id.list_of_items);
        layoutItem = view.findViewById(R.id.item);
        checkPickLocation=view.findViewById(R.id.pick_location_check);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(checkState.equals("lost")) tvState.setText("מה איבדת?");
        else tvState.setText("מה מצאת?");
        pickItem.setAdapter(adapterItems);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isServicesOk()){
            init();
        }
    }

    private void init(){
        if(this.getArguments()!=null) {
            latitude = this.getArguments().getDouble("latitude");
            longitude = this.getArguments().getDouble("longitude");
            if(latitude!=0 && longitude!=0)  checkPickLocation.setImageResource(R.drawable.baseline_library_add_check_24);
        }

        mapIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from_where", "create_post");
                mapFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, mapFragment).commit();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutItem.setHelperText("");
                name=etName.getText().toString();
                item=pickItem.getText().toString();
                if(item.isEmpty()){
                    layoutItem.setHelperText("שדה חובה");
                }
                if(!name.isEmpty() && !item.isEmpty() && name.length()<=30 && latitude!=0 && longitude!=0) {
                    radius= (int) radiusSlider.getValue();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("item", item);
                    bundle.putDouble("latitude", latitude);
                    bundle.putDouble("longitude", longitude);
                    bundle.putInt("radius", radius);
                    createPostFragment2.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment2).commit();
                }
            }
        });

    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOk: google play services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getActivity(), "you can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}