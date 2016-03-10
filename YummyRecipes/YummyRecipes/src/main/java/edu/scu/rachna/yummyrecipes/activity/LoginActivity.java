package edu.scu.rachna.yummyrecipes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Validator;



public class LoginActivity extends Activity
{
    private static final int REGISTER_REQUEST_CODE = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_login);

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY,
                Default.VERSION);

        Button loginButton = (Button) findViewById( R.id.loginButton );
        loginButton.setOnClickListener( createLoginButtonListener() );


        makeRegistrationLink();
    }

    public void makeRegistrationLink()
    {
        SpannableString registrationPrompt = new SpannableString( getString( R.string.register_prompt ) );

        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick( View widget )
            {
                startRegistrationActivity();
            }
        };

        String linkText = getString( R.string.register_link );
        int linkStartIndex = registrationPrompt.toString().indexOf( linkText );
        int linkEndIndex = linkStartIndex + linkText.length();
        registrationPrompt.setSpan( clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        TextView registerPromptView = (TextView) findViewById( R.id.registerPromptText );
        registerPromptView.setText( registrationPrompt );
        registerPromptView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void startRegistrationActivity()
    {
        Intent registrationIntent = new Intent( this, RegistrationActivity.class );
        startActivityForResult( registrationIntent, REGISTER_REQUEST_CODE );
    }


    public void loginUser( String email, String password, AsyncCallback<BackendlessUser> loginCallback )
    {
        Backendless.UserService.login( email, password, loginCallback,true);
    }


    public View.OnClickListener createLoginButtonListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                EditText emailField = (EditText) findViewById( R.id.emailField );
                EditText passwordField = (EditText) findViewById( R.id.passwordField );

                CharSequence email = emailField.getText();
                CharSequence password = passwordField.getText();

                if( isLoginValuesValid( email, password ) )
                {
                    LoadingCallback<BackendlessUser> loginCallback = createLoginCallback();

                    loginCallback.showLoading();
                    loginUser( email.toString(), password.toString(), loginCallback );
                }
            }
        };
    }

    public boolean isLoginValuesValid( CharSequence email, CharSequence password )
    {
        return Validator.isEmailValid(this, email) && Validator.isPasswordValid( this, password );
    }


    public LoadingCallback<BackendlessUser> createLoginCallback()
    {
        return new LoadingCallback<BackendlessUser>( this, getString( R.string.loading_login ) )
        {
            @Override
            public void handleResponse( BackendlessUser loggedInUser )
            {
                super.handleResponse(loggedInUser);
                //Toast.makeText(getApplicationContext(),"Works", Toast.LENGTH_LONG).show();
                startDashboardActivity(loggedInUser);

            }
        };
    }

    private void startDashboardActivity(BackendlessUser user) {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("objectId", user.getObjectId());
        bundle.putString("emailId", user.getEmail());
        bundle.putString("userId", user.getUserId());
        dashboardIntent.putExtras(bundle);
        startActivity(dashboardIntent);
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if( resultCode == RESULT_OK )
        {
            switch( requestCode )
            {
                case REGISTER_REQUEST_CODE:
                    String email = data.getStringExtra( BackendlessUser.EMAIL_KEY );
                    EditText emailField = (EditText) findViewById( R.id.emailField );
                    emailField.setText( email );

                    EditText passwordField = (EditText) findViewById( R.id.passwordField );
                    passwordField.requestFocus();

                    Toast.makeText(this, getString(R.string.info_registered_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
