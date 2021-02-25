package com.example.fireoperationmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoView photoView;
    private Map<Integer, Map<Integer, Place>> sectionData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        createMapView();
        initializeAdapterAndRecyclerView();
        createSearchView();
    }

    private void initializeAdapterAndRecyclerView() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference sectionRef = FirebaseDatabase.getInstance().getReference().child("Section");
        adapter = new CustomAdapter();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        //파에어베이스에서 adapter.userList로 데이터를 불러옴
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.init();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    adapter.addUser(user);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        sectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sectionData = new HashMap<>();
                for(DataSnapshot sectionData : snapshot.getChildren()) {
                    int sectionKey = Integer.parseInt(sectionData.getKey().split("_")[1]);
                    if (!MainActivity.this.sectionData.containsKey(sectionKey)) {
                        MainActivity.this.sectionData.put(sectionKey, new HashMap<>());
                    }
                    for (DataSnapshot placeData : sectionData.getChildren()) {
                        Place place = placeData.getValue(Place.class);
                        int placeKey = Integer.parseInt(placeData.getKey().split("_")[1]);
                        MainActivity.this.sectionData.get(sectionKey).put(placeKey, place);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //아이템 클릭시 이벤트 설정
        adapter.setOnItemClickListener((view, position) -> {
            User user = adapter.getItem(position);
            int sectionNum = Integer.parseInt(user.getId().split("-")[0]);
            int placeNum = Integer.parseInt(user.getId().split("-")[1]);

            //비율 좌표 가져오기!!! double형임에 유의 (아래 자료형들 전부 double로 바꿔야 함)
            double ratioX = sectionData.get(sectionNum).get(placeNum).getX();
            double ratioY = sectionData.get(sectionNum).get(placeNum).getY();

            Toast.makeText(getApplicationContext(), user.getId() + "가 선택됨 ", Toast.LENGTH_SHORT).show();
            ImageView icon = findViewById(R.id.pin);

            float[] matrix = new float[9];
            Matrix m = new Matrix();
            float dx, dy;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float middleX = (size.x) * 0.5f;
            float middleY = (size.y) * 0.3f;
            photoView.setScale(3.0f);
            photoView.getImageMatrix().getValues(matrix);

            Log.d("pre rect ", "left " + photoView.getDisplayRect().left + ", right " + photoView.getDisplayRect().right + " ,top: " + photoView.getDisplayRect().top);
            Log.d("pre matrix", "matrix[2]: " + matrix[2] + ", matrix[5]" + matrix[5]);
            dx = middleX - (matrix[2] + (photoView.getDisplayRect().right - photoView.getDisplayRect().left) * 0.75f);
            dy = middleY - (matrix[5] + (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) * 0.3f);

            Log.d("dx", "is " + dx);
            Log.d("dy", "is " + dy);

            matrix[2] = matrix[2] + dx;
            matrix[5] = matrix[5] + dy;
            Log.d("post matrix", "matrix[2]: " + matrix[2] + ", matrix[5]: " + matrix[5]);
            m.setValues(matrix);
            photoView.setImageMatrix(m);

            icon.setVisibility(View.VISIBLE);
            icon.setX(middleX);
            icon.setY(middleY);
            Log.d("m values", "m : " + m);
            photoView.setOnMatrixChangeListener(rect -> {
                Log.d("icon pos", "icon x: " + rect.left + ", icon y:" + icon.getY());
                icon.setX(rect.left + (photoView.getDisplayRect().right - photoView.getDisplayRect().left) * 0.75f);
                icon.setY(rect.top + (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) * 0.3f);
            });
        });
    }

    private void createSearchView() {
        EditText searchField = findViewById(R.id.searchField);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        ImageButton searchButton = findViewById(R.id.searchButton);

        radioGroup.check(R.id.rb_stname);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_stname) {
                Toast.makeText(MainActivity.this, "상호명으로 검색", Toast.LENGTH_SHORT).show();
                adapter.setSearchState("st_name");
            }
            else if (checkedId == R.id.rb_address) {
                Toast.makeText(MainActivity.this, "주소지로 검색", Toast.LENGTH_SHORT).show();
                adapter.setSearchState("address");
            }
        });

        searchButton.setOnClickListener(view -> {
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            //여기 수정 해야함 manger가 불러와졌을때만 적용할 수 있도록
            //manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        });

        searchField.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                return true;
            }

            return false;
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createMapView(){
        photoView = findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.naver_map2);

        photoView.setMaximumScale(3.0f);
    }
}