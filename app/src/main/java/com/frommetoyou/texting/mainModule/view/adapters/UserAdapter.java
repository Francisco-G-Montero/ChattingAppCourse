package com.frommetoyou.texting.mainModule.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.databinding.ItemUserBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> mUsers;
    private Context mContext;

    public UserAdapter(List<User> mUsers, OnItemUserClickListener mListener) {
        this.mUsers = mUsers;
        this.mListener = mListener;
    }

    private OnItemUserClickListener mListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.setClickListener(user, mListener);
        holder.binding.content.tvName.setText(user.getUsernameValid());
        int messageUnreaded = user.getMessagesUnreaded();
        if (messageUnreaded > 0) {
            String countStr = messageUnreaded > 99 ? mContext.getString(R.string.main_item_max_unreaded) : String.valueOf(messageUnreaded);
            holder.binding.content.tvCountUnread.setText(countStr);
            holder.binding.content.tvCountUnread.setVisibility(View.VISIBLE);
        } else
            holder.binding.content.tvCountUnread.setVisibility(View.GONE);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.binding.content.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void add(User user) {
        if (!mUsers.contains(user)) {
            mUsers.add(user);
            notifyItemInserted(mUsers.size() - 1);
        } else
            updateUser(user);
    }

    public void updateUser(User user) {
        if (mUsers.contains(user)) {
            int index = mUsers.indexOf(user);
            mUsers.set(index, user);
            notifyItemChanged(index);
        }
    }

    public void remove(User user) {
        if (mUsers.contains(user)) {
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUserBinding.bind(itemView);
        }

        private void setClickListener(User user, OnItemUserClickListener listener) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(user);
                    return true;
                }
            });

        }
    }
}
