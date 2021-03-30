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
import com.frommetoyou.texting.databinding.ItemRequestBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;
    private OnItemUserClickListener mListener;

    public RequestAdapter(List<User> mUsers, OnItemUserClickListener mListener) {
        this.mUsers = mUsers;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.setOnclickListener(user, mListener);
        holder.binding.tvName.setText(user.getUsername());
        holder.binding.tvEmail.setText(user.getEmail());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.binding.ivPhoto);
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

    public void remove(User user){
        if (mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ItemRequestBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRequestBinding.bind(itemView);
        }
        void setOnclickListener(User user, OnItemUserClickListener listener) {
            binding.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptRequest(user);
                }
            });
            binding.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDenyRequest(user);
                }
            });
        }
    }
}
