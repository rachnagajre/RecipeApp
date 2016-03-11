package edu.scu.rachna.yummyrecipes.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vairavan on 3/10/16.
 */
public class Comment implements Serializable {
    private Date created;
    private String objectId;
    private Date updated;
    private String comment;

    public Comment() {
    }

    public Comment(Date created, String objectId, Date updated, String comment) {
        this.created = created;
        this.objectId = objectId;
        this.updated = updated;
        this.comment = comment;
    }

    public Comment(String comment) {
        this.comment = comment;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created) {this.created=created;}

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId){this.objectId=objectId;}

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date updated){this.updated=updated;}

    public void setComment(String comment) {this.comment=comment;}

    public String getComment() {return comment;}

}
