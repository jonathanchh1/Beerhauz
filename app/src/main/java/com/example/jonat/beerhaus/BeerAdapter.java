package com.example.jonat.beerhaus;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jonat on 6/25/2017.
 */

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.CustomViewHolder> {
    private ArrayList<Beeritems> feedItemList = new ArrayList<>();
    private Context mContext;
    private final Beeritems beeritems = new Beeritems();
    private Callbacks mCallbacks;
    private int rowLayout;
    private String errorMsg;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;



    public BeerAdapter(Context context, int rowLayout, ArrayList<Beeritems> feedItemList, Callbacks callbacks) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        this.mCallbacks = callbacks;
    }


    /*--------------------------------------------------pagination----------------------------------------------*/

    public void addpages(Beeritems r) {
        feedItemList.add(r);
        notifyItemInserted(feedItemList.size() - 1);
    }

    public void addAll(ArrayList<Beeritems> Results) {
        for (Beeritems result : Results) {
            addpages(result);
        }
    }

    public void removes(Beeritems r) {
        int position = feedItemList.indexOf(r);
        if (position > -1) {
            feedItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            removes(getItem(0));
        }
    }

    public Beeritems getItem(int position) {
        return feedItemList.get(position);
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        addpages(new Beeritems());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = feedItemList.size() - 1;
        Beeritems result = getItem(position);

        if (result != null) {
            feedItemList.remove(position);
            notifyItemRemoved(position);
        }
    }


    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(feedItemList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    /*----------------------------------------------------------end of pagination---------------------------------------------------*/


    public void setData(ArrayList<Beeritems> data) {
        remove();
        for (Beeritems items : data) {
            add(items);
        }
    }

    public void remove() {
        synchronized (beeritems) {
            feedItemList.clear();
        }
        notifyDataSetChanged();
    }

    public void add(Beeritems items) {
        synchronized (beeritems) {
            feedItemList.add(items);
        }

        notifyDataSetChanged();
    }


    public interface Callbacks{
        void onTaskCompleted(Beeritems items, int position);
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Beeritems feedItem = feedItemList.get(i);
        customViewHolder.items = feedItem;

        //Render image using Picasso library
        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            Picasso.with(mContext).load(feedItem.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }

        customViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onTaskCompleted(feedItem, customViewHolder.getAdapterPosition());
            }
        });

        //Setting text view title
        if (feedItem.getTitle() != null) {
            customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
        }
    }



    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        public Beeritems items;
        View mView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
            mView = view;
        }
    }

}
