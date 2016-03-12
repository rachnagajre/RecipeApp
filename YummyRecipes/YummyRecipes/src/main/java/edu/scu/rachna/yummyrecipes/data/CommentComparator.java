package edu.scu.rachna.yummyrecipes.data;

import java.util.Comparator;


public class CommentComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment lhs, Comment rhs) {
        if (lhs.getCreated().before(rhs.getCreated())) {
            return -1;
        } else if (lhs.getCreated().after(rhs.getCreated())) {
            return 1;
        } else {
            return 0;
        }

    }
}
