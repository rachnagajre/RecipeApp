package edu.scu.rachna.yummyrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Comment;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DialogHelper;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;

public class AddCommentActivity extends BaseActivity {


    private EditText enterComment;
    private Button submitCommentButton;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY,
                Default.VERSION);
        id = getIntent().getStringExtra("recipeId");

        enterComment = (EditText) findViewById(R.id.enterComment);
        submitCommentButton = (Button) findViewById(R.id.submitCommentButton);

        Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, "", true) {
            @Override
            public void handleResponse(Recipe loadedRecipe) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(loadedRecipe.getRecipeName());
                super.handleResponse(loadedRecipe);
            }
        });

        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });
    }

    public void submitComment() {

        Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, "", true) {
            @Override
            public void handleResponse(Recipe loadedrecipe) {
                List<Comment> comment;
                if (loadedrecipe.getComments()==null)
                {
                    comment = new ArrayList<Comment>();
                }
                else
                {
                    comment = loadedrecipe.getComments();
                }
                TextView getcomment = (TextView)findViewById(R.id.enterComment);
                Comment usercomment = new Comment();
                usercomment.setComment(getcomment.getText().toString());
                comment.add(usercomment);
                loadedrecipe.setComments(comment);
                super.handleResponse(loadedrecipe);
                loadedrecipe.saveAsync(
                        new LoadingCallback<Recipe>(AddCommentActivity.this, "Adding Comment", true) {
                            @Override
                            public void handleResponse(Recipe recipe) {
                                super.handleResponse(recipe);
                                Intent backtorecipe = new Intent(AddCommentActivity.this,
                                        RecipeDetailActivity.class);
                                backtorecipe.putExtra("recipeId", id);
                                startActivity(backtorecipe);
                                finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                progressDialog.dismiss();

                                DialogHelper.createErrorDialog(AddCommentActivity.this,
                                        "Yummy Recipe Fault",
                                        fault.getMessage()).show();
                            }
                        });

            }
        });

    }

}
