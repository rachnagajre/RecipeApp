package edu.scu.rachna.yummyrecipes.data;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.Serializable;


public class Recipe implements Serializable
{
    private java.util.Date created;
    private String objectId;
    private java.util.Date updated;
    private String ownerId;
    private BackendlessUser creator;
    private String recipeName;
    private String ingredients;
    private String directions;
    private String image;
    private int likes;

    public Recipe() {}

    public String getRecipeName()
    {
        return recipeName;
    }

    public void setRecipeName( String recipeName )
    {
        this.recipeName = recipeName;
    }

    public void setIngredients( String ingredients) {this.ingredients=ingredients;}

    public String getIngredients(){return ingredients;}

    public void setDirections(String directions){this.directions=directions;}

    public String getDirections(){return directions;}

    public java.util.Date getCreated()
    {
        return created;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public java.util.Date getUpdated()
    {
        return updated;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public BackendlessUser getCreator()
    {
        return creator;
    }

    public void setCreator( BackendlessUser creator )
    {
        this.creator = creator;
    }

    public void setImage(String image) {this.image=image;}

    public String getImage() {return image;}

    public void setLikes(int likes){this.likes=likes;}

    public int getLikes(){return likes;}

    public Recipe save()
    {
        return Backendless.Data.of( Recipe.class ).save( this );
    }

    public void saveAsync( AsyncCallback<Recipe> callback )
    {
        Backendless.Data.of( Recipe.class ).save( this, callback );
    }

    public Long remove()
    {
        return Backendless.Data.of( Recipe.class ).remove( this );
    }


    public void removeAsync( AsyncCallback<Long> callback )
    {
        Backendless.Data.of( Recipe.class ).remove( this, callback );
    }

    public static Recipe findById( String id )
    {
        return Backendless.Data.of( Recipe.class ).findById( id );
    }
    

    public static void findByIdAsync( String id, AsyncCallback<Recipe> callback )
    {
        Backendless.Data.of( Recipe.class ).findById( id, callback );
    }

    public static Recipe findFirst()
    {
        return Backendless.Data.of( Recipe.class ).findFirst();
    }
    

    public static void findFirstAsync( AsyncCallback<Recipe> callback )
    {
        Backendless.Data.of( Recipe.class ).findFirst( callback );
    }

    public static Recipe findLast()
    {
        return Backendless.Data.of( Recipe.class ).findLast();
    }



    public static void findLastAsync( AsyncCallback<Recipe> callback )
    {
        Backendless.Data.of( Recipe.class ).findLast( callback );
    }

    public static BackendlessCollection<Recipe> find( BackendlessDataQuery query )
    {
        return Backendless.Data.of( Recipe.class ).find( query );
    }


    public static void getAllRecipes(AsyncCallback<BackendlessCollection<Recipe>> callback)
    {
        Backendless.Data.of( Recipe.class).find(callback);
    }
    public static void findAsync(BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Recipe>> callback)
    {
        Backendless.Data.of( Recipe.class ).find( query, callback );
    }

}
