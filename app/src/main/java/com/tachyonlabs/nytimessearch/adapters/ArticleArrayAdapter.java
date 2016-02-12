package com.tachyonlabs.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.nytimessearch.R;
import com.tachyonlabs.nytimessearch.models.Article;

import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for the current position
        Article article = this.getItem(position);
        // check to see if existing view is being recycled
        // if not using a recycled view, inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        // find the ImageView
        ImageView imageview = (ImageView) convertView.findViewById(R.id.ivImage);
        // clear out recycled image from convertView from last time
        imageview.setImageResource(0);
        // and find the TextView
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());
        // populate the thumbnail image
        // remotely download
        String thumbnail = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).fit().into(imageview);
        } else {
            Picasso.with(getContext()).load(R.drawable.placeholder).fit().into(imageview);
        }
        return convertView;
    }
}
