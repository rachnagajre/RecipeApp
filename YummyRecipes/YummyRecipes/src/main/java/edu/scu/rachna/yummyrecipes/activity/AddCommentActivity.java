package edu.scu.rachna.yummyrecipes.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.scu.rachna.yummyrecipes.R;

public class AddCommentActivity extends BaseActivity {


    private EditText enterComment;
    private Button submitCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enterComment = (EditText) findViewById(R.id.enterComment);
        submitCommentButton = (Button) findViewById(R.id.submitCommentButton);

        ActionBar actionBar = getSupportActionBar();
        /**
         *  TODO : Recipe Name for which comment is being added should go here
         *  Recipe id, name or entire recipe object can be passed by intent
         *  and data should be fetched from intent and Title should be assigned accordingly
         */
        actionBar.setTitle("Recipe Name");

        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });
    }

    public void submitComment() {
        /**
         * TODO : Add backend code here that will submit comment for current selected recipe
         *   Current recipe object will be passed by intent
         *   After comment is added successfully call finish() method to finish current activity and
         *   automatically go back to the previous RecipeDetailActivity
         */
        toast("Add Comment clicked!!");
    }

}
