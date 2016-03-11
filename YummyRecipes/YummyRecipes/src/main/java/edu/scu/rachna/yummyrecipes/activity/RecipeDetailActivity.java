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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.adapter.CommentRowData;
import edu.scu.rachna.yummyrecipes.adapter.CustomCommentsAdapter;
import edu.scu.rachna.yummyrecipes.adapter.Helper;
import edu.scu.rachna.yummyrecipes.data.Comment;
import edu.scu.rachna.yummyrecipes.data.CommentComparator;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DialogHelper;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;
import edu.scu.rachna.yummyrecipes.task.DownloadUrlAsyncTask;

public class RecipeDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {

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
    private ListView commentsListView;

    private CustomCommentsAdapter commentsAdapter;

    private BackendlessUser loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY,
                Default.VERSION);

        id = getIntent().getStringExtra("recipeId");
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


                commentsListView = (ListView) findViewById(R.id.commentsList);
                if(commentsAdapter == null) {
                    commentsAdapter = new CustomCommentsAdapter(getApplicationContext(), R.layout.comment_list_item, convertCommentList(loadedrecipe.getComments()));
                } else {
                    commentsAdapter.updateCommentsList(convertCommentList(loadedrecipe.getComments()));
                }
                commentsListView.setVisibility(View.VISIBLE);
                commentsListView.setAdapter(commentsAdapter);
                commentsListView.setOnItemClickListener(RecipeDetailActivity.this);
                Helper.getListViewSize(commentsListView);
                super.handleResponse(loadedrecipe);
            }
        });

        addNewCommentToRecipeButton = (FloatingActionButton) findViewById(R.id.addNewCommentToRecipeButton);

        addNewCommentToRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewCommentToRecipeButtonIntent = new Intent(RecipeDetailActivity.this, AddCommentActivity.class);
                //Pass current selected Recipe object or recipeId into intent
                addNewCommentToRecipeButtonIntent.putExtra("recipeId", id);
                startActivity(addNewCommentToRecipeButtonIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        String recipeId = getIntent().getStringExtra("recipeId");
        Recipe.findByIdAsync(recipeId, new LoadingCallback<Recipe>(this, "Getting Recipe", false) {
            @Override
            public void handleResponse(Recipe currentRecipe) {
                loggedInUser = Backendless.UserService.CurrentUser();
                //Set delete menu item visible only if recipe belongs to the user
                MenuItem removeMenuItem = menu.findItem(R.id.action_delete);
                if(currentRecipe != null && loggedInUser!= null && loggedInUser.getObjectId().equals(currentRecipe.getCreator().getObjectId())) {
                    removeMenuItem.setVisible(true);
                } else {
                    removeMenuItem.setVisible(false);
                }
                progressDialog.hide();
            }
        });
        return true;
    }

    public List<CommentRowData> convertCommentList(List<Comment> commentsList) {
        if(CollectionUtils.isNotEmpty(commentsList)) {
            List<CommentRowData> returnList = new ArrayList<CommentRowData>();
            Collections.sort(commentsList, Collections.reverseOrder(new CommentComparator()));
            for(Comment c : commentsList) {
                CommentRowData r = new CommentRowData(c.getComment());
                returnList.add(r);
            }
            return returnList;
        } else {
            return new ArrayList<CommentRowData>();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int pid = item.getItemId();
        switch (pid) {
            case R.id.action_share:
                handleShareClick();
                break;
            case R.id.action_delete :
                Recipe.findByIdAsync(id, new LoadingCallback<Recipe>(this, "Deleting recipe", false) {
                    @Override
                    public void handleResponse(Recipe currentRecipe) {
                        progressDialog.hide();
                        Backendless.Persistence.of(Recipe.class).remove(currentRecipe, new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long aLong) {
                                progressDialog.hide();
                            }
                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                toast("Can not delete current recipe.");
                            }
                        });
                    }
                });
                finish();
                startActivity(new Intent(this, DashboardActivity.class));
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
                        .append("Recipe Preparation Time (minutes) : ")
                        .append(loadedRecipe.getTime()).append("\n")
                        .append("Recipe Servings : ")
                        .append(loadedRecipe.getServes()).append("\n")
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
                    } else if(packageName.contains("com.facebook.orca") ||
                              packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, rInfo.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");

                        if(packageName.contains("com.facebook.orca")) {
                            //Facebook Messanger package
                            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        } else if(packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {
                            //Whatsapp or Gmail package name
                            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                            downloadAndAttachImageFromBackendLessAsync(intent, loadedRecipe);
                            if(packageName.contains("com.google.android.gm")) {
                                //For Gmail, add subject for email also
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}