package com.frommetoyou.texting.chatModule.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.pojo.Message;
import com.frommetoyou.texting.databinding.ItemChatBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> mMessages;
    private OnItemClickListener mListener;
    private int lastPhoto = 0;

    public ChatAdapter(List<Message> mMessages, OnItemClickListener mListener) {
        this.mMessages = mMessages;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        final int maxMarginHorizontal = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_horizontal);
        final int maxMarginTop = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_top);
        final int minMargin = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_min);

        int gravity = Gravity.END;
        Drawable background = ContextCompat.getDrawable(mContext, R.drawable.background_chat_me);
        int marginStart = maxMarginHorizontal;
        int marginTop = minMargin;
        int marginEnd = minMargin;

        if (!message.isSentByMe()) {
            gravity = Gravity.START;
            background = ContextCompat.getDrawable(mContext, R.drawable.background_chat_friend);
            marginEnd = maxMarginHorizontal;
            marginStart = minMargin;
        }
        if (position > 0 && message.isSentByMe() != mMessages.get(position - 1).isSentByMe()) {
            marginTop = maxMarginTop;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.binding.tvMessage.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(marginStart, marginTop, marginEnd, minMargin);

        if (message.getPhotoUrl() != null) {
            holder.binding.tvMessage.setVisibility(View.GONE);
            holder.binding.ivPhoto.setVisibility(View.VISIBLE);
            if (position > lastPhoto) {
                lastPhoto = position;
            }
            final int size = mContext.getResources().getDimensionPixelSize(R.dimen.chat_size_image);
            params.width = size;
            params.height = size;
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_timer_sand_160)
                    .error(R.drawable.ic_emoticon_sad)
                    .centerCrop();
            Glide.with(mContext)
                    .asBitmap()
                    .load(message.getPhotoUrl())
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            int dimension = size - mContext.getResources().getDimensionPixelSize(R.dimen.chat_padding_image);
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(resource, dimension, dimension);
                            holder.binding.ivPhoto.setImageBitmap(bitmap);
                            if (!message.isLoaded()) {
                                message.setLoaded(true);
                                if (position == lastPhoto) {
                                    mListener.onImageLoaded();
                                }
                            }
                            return true;
                        }
                    })
                    .into(holder.binding.ivPhoto);
            holder.binding.ivPhoto.setBackground(background);
            holder.setClickListener(message, mListener);
        } else {
            holder.binding.tvMessage.setVisibility(View.VISIBLE);
            holder.binding.ivPhoto.setVisibility(View.GONE);
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }

        holder.binding.tvMessage.setBackground(background);
        holder.binding.tvMessage.setText(message.getMessage());
        holder.binding.ivPhoto.setLayoutParams(params);
        holder.binding.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void add(Message message) {
        if (!mMessages.contains(message)) {
            mMessages.add(message);
            notifyItemInserted(mMessages.size() - 1);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemChatBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatBinding.bind(itemView);
        }

        private void setClickListener(Message message, OnItemClickListener listener) {
            binding.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickedImage(message);
                }
            });
        }
    }
}
