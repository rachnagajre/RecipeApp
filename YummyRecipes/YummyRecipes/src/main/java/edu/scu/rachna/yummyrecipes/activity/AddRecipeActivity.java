package edu.scu.rachna.yummyrecipes.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DialogHelper;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;

public class AddRecipeActivity extends AppCompatActivity {

    private Button takePictureButton;
    private ImageView capturedImage;
    private EditText recipeName;
    private EditText prepTime;
    private EditText serves;
    private EditText recipeIngredients;
    private EditText recipeMethod;
    private String imagePath;
    private Bitmap bp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY, Default.VERSION);
        takePictureButton = (Button) findViewById(R.id.takePictureButton);
        capturedImage = (ImageView) findViewById(R.id.recipeImage);
        recipeName = (EditText) findViewById(R.id.recipeName);
        prepTime = (EditText)findViewById(R.id.prepTime);
        serves = (EditText)findViewById(R.id.serves);
        recipeIngredients = (EditText) findViewById(R.id.recipeIngredients);
        recipeMethod = (EditText) findViewById(R.id.recipeMethod);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            bp = (Bitmap) data.getExtras().get("data");
            capturedImage.setImageBitmap(bp);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "PN_" +Backendless.UserService.CurrentUser().getUserId()+"_"+ timeStamp + ".jpg";  //create the image file name
            imagePath="https://api.backendless.com/" + Default.APPLICATION_ID + "/" + Default.VERSION + "/files/mypics/" + imageFileName;
            Backendless.Files.Android.upload(bp, Bitmap.CompressFormat.PNG, 100, imageFileName,
                    "mypics", new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile backendlessFile) {
                            Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG)
                                    .show();

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(AddRecipeActivity.this, backendlessFault.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Backendless.UserService.CurrentUser().getEmail()
        }
        else{
            Toast.makeText(getApplicationContext(),"No picture Taken", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_recipe, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setVisible(true);
        MenuItem closeMenuItem = menu.findItem(R.id.action_mode_close_button);
        closeMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save :
                saveRecipe();
                break;
            case R.id.action_mode_close_button:
                finish();
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    private void saveRecipe() {
        TextView getrname = (TextView) findViewById(R.id.recipeName);
        TextView getprepTime = (TextView) findViewById(R.id.prepTime);
        TextView getserves = (TextView) findViewById(R.id.serves);
        TextView getingredients = (TextView) findViewById(R.id.recipeIngredients);
        TextView getsteps = (TextView) findViewById(R.id.recipeMethod);

        Recipe recipe = new Recipe();
        recipe.setCreator(Backendless.UserService.CurrentUser());
        recipe.setDirections(getsteps.getText().toString());
        recipe.setIngredients(getingredients.getText().toString());
        recipe.setRecipeName(getrname.getText().toString());
        recipe.setPrepTime(getprepTime.getText().toString());
        recipe.setServes(getserves.getText().toString());

        recipe.setLikes(0);
        if (imagePath != null)
        {
            recipe.setImage(imagePath);
        }
        recipe.saveAsync(new LoadingCallback<Recipe>(AddRecipeActivity.this,"Saving Recipe", true) {
            @Override
            public void handleResponse(Recipe recipe) {
                super.handleResponse(recipe);
                Intent dashboardintent = new Intent(AddRecipeActivity.this,DashboardActivity.class);
                //orderSuccessIntent.putExtra("restaurant", restaurant);
                //orderSuccessIntent.putExtra("location", location);
                //orderSuccessIntent.putExtra("order", order);
                startActivity(dashboardintent);
                finish();
            }

            @Override
            public void handleFault( BackendlessFault fault )
            {
                progressDialog.dismiss();

                DialogHelper.createErrorDialog(AddRecipeActivity.this, "BackendlessFault",
                        fault.getMessage()).show();
            }
        });
    }

}

