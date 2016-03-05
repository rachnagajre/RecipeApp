package edu.scu.rachna.yummyrecipes.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import edu.scu.rachna.yummyrecipes.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(this instanceof RecipeDetailActivity) {
            MenuItem shareMenuItem = menu.findItem(R.id.action_share);
            shareMenuItem.setVisible(true);
            //TODO : Set Delete Action to false only if recipes do not belong to the user
            MenuItem removeMenuItem = menu.findItem(R.id.action_delete);
            removeMenuItem.setVisible(false);
            MenuItem closeButton = menu.findItem(R.id.action_mode_close_button);
            closeButton.setVisible(true);
            MenuItem likeButton = menu.findItem(R.id.action_like);
            likeButton.setVisible(true);
        } else if(this instanceof AddCommentActivity) {
            //For Add Comment Activity only show back buttonand nothing else on menu
            MenuItem shareMenuItem = menu.findItem(R.id.action_share);
            shareMenuItem.setVisible(false);
            MenuItem removeMenuItem = menu.findItem(R.id.action_delete);
            removeMenuItem.setVisible(false);
            MenuItem closeButton = menu.findItem(R.id.action_mode_close_button);
            closeButton.setVisible(true);
            MenuItem likeButton = menu.findItem(R.id.action_like);
            likeButton.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                /**
                 * TODO : Increment Like counter on backend less for current recipe and reload the data for RecipeDetailActivity
                 * Reloading RecipeDetailActivity should reload the total like counter on page
                 */
                toast("Like action");
                break;
            default :
                this.finish();
                break;
        }
        return true;
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
