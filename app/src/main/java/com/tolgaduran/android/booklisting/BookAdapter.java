package com.tolgaduran.android.booklisting;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Created by Java_Engineer on 16.11.2016.
 */

public class BookAdapter extends ArrayAdapter<BookObject> {
    public BookAdapter(Context context, List<BookObject> objects) {
        super(context, 0, objects);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) listItemView.findViewById(R.id.title_text);
            viewHolder.author = (TextView) listItemView.findViewById(R.id.author_text);
            viewHolder.description = (TextView) listItemView.findViewById(R.id.description_text);
            viewHolder.thumbNail = (ImageView) listItemView.findViewById(R.id.thumbnail);
            viewHolder.progressBar = (ProgressBar) listItemView.findViewById(R.id.progress);

            listItemView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) listItemView.getTag();
        }

        BookObject currentBookObject = getItem(position);

        if (currentBookObject != null) {
            viewHolder.title.setText(currentBookObject.getTitle());

            String author = getContext().getString(R.string.by) + " " + currentBookObject.getAuthor();
            viewHolder.author.setText(author);

            String description;
            if (currentBookObject.getDescription() == null || currentBookObject.getDescription().equals("")) {
                viewHolder.description.setTextSize(8);
                description = "\n" + getContext().getResources().getString(R.string.no_description);
            } else {
                viewHolder.description.setTextSize(12);
                description = currentBookObject.getDescription();
            }
            viewHolder.description.setText(description);

            if (!currentBookObject.getThumbNailUrl().equals("")) {

                viewHolder.progressBar.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(currentBookObject.getThumbNailUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewHolder.thumbNail);
            } else {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.thumbNail.setImageResource(R.drawable.stub);
            }
        }
        return listItemView;
    }

    static class ViewHolder {
        TextView title;
        TextView author;
        TextView description;
        ImageView thumbNail;
        ProgressBar progressBar;
    }
}
