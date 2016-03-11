package edu.scu.rachna.yummyrecipes.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DialogHelper;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;
import edu.scu.rachna.yummyrecipes.task.DownloadUrlAsyncTask;

public class RecipeDetailActivity extends BaseActivity {

    private static final String TAG = "RecipeDetailActivity";

    private FloatingActionButton addNewCommentToRecipeButton;
    private String id;

    private TextView ingredients;
    private TextView recipeName;
    private TextView prepTime;
    private TextView serves;
    private TextView recipeSteps;
    private ImageView recipeImage;
    private TextView likes;

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
                recipeName = (TextView) findViewById(R.id.recipeName);
                prepTime = (TextView)findViewById(R.id.prepTime);
                serves = (TextView)findViewById(R.id.serves);
                ingredients = (TextView) findViewById(R.id.recipeIngredients);
                recipeSteps = (TextView) findViewById(R.id.recipeMethod);
                recipeImage = (ImageView) findViewById(R.id.recipeImage);
                likes = (TextView) findViewById(R.id.likesDisplay);
                ActionBar actionBar = getSupportActionBar();

                recipeName.setText(loadedrecipe.getRecipeName());


                actionBar.setTitle(loadedrecipe.getRecipeName());

                prepTime.setText(String.valueOf(loadedrecipe.getTime()));
                serves.setText(String.valueOf(loadedrecipe.getServes()));
                ingredients.setText(loadedrecipe.getIngredients());
                recipeSteps.setText(loadedrecipe.getDirections());
                Picasso.with(getApplicationContext()).load(loadedrecipe.getImage()).fit().into(recipeImage);
                likes.setText(String.valueOf(loadedrecipe.getLikes()));

                super.handleResponse(loadedrecipe);
            }
        });

        addNewCommentToRecipeButton = (FloatingActionButton) findViewById(R.id.addNewCommentToRecipeButton);

        addNewCommentToRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toast("Add a new comment clicked!!!");
                Intent addNewCommentToRecipeButtonIntent = new Intent(RecipeDetailActivity.this, AddCommentActivity.class);
                //Pass current selected Recipe object or recipeId into intent
                addNewCommentToRecipeButtonIntent.putExtra("recipeId", id);
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
                handleShareClick();
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

    public void handleShareClick() {
        Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, false) {

            @Override
            public void handleFault(BackendlessFault fault) {
                progressDialog.dismiss();
                DialogHelper.createErrorDialog(RecipeDetailActivity.this,
                        "BackendlessFault",
                        fault.getMessage()).show();
            }

            @Override
            public void handleResponse(Recipe loadedRecipe) {
                Resources resources = getResources();

                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);

                StringBuilder sb = new StringBuilder();
                sb.append("Recipe Name : ")
                        .append(loadedRecipe.getRecipeName()).append("\n")
                        .append("Recipe Ingredients : ")
                        .append(loadedRecipe.getIngredients()).append("\n")
                        .append("Recipe Method : ")
                        .append(loadedRecipe.getDirections()).append("\n");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

                List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resolveInfoList.size(); i++) {

                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo rInfo = resolveInfoList.get(i);
                    String packageName = rInfo.activityInfo.packageName;

                    if(packageName.contains("android.email")) {
                        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Recipe : " + loadedRecipe.getRecipeName());
                        emailIntent.setType("message/rfc822");
                        downloadAndAttachImageFromBackendLessAsync(emailIntent, loadedRecipe);
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("facebook") || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, rInfo.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");

                        if(packageName.contains("facebook")) {
                            //TODO : Need to use facebook SDK to allow sharing recipe on facebook
                            //Facebook does not allow INTENT.EXTRA_TEXT to be posted to facebook posts
                            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        } else if(packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {
                            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                            downloadAndAttachImageFromBackendLessAsync(intent, loadedRecipe);
                            if(packageName.contains("com.google.android.gm")) {
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Recipe : " + loadedRecipe.getRecipeName());
                            }
                        }
                        intentList.add(new LabeledIntent(intent, packageName, rInfo.loadLabel(pm), rInfo.icon));
                    }
                }

                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);

                super.handleResponse(loadedRecipe);
            }
        });
    }

    public void downloadAndAttachImageFromBackendLessAsync(Intent intent, Recipe loadedRecipe) {
        DownloadUrlAsyncTask downloadTask;
        downloadTask = new DownloadUrlAsyncTask(getApplicationContext());
        Bitmap bm = null;
        try {
            bm = (Bitmap) downloadTask.execute(loadedRecipe.getImage()).get();
            intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bm));
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error occured while fetching image from backendless.", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "Error occured while fetching image from backendless.", e);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //Write to Media store (Requires external storage)
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}