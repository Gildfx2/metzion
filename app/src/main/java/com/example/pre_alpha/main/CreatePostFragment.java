package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.util.Calendar;


public class CreatePostFragment extends Fragment {


    static final int ERROR_DIALOG_REQUEST= 9001;
    private static final String TAG = "CreatePostFragment";

    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};
    String name, item;
    double latitude, longitude;
    int radius;
    TextView tvState;
    TextInputEditText etName;
    ImageView mapIv, checkPickLocation;
    DatePickerDialog datePickerDialog;
    Slider radiusSlider;
    Button next, dateButton;
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
        dateButton=view.findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        pickItem.setAdapter(adapterItems);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(checkState.equals("lost")) tvState.setText("מה איבדת?");
        else tvState.setText("מה מצאת?");


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(getArguments()!=null){
            etName.setText(getArguments().getString("state_name", ""));
            pickItem.setText(getArguments().getString("state_item", ""));
            dateButton.setText(getArguments().getString("state_date", getTodaysDate()));

        }
        if(isServicesOk()){
            init();
        }
        initDatePicker();
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
                bundle.putString("state_name", etName.getText().toString());
                bundle.putString("state_item", pickItem.getText().toString());
                bundle.putString("state_date", dateButton.getText().toString());
                mapFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, mapFragment).commit();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
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
                    Log.d(TAG, "onClick: radius is " + radius);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("item", item);
                    bundle.putDouble("latitude", latitude);
                    bundle.putDouble("longitude", longitude);
                    bundle.putInt("radius", radius);
                    bundle.putString("date", dateButton.getText().toString());
                    createPostFragment2.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment2).commit();
                }
            }
        });

    }


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month=month+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        if(month==1) return "JAN";
        if(month==2) return "FEB";
        if(month==3) return "MAR";
        if(month==4) return "APR";
        if(month==5) return "MAY";
        if(month==6) return "JUN";
        if(month==7) return "JUL";
        if(month==8) return "AUG";
        if(month==9) return "SEP";
        if(month==10) return "OCT";
        if(month==11) return "NOV";
        if(month==12) return "DEC";

        return "JAN";
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