package com.example.fireoperationmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoView photoView;
    EditText searchField;
    private Map<Integer, Map<Integer, Place>> sectionData = new HashMap<>();
    private List<Arcade> arcadeList = new ArrayList<>();
    private final float slidingPanelAnchorPoint = 0.4f;
    private final PointF curRatio = new PointF(0.0f, 0.0f);
    private long backBtnTime = 0;
    private enum ButtonState {LARGE, SMALL}
    private ButtonState buttonState = ButtonState.SMALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        searchField = findViewById(R.id.searchField);
        slidingUpPanelLayout = findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setAnchorPoint(slidingPanelAnchorPoint);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED)
                    searchField.clearFocus();
            }
        });

        initializeAdapterAndRecyclerView();
        createSearchView();
    }

    @Override
    public void onBackPressed() { //뒤로가기 두 번 눌를 시 종료
        long curTime = System.currentTimeMillis();
        long gaptime = curTime - backBtnTime;

        if (0 <= gaptime && 2000 >= gaptime) {
            moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }
        //super.onBackPressed();
    }

    private void initializeAdapterAndRecyclerView() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference sectionRef = FirebaseDatabase.getInstance().getReference().child("Section");
        DatabaseReference arcadeRef = FirebaseDatabase.getInstance().getReference().child("Arcade");
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sectionData = new HashMap<>();
                for (DataSnapshot sectionData : snapshot.getChildren()) {
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //아케이트 정보를 모두 불러오면 mapview 재귀 호출
        arcadeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arcadeList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Arcade arcade = data.getValue(Arcade.class);
                    arcadeList.add(arcade);
                }
                Collections.sort(arcadeList);
                createMapView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //아이템 클릭시 이벤트 설정
        adapter.setOnSimpleItemClickListener((view, position) -> {
            User user = adapter.getItem(position);
            int sectionNum = Integer.parseInt(user.getId().split("-")[0]);
            int placeNum = Integer.parseInt(user.getId().split("-")[1]);

            //테스트용 토스트 메세지 출력
            Toast.makeText(getApplicationContext(), user.getId() + "가 선택됨 ", Toast.LENGTH_SHORT).show();

            //아이템 클릭시 검색창 비활성화
            searchField.clearFocus();

            //비율 좌표 가져오기
            curRatio.set(sectionData.get(sectionNum).get(placeNum).getX(), sectionData.get(sectionNum).get(placeNum).getY());

            ImageView icon = findViewById(R.id.pin);
            icon.setVisibility(View.VISIBLE);

            photoView.setScale(photoView.getMaximumScale(), 0.0f, 0.0f, false);
            Matrix suppMatrix = new Matrix();
            float[] values = new float[9];
            photoView.getSuppMatrix(suppMatrix);
            suppMatrix.getValues(values);

            PointF middleRatio = new PointF(0.5f, (1.0f - slidingPanelAnchorPoint) / 2.0f);
            PointF middleP = new PointF(getScreenWidth(this) * middleRatio.x, getScreenHeight(this) * middleRatio.y);

            values[2] += middleP.x - (photoView.getDisplayRect().left + (photoView.getDisplayRect().right - photoView.getDisplayRect().left) * curRatio.x);
            values[5] += middleP.y - (photoView.getDisplayRect().top + (photoView.getDisplayRect().bottom - photoView.getDisplayRect().top) * curRatio.y);

            suppMatrix.setValues(values);
            photoView.setSuppMatrix(suppMatrix);
        });
    }

    public static int getScreenHeight(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    private void createSearchView() {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        ImageButton searchButton = findViewById(R.id.searchButton);

        radioGroup.check(R.id.rb_stname);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_stname) {
                Toast.makeText(MainActivity.this, "상호명으로 검색", Toast.LENGTH_SHORT).show();
                searchField.setHint("상호명을 입력하세요.");
                adapter.setSearchState("st_name");
                adapter.getFilter().filter(searchField.getText());
            } else if (checkedId == R.id.rb_address) {
                Toast.makeText(MainActivity.this, "주소지로 검색", Toast.LENGTH_SHORT).show();
                searchField.setHint("주소지를 입력하세요.");
                adapter.setSearchState("address");
                adapter.getFilter().filter(searchField.getText());
            } else if (checkedId == R.id.rb_id) {
                Toast.makeText(MainActivity.this, "건물번호로 검색", Toast.LENGTH_SHORT).show();
                searchField.setHint("건물번호를 입력하세요. (예시: 1-3-2)");
                adapter.setSearchState("id");
                adapter.getFilter().filter(searchField.getText());
            }
        });

        searchButton.setOnClickListener(view -> {
            if (searchField.hasFocus()) {
                searchField.clearFocus();
            }
        });

        //입력중일때 추천검색어 고민중
        searchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ImageView icon = findViewById(R.id.pin);
                icon.setVisibility(View.INVISIBLE);
                adapter.clearRecyclerView();
                searchField.setText("");
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            } else {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });

        searchField.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createMapView() {
        photoView = findViewById(R.id.photo_view);
        //줌 비율 설정
        photoView.setMaximumScale(7.0f);
        photoView.setImageResource(R.drawable.operation_map);
        Toast.makeText(getApplicationContext(), "로딩 완료", Toast.LENGTH_SHORT).show();
        photoView.setScale(2.7f, 910.0f, 0.0f, false);


        //임시 개수
        ImageButton[] button = new ImageButton[arcadeList.size()];
        FrameLayout mapView = findViewById(R.id.mapView);
        final int[] arcadeImgList = new int[]{R.drawable.arcadepin1, R.drawable.arcadepin2, R.drawable.arcadepin3, R.drawable.arcadepin4,
                R.drawable.arcadepin5, R.drawable.arcadepin6, R.drawable.arcadepin7, R.drawable.arcadepin8};

        final int buttonSizeSmall = dpToPx(this, 15f);
        final int buttonSizeLarge = dpToPx(this, 40f);
        int initSize = buttonSizeLarge;


        //버튼 위치 임시 설정
        PointF[] btnPos = new PointF[arcadeList.size()];
        for (int i = 0; i < arcadeList.size(); i++) {
            btnPos[i] = new PointF();
            btnPos[i].x = arcadeList.get(i).getX();
            btnPos[i].y = arcadeList.get(i).getY();
        }

        for (int i = 0; i < arcadeList.size(); i++) {
            button[i] = new ImageButton(this);
            button[i].setBackgroundResource(arcadeImgList[i]);
            RelativeLayout.LayoutParams pm = new RelativeLayout.LayoutParams(initSize, initSize);
            pm.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            button[i].setLayoutParams(pm);
            RectF rect = photoView.getDisplayRect();
            button[i].setX((rect.left + ((rect.right - rect.left) * btnPos[i].x) - initSize / 2.0f));
            button[i].setY((rect.top + ((rect.bottom - rect.top) * btnPos[i].y) - initSize / 2.0f));
            mapView.addView(button[i]);
        }

        for (int i = 0; i < arcadeList.size(); i++) {
            int finalI = i;
            button[i].setOnClickListener(v -> {
                //검색 느림 추후 개선
                String arcadeId = arcadeList.get(finalI).getId().trim();
                String address = adapter.getItem(arcadeId).getAddress();

                Intent intent = new Intent(getApplicationContext(), ArcadePop.class);
                intent.putExtra("Enter_num", Integer.toString(arcadeList.get(finalI).getNum()));
                intent.putExtra("Address", address);
                intent.putExtra("Detail_info", arcadeList.get(finalI).getDetail());
                startActivityForResult(intent, 1);
            });
        }

        photoView.setOnMatrixChangeListener(rect -> {
            ImageView icon = findViewById(R.id.pin);
            float pinWidth = icon.getWidth();
            float pinHeight = icon.getHeight();
            float offset = 0.1f;

            icon.setX((rect.left + ((rect.right - rect.left) * curRatio.x) - pinWidth / 2));
            icon.setY((rect.top + ((rect.bottom - rect.top) * curRatio.y) - pinHeight));

            if (buttonState == ButtonState.SMALL && photoView.getScale() > photoView.getMediumScale() + offset) {

                FrameLayout.LayoutParams pm = new FrameLayout.LayoutParams(buttonSizeLarge, buttonSizeLarge);
                buttonState = ButtonState.LARGE;
                for (int i = 0; i < arcadeList.size(); i++) {
                    button[i].setLayoutParams(pm);
                }
            }
            else if (buttonState == ButtonState.LARGE && photoView.getScale() < photoView.getMediumScale() - offset) {
                FrameLayout.LayoutParams pm = new FrameLayout.LayoutParams(buttonSizeSmall, buttonSizeSmall);
                buttonState = ButtonState.SMALL;
                for (int i = 0; i < arcadeList.size(); i++){
                    button[i].setLayoutParams(pm);
                }
            }

            for (int i = 0; i < arcadeList.size(); i++) {
                button[i].setX((rect.left + ((rect.right - rect.left) * btnPos[i].x) - button[i].getLayoutParams().width / 2.0f));
                button[i].setY((rect.top + ((rect.bottom - rect.top) * btnPos[i].y) - button[i].getLayoutParams().height / 2.0f));
            }
        });
    }

    public int dpToPx(Context context, float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }
}