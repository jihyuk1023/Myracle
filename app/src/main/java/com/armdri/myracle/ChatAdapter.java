package com.armdri.myracle;

import static com.armdri.myracle.Constants.LEFT_POSITION;
import static com.armdri.myracle.Constants.RIGHT_POSITION;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armdri.myracle.databinding.ItemLeftBinding;
import com.armdri.myracle.databinding.ItemRightBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ChatAdapter";

    private ArrayList<Chat> chatList;
    private String name;


    public ChatAdapter(ArrayList<Chat> chatData, String name){
        //ChatFragment.java에서 받은 데이터들을 저장
        this.chatList = chatData;
        this.name = name;
    }

    public static class LeftViewHolder extends RecyclerView.ViewHolder {
        ItemLeftBinding itemLeftBinding;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLeftBinding = ItemLeftBinding.bind(itemView);
            itemView.setEnabled(true);
            itemView.setClickable(true);
        }
        void bindItemLeft(Chat chat){
            itemLeftBinding.msgLinear.setGravity(Gravity.LEFT);
            itemLeftBinding.nameText.setText(chat.getName());
            itemLeftBinding.msgText.setText(chat.getMsg());
        }
    }

    public static class RightViewHolder extends RecyclerView.ViewHolder {
        ItemRightBinding itemRightBinding;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRightBinding = ItemRightBinding.bind(itemView);
            itemView.setEnabled(true);
            itemView.setClickable(true);
        }
        void bindItemRight(Chat chat){
            itemRightBinding.msgLinear.setGravity(Gravity.RIGHT);
            itemRightBinding.nameText.setText(chat.getName());
            itemRightBinding.msgText.setText(chat.getMsg());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == LEFT_POSITION) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_left, parent, false);
            LeftViewHolder leftViewHolder = new LeftViewHolder(linearLayout);
            return leftViewHolder;
        }
        else if(viewType == RIGHT_POSITION) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_right, parent, false);
            RightViewHolder rightViewHolder = new RightViewHolder(linearLayout);
            return rightViewHolder;
        }
        else throw new RuntimeException("viewType Error on create view holder");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        if(holder instanceof LeftViewHolder) {
            ((LeftViewHolder) holder).bindItemLeft(chat);
            //Log.d(TAG, "왼쪽 정렬 바인딩됨");
        }
        else if(holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).bindItemRight(chat);
            //Log.d(TAG, "오른쪽 정렬 바인딩됨");
        }
    }

    //메시지아이템 갯수세기
    @Override
    public int getItemCount() {
        return chatList == null ? 0: chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }

    //chatList 전부 반환
    public ArrayList<Chat> getItems(){
        return chatList;
    }
    
    //메시지아이템의 추가 및 적용
    public void addChat(Chat chat){
        chatList.add(chat);
        notifyItemInserted(chatList.size()-1);
    }
}
