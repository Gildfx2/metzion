package com.example.pre_alpha.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

public class FilterFragment extends Fragment {

    private static final String TAG = "FilterFragment";
    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};

    Button fromDatePickerButton, toDatePickerButton, showResults;
    ImageView mapIv, checkPickLocation;
    AutoCompleteTextView pickItem;
    ArrayAdapter<String> adapterItems;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    Chip lostFilter, foundFilter;
    ChipGroup filterGroup;
    String lostOrFound="";
    double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        //initializing
        fromDatePickerButton=view.findViewById(R.id.from_date_picker_button);
        toDatePickerButton=view.findViewById(R.id.to_date_picker_button);
        mapIv=view.findViewById(R.id.select_location);
        pickItem=view.findViewById(R.id.filter_list_of_items);
        showResults=view.findViewById(R.id.show_results);
        lostFilter=view.findViewById(R.id.filter_lost_items);
        foundFilter=view.findViewById(R.id.filter_found_items);
        checkPickLocation=view.findViewById(R.id.pick_location_check);
        filterGroup=view.findViewById(R.id.filter_group);
        adapterItems = new ArrayAdapter<>(getActivity(),R.layout.list_item,items);
        pickItem.setAdapter(adapterItems);
        fromDatePickerButton.setText(getTodaysDate());
        toDatePickerButton.setText(getTodaysDate());
        return view;
    }

    public void onResume() {
        super.onResume();
        if(getArguments()!=null){
            lostOrFound = getArguments().getString("lost_or_found", "");
            if(lostOrFound.equals("lost")) lostFilter.setChecked(true);
            if(lostOrFound.equals("found")) foundFilter.setChecked(true);
            pickItem.setText(getArguments().getString("state_item", ""));
            fromDatePickerButton.setText(getArguments().getString("state_date_from", getTodaysDate()));
            toDatePickerButton.setText(getArguments().getString("state_date_to", getTodaysDate()));
        }
        if(isServicesOk()){ //checking ig the services that runs the map are working fine
            init();
        }
        initDatePicker1(); //init the "from" date picker
        initDatePicker2(); //init the "to" date picker
    }

    private void init(){
        if(this.getArguments()!=null) { //getting the landmark
            latitude = this.getArguments().getDouble("latitude");
            longitude = this.getArguments().getDouble("longitude");
            if(latitude!=0 && longitude!=0)  checkPickLocation.setImageResource(R.drawable.baseline_library_add_check_24);
        }
        fromDatePickerButton.setOnClickListener(new View.OnClickListener() { //showing the "from" date picker dialog
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        toDatePickerButton.setOnClickListener(new View.OnClickListener() { //showing the "to" date picker dialog
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        mapIv.setOnClickListener(new View.OnClickListener() { //moving to the map fragment
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                if(!lostFilter.isChecked() && !foundFilter.isChecked()) lostOrFound="";
                bundle.putString("from_where", "filter_posts");
                bundle.putString("lost_or_found", lostOrFound);
                bundle.putString("state_item", pickItem.getText().toString());
                bundle.putString("state_date_from", fromDatePickerButton.getText().toString());
                bundle.putString("state_date_to", toDatePickerButton.getText().toString());
                mapFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, mapFragment).commit();
            }
        });

        showResults.setOnClickListener(new View.OnClickListener() { //moving to the screen that shows the results after the filtering
            @Override
            public void onClick(View v) {
                if(lostOrFound.isEmpty()) Toast.makeText(getActivity() ,"יש לבחור אם החפץ המבוקש הוא מציאה או אבידה", Toast.LENGTH_SHORT).show();
                else{
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    if(!lostFilter.isChecked() && !foundFilter.isChecked()) lostOrFound="";
                    bundle.putString("lost_or_found", lostOrFound);
                    bundle.putString("state_item", pickItem.getText().toString());
                    bundle.putString("state_date_from", fromDatePickerButton.getText().toString());
                    bundle.putString("state_date_to", toDatePickerButton.getText().toString());
                    bundle.putDouble("latitude", latitude);
                    bundle.putDouble("longitude", longitude);
                    searchFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment).commit();
                }
            }
        });

        filterGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() { //make sure that the user can pick only lost or found filter
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (checkedId == foundFilter.getId()) {
                    if(foundFilter.isChecked()) lostOrFound="found";
                }
                else if (checkedId == lostFilter.getId()) {
                    if(lostFilter.isChecked()) lostOrFound="lost";
                }

            }
        });


    }

    private void initDatePicker1(){ //init the "from" date picker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = makeDateString(dayOfMonth, month, year);
                fromDatePickerButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        fromDatePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


    }

    private void initDatePicker2(){ //init the "to" date picker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = makeDateString(dayOfMonth, month, year);
                toDatePickerButton.setText(date);
                String[] dateComponents = fromDatePickerButton.getText().toString().split(" ");
                int monthMax = getMonthFormatFromWord(dateComponents[0]) - 1; // Calendar months are 0-based
                int dayOfMonthMax = Integer.parseInt(dateComponents[1]);
                int yearMax = Integer.parseInt(dateComponents[2]);
                Calendar calender2 = Calendar.getInstance();
                calender2.set(Calendar.YEAR, yearMax);
                calender2.set(Calendar.MONTH, monthMax);
                calender2.set(Calendar.DAY_OF_MONTH, dayOfMonthMax);
                Calendar calender1 = Calendar.getInstance();
                calender1.set(Calendar.YEAR, year);
                calender1.set(Calendar.MONTH, month-1);
                calender1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(calender2.after(calender1)) fromDatePickerButton.setText(toDatePickerButton.getText().toString());
                fromDatePickerDialog.getDatePicker().setMaxDate(calender1.getTimeInMillis());
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        toDatePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        toDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String getTodaysDate() { //getting today's date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month=month+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year){ //transferring the date into string
        return getMonthFormatFromNumber(month) + " " + day + " " + year;
    }

    private String getMonthFormatFromNumber(int month){ //converting the month's number into abbreviation
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

    private int getMonthFormatFromWord(String month){ //converting the month's abbreviation into number
        if(month.equals("JAN")) return 1;
        if(month.equals("FEB")) return 2;
        if(month.equals("MAR")) return 3;
        if(month.equals("APR")) return 4;
        if(month.equals("MAY")) return 5;
        if(month.equals("JUN")) return 6;
        if(month.equals("JUL")) return 7;
        if(month.equals("AUG")) return 8;
        if(month.equals("SEP")) return 9;
        if(month.equals("OCT")) return 10;
        if(month.equals("NOV")) return 11;
        if(month.equals("DEC")) return 12;

        return 1;
    }

    public boolean isServicesOk(){ //checking ig the services that running the map are working
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOk: google play services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, CreatePostFragment.ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getActivity(), "you can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        foundFilter.setChecked(false);
        lostFilter.setChecked(false);
        pickItem.setText("");
    }
}