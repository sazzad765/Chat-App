package com.team15.chatapp.Adapter;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team15.chatapp.Model.Chat;
import com.team15.chatapp.R;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Chat> mChat) {
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);

        if (chat.getType().equals("image")) {
            holder.chatImageView.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(chat.getMessage()).into(holder.chatImageView);
        } else {
            holder.show_message.setText(chat.getMessage());
        }

        if (position == mChat.size() - 1) {
            if (chat.isSeen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Sent");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
        holder.chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopupDialog(chat.getMessage());
            }
        });
        holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText( holder.show_message.getText());
                Toast.makeText(mContext, "Text copied", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView chatImageView;
        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            chatImageView = itemView.findViewById(R.id.chatImageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private void ShowPopupDialog(String imageurl) {
        final Dialog dialogView = new Dialog(mContext);
        dialogView.setContentView(R.layout.image_view_layout);

        ImageView imageView = dialogView.findViewById(R.id.imageView);
        ImageButton imgClose = dialogView.findViewById(R.id.imgClose);
        Glide.with(mContext).load(imageurl).into(imageView);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView.dismiss();
            }
        });
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.show();
    }
}