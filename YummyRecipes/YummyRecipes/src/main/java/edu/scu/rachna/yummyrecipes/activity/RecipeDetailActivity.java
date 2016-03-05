package edu.scu.rachna.yummyrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DialogHelper;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;

public class RecipeDetailActivity extends BaseActivity {

    private FloatingActionButton addNewCommentToRecipeButton;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY,
                Default.VERSION);
        id = getIntent().getStringExtra("recipeId");
        //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
        Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, "Getting Recipe", true) {
            @Override
            public void handleResponse(Recipe loadedrecipe) {
                TextView name = (TextView) findViewById(R.id.recipeName);
                name.setText(loadedrecipe.getRecipeName());
                TextView ingredients = (TextView) findViewById(R.id.recipeIngredients);
                ingredients.setText(loadedrecipe.getIngredients());
                TextView steps = (TextView) findViewById(R.id.recipeMethod);
                steps.setText(loadedrecipe.getDirections());
                ImageView pic = (ImageView) findViewById(R.id.recipeImage);
                Picasso.with(getApplicationContext()).load(loadedrecipe.getImage()).fit().into(pic);
                TextView likes = (TextView) findViewById(R.id.likesDisplay);
                likes.setText(String.valueOf(loadedrecipe.getLikes()));
                super.handleResponse(loadedrecipe);
            }
        });


        /**
         *  TODO : Based on RecipeId passed from DashBoardActivity
         *  Fetch all recipe details from backend less
         */

        ActionBar actionBar = getSupportActionBar();
        /**
         *  TODO : Recipe Name for which comment is being added should go here
         *  Data for recipe can be fetched from backendless based on recipeId passed in into intent
         */
        actionBar.setTitle("Recipe Name");

        addNewCommentToRecipeButton = (FloatingActionButton) findViewById(R.id.addNewCommentToRecipeButton);

        addNewCommentToRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // toast("Add a new comment clicked!!!");
                Intent addNewCommentToRecipeButtonIntent = new Intent(RecipeDetailActivity.this, AddCommentActivity.class);
                // TODO : Pass current selected Recipe object into intent
                startActivity(addNewCommentToRecipeButtonIntent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        /**
         *  TODO : Fetch recipe details from backend less
         *  This is needed because everytime this activity might be started or resumed there might be changed data
         *  (e.g. Total number of likes or newly added comments) that need to be updated on RecipeDetail page
         */
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         *  TODO : Fetch recipe details from backend less
         *  This is needed because everytime this activity might be started or resumed there might be changed data
         *  (e.g. Total number of likes or newly added comments) that need to be updated on RecipeDetail page
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int pid = item.getItemId();
        switch (pid) {
            case R.id.action_share:
                toast("Share action ...");
                break;
            case R.id.action_delete :
                toast("Delete recipe Action ...");
                break;
            case R.id.action_mode_close_button:
                this.finish();
                break;
            case R.id.action_like:

                Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, false) {
                    @Override
                    public void handleResponse(Recipe loadedrecipe) {
                        TextView likes = (TextView) findViewById(R.id.likesDisplay);
                        likes.setText(String.valueOf(loadedrecipe.getLikes()+1));
                        loadedrecipe.setLikes(loadedrecipe.getLikes()+1);
                        loadedrecipe.saveAsync(
                                new LoadingCallback<Recipe>(RecipeDetailActivity.this, false) {
                                    @Override
                                    public void handleResponse(Recipe recipe) {
                                        super.handleResponse(recipe);

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        progressDialog.dismiss();

                                        DialogHelper.createErrorDialog(RecipeDetailActivity.this,
                                                "BackendlessFault",
                                                fault.getMessage()).show();
                                    }
                                });
                        super.handleResponse(loadedrecipe);
                    }
                });



                break;
            default :
                this.finish();
                break;
        }
        return true;
    }

}
