package edu.scu.rachna.yummyrecipes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Comment;

/**
 * Created by Rachna on 3/10/2016.
 */
public class CustomCommentsAdapter extends BaseAdapter {

    private final List<Comment> commentsList;

    private Context context;

    public CustomCommentsAdapter(Context context, int resource, List<Comment> comments) {
        super();
        this.commentsList = comments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateCommentsList(List<Comment> updatedList) {
        this.commentsList.clear();
        this.commentsList.addAll(updatedList);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ScrapViewHolder holder;

        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.comment_list_item, null);

            holder = new ScrapViewHolder();
            holder.commentContent = (TextView) row.findViewById(R.id.commentContent);

            row.setTag(holder);

        } else {
            holder = (ScrapViewHolder) row.getTag();
        }

        //Set Comment
        holder.commentContent.setText(commentsList.get(position).getComment());

        return row;
    }

    /**
     *  Class to hold row data from each row in list
     */
    public class ScrapViewHolder {
        TextView commentContent;
    }
}
