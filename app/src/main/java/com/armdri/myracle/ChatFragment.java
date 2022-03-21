package com.armdri.myracle;

import static com.armdri.myracle.Constants.LEFT_POSITION;
import static com.armdri.myracle.Constants.RIGHT_POSITION;
import static com.armdri.myracle.Nickname.loadNickname;
import static com.armdri.myracle.Nickname.makeNickname;
import static com.armdri.myracle.Nickname.saveNickname;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    Context context;
    Fragment fragment;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Chat> chatList;

    private String nickname = "test";

    private EditText chatText;
    private Button sendButton;

    private DatabaseReference myRef;
    private Handler mainThreadHandler;

    private static NicknameDatabase nicknameDatabase = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);
        context = container.getContext();


        openNicknameDatabase();
        // 닉네임은 처음 한번만 생성하여 DB에 저장. 이후로는 저장된걸 가져다 씀
        if ((nickname = loadNickname(context)) == null) {
            nickname = makeNickname();
            saveNickname(nickname, context);
        }

        initUI(rootView);

        boolean lockChat = DateUtil.lockChat(DateUtil.getCurrentTime(), "05");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockChat){
                    mOnPopupClick(v);
                } else {
                    //입력창에 메시지를 입력 후 버튼클릭했을 때
                    String msg = chatText.getText().toString();
                    if (msg == null || msg.equals("")) return;

                    Chat chat = new Chat();
                    chat.setName(nickname);
                    chat.setMsg(msg);

                    //메시지를 파이어베이스에 보냄.
                    myRef.push().setValue(chat);
                    chatText.setText("");

                    // 딜레이 있는 스크롤
                    mainThreadHandler.postDelayed(scroll, 1);
                    ((MainActivity)getActivity()).updateTodayCalendar();

                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        updateRecyclerView();

        //데이터들을 추가, 변경, 제거, 이동, 취소
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //어댑터에 DTO추가
                Chat chat = snapshot.getValue(Chat.class);

                // 여기서 왼쪽 오른쪽 뷰를구분해서 어댑터에 넣어야함
                if(chat.getName().equals(nickname)) chat.setViewType(RIGHT_POSITION);
                else chat.setViewType(LEFT_POSITION);

                ((ChatAdapter)adapter).addChat(chat);
                //Log.d(TAG, "chat 이름 : " + chat.getName());
                //Log.d(TAG, "view type : " + chat.getViewType());

                // 딜레이 없는 스크롤
                recyclerView.scrollToPosition(Math.max(adapter.getItemCount()-1, 0));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: "+isVisible);
                recyclerView.scrollToPosition(Math.max(adapter.getItemCount()-1, 0));

                if(isVisible) ((MainActivity)getActivity()).hideBottomNav();
                else          ((MainActivity)getActivity()).showBottomNav();
            }
        });

        return rootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) {
            //adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(Math.max(adapter.getItemCount() - 1, 0));
        }
    }


    private void initUI(ViewGroup rootView){
        fragment = this;

        //리사이클러뷰 연결
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        chatText = rootView.findViewById(R.id.chatText);
        sendButton = rootView.findViewById(R.id.sendButton);

        // chatList 배열 생성
        chatList = new ArrayList<>();

        //리사이클러뷰에 어댑터 적용
        adapter = new ChatAdapter(chatList, nickname);
        recyclerView.setAdapter(adapter);


        //쓰레드 핸들러 초기화
        mainThreadHandler = new Handler();
    }

    public void mOnPopupClick(View v){
        // 데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(context, PopupActivity.class);
        intent.putExtra("data", "채팅 가능한 시간이 아닙니다.\n(05:00 ~ 06:00)");
        startActivityForResult(intent, 1);
    }

    public void openNicknameDatabase() {
        if (nicknameDatabase != null) {
            nicknameDatabase.close();
            nicknameDatabase = null;
        }

        nicknameDatabase = NicknameDatabase.getInstance(context);
        boolean isOpen = nicknameDatabase.open();
        if (isOpen) {
            Log.d(TAG, "nicknameDatabase is open.");
        } else {
            Log.d(TAG, "nicknameDatabase is not open");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (nicknameDatabase != null) {
            nicknameDatabase.close();
            nicknameDatabase = null;
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    public boolean updateRecyclerView(){
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        return mainThreadHandler.postDelayed(scroll, 1);
    }

    private Runnable scroll = new Runnable() {
        @Override
        public void run() {
            recyclerView.scrollToPosition(Math.max(adapter.getItemCount()-1, 0));
            mainThreadHandler.sendEmptyMessage(0);
            //Log.d(TAG, "핸들러 run 파트 실행됨");
            //Log.d(TAG, "getItemCount: " + Integer.toString(adapter.getItemCount()));
        }
    };
}
