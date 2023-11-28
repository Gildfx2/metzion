package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.pre_alpha.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CreatePostFragment extends Fragment {

    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "כובע חרדי / שטריימל", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};
    String[] areas={"ירושלים", "תל אביב-יפו","חיפה","ראשון לציון", "פתח תקווה", "אשדוד", "נתניה", "באר שבע", "בני ברק", "חולון", "רמת גן", "אשקלון", "רחובות", "בית שמש", "בת ים", "כפר סבא","הרצליה", "חדרה", "מודיעין", "לוד", "נצרת", "רמלה", "רעננה", "רהט", "ראש העין", "הוד השרון", "ביתר עילית", "גבעתיים", "קריית אתא", "נהריה", "קריית גת", "אום אל-פחם", "עפולה", "אילת", "נס ציונה", "עכו", "יבנה", "אלעד", "רמת השרון", "כרמיאל", "טבריה", "קריית מוצקין",
            "טייבה", "שפרעם", "נוף הגליל", "קריית ביאליק", "קריית אונו", "קריית ים", "נתיבות", "מעלה אדומים", "אור יהודה", "צפת", "דימונה", "טמרה", "אופקים", "סח'נין", "באקה אל-גרבייה", "יהוד-מונוסון", "שדרות", "באר יעקב", "גבעת שמואל", "ערד", "טירה", "עראבה", "כפר יונה", "מגדל העמק", "קריית מלאכי", "כפר קאסם", "טירת כרמל", "יקנעם עילית", "נשר", "קלנסווה", "קריית שמונה", "מעלות-תרשיחא", "אריאל", "אור עקיבא", "בית שאן"};
    String name, item, area, about;
    TextView tvState;
    TextInputEditText etName, etAbout;
    Button next;
    TextInputLayout layoutItem, layoutArea;
    AutoCompleteTextView pickItem, pickArea;
    ArrayAdapter<String> adapterItems, adapterAreas;
    SelectImageFragment selectImageFragment=new SelectImageFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvState=view.findViewById(R.id.message_state);
        next=view.findViewById(R.id.next);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        adapterAreas = new ArrayAdapter<String>(getActivity(),R.layout.list_item,areas);
        pickItem = view.findViewById(R.id.list_of_items);
        pickArea = view.findViewById(R.id.list_of_areas);
        layoutItem = view.findViewById(R.id.item);
        layoutArea = view.findViewById(R.id.area);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(checkState.equals("lost")) tvState.setText("מה איבדת?");
        else tvState.setText("מה מצאת?");
        etName = view.findViewById(R.id.name);
        etAbout = view.findViewById(R.id.about);
        pickItem.setAdapter(adapterItems);
        pickArea.setAdapter(adapterAreas);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutItem.setHelperText("");
                layoutArea.setHelperText("");
                name=etName.getText().toString();
                item=pickItem.getText().toString();
                area=pickArea.getText().toString();
                about=etAbout.getText().toString();
                if(item.isEmpty()){
                    layoutItem.setHelperText("שדה חובה");
                }
                if(area.isEmpty() || !areaExist(areas, area)) {
                    layoutArea.setHelperText("יש לבחור ישוב מתוך הרשימה");
                }
                if(!name.isEmpty() && !item.isEmpty() && !area.isEmpty() && areaExist(areas,area) && name.length()<=30 && about.length()<=150) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("item", item);
                    bundle.putString("area", area);
                    bundle.putString("about", about);
                    selectImageFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, selectImageFragment).commit();
                }
            }
        });
        return view;
    }

    private boolean areaExist(String[] areas, String area) {
        for(String str : areas){
            if(str.equals(area)) return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        etName.setText("");
        pickItem.setText("");
        pickArea.setText("");
        etAbout.setText("");
        super.onPause();
    }
}