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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoView photoView;
    EditText searchField;
    private Map<Integer, Map<Integer, Place>> sectionData = new HashMap<>();
    private double pin_width, pin_height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        slidingUpPanelLayout = findViewById(R.id.slidingLayout);
        searchField = findViewById(R.id.searchField);

        createMapView();
        initializeAdapterAndRecyclerView();
        createSearchView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView icon = (ImageView) findViewById(R.id.pin);
        pin_width = icon.getWidth();
        pin_height = icon.getHeight();
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
        adapter.setOnSimpleItemClickListener((view, position) -> {
            User user = adapter.getItem(position);
            int sectionNum = Integer.parseInt(user.getId().split("-")[0]);
            int placeNum = Integer.parseInt(user.getId().split("-")[1]);

            //아이템 클릭시 검색창 비활성화
            searchField.clearFocus();

            //비율 좌표 가져오기!!! double형임에 유의 (아래 자료형들 전부 double로 바꿔야 함)
            double ratioX = sectionData.get(sectionNum).get(placeNum).getX();
            double ratioY = sectionData.get(sectionNum).get(placeNum).getY();

            Toast.makeText(getApplicationContext(), user.getId() + "가 선택됨 ", Toast.LENGTH_SHORT).show();
            ImageView icon = findViewById(R.id.pin);

            float[] matrix = new float[9];
            Matrix m = new Matrix();
            double dx, dy;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            double middleX = (size.x) * 0.5;
            double middleY = (size.y) * 0.3;
            photoView.setScale(3.0f);
            photoView.getImageMatrix().getValues(matrix);

            Log.d("ratio values" , "retio x: " + ratioX + ", ratio y: " + ratioY);
            Log.d("get width and height", "width: " + pin_width + ", height " + pin_height);
            dx = middleX - (matrix[2] + (photoView.getDisplayRect().right - photoView.getDisplayRect().left) * ratioX);
            dy = middleY - (matrix[5] + (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) * ratioY);

            Log.d("dx", "is " + dx);
            Log.d("dy", "is " + dy);

            matrix[2] = matrix[2] + (float)dx;
            matrix[5] = matrix[5] + (float)dy;
            Log.d("post matrix", "matrix[2]: " + matrix[2] + ", matrix[5]: " + matrix[5]);
            m.setValues(matrix);
            photoView.setImageMatrix(m);

            icon.setVisibility(View.VISIBLE);
            icon.setX((float)(middleX - (pin_width/2)));
            icon.setY((float)(middleY - (pin_height)));
            Log.d("m values", "m : " + m);
            Log.d("pin position", "pin x: " + (float)(middleX - (pin_width/2)) + ", pin y: " + (float)(middleY - (pin_height)));
            Log.d("middle values" , "middle x: " + middleX + ", middle y" + middleY);
            photoView.setOnMatrixChangeListener(rect -> {
                Log.d("icon pos", "icon x: " + icon.getX() + ", icon y:" + icon.getY());
                icon.setX((float)(rect.left + ((photoView.getDisplayRect().right - photoView.getDisplayRect().left) * ratioX) - pin_width/2));
                icon.setY((float)(rect.top + ((photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) * ratioY) - pin_height));
            });
        });
    }

    private void createSearchView() {
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
            if (searchField.hasFocus()) {
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                searchField.clearFocus();
            }
        });

        //입력중일때 추천검색어 고민중
        searchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchField.setText("");
                adapter.clearRecyclerView();
                //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        searchField.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                searchField.clearFocus();
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
                /* 검색창이 비어있는 상태에서 모든 결과를 띄울것인가 말것인가
                if (s.toString().trim().equals("")) {
                    adapter.clearRecyclerView();
                }
                else adapter.getFilter().filter(s);
                */
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createMapView(){
        photoView = findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.operation_map);

        //줌 비율 설정!! 지도 이미지를 참고해서 적당한 확대/축소 비율 찾기
        photoView.setMaximumScale(5.0f);
    }
}