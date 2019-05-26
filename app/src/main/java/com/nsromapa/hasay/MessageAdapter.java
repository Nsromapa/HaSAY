package com.nsromapa.hasay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    List<MessagesObjects> messages;

    public MessageAdapter(Context context, List<MessagesObjects> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_items,null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final MessagesObjects thisMessage = messages.get(position);

        viewHolder.UserMessageFullLinear.setVisibility(View.GONE);
        viewHolder.HaSAYMessageFullLinear.setVisibility(View.GONE);

        if (thisMessage.getFrom().equals("HaSAY")){
            viewHolder.HaSAYMessageFullLinear.setVisibility(View.VISIBLE);
            viewHolder.HaSAYMessage.setText(thisMessage.getMessage());
            viewHolder.HaSAYMessageTime.setText(thisMessage.getTime());

        }else{
            viewHolder.UserMessageFullLinear.setVisibility(View.VISIBLE);
            viewHolder.UserMessage.setText(thisMessage.getMessage());
            viewHolder.userMessageTime.setText(thisMessage.getTime());

        }

    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }








    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout UserMessageFullLinear, HaSAYMessageFullLinear;
        TextView UserMessage, HaSAYMessage;
        TextView userMessageTime, HaSAYMessageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserMessageFullLinear = itemView.findViewById(R.id.UserMessageFullLinear);
            HaSAYMessageFullLinear = itemView.findViewById(R.id.HaSAYMessageFullLinear);
            UserMessage = itemView.findViewById(R.id.UserMessage);
            HaSAYMessage = itemView.findViewById(R.id.HaSAYMessage);
            HaSAYMessageTime = itemView.findViewById(R.id.HaSAYMessageTime);
            userMessageTime = itemView.findViewById(R.id.userMessageTime);
        }
    }
}
