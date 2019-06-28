package com.unibg.app3dsat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.unibg.app3dsat.util.DefaultValues;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static com.unibg.app3dsat.util.HashFunction.toSha256;

/**
 * Class: LoginActivity
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // ID to identity READ_CONTACTS permission request
    private static final int REQUEST_READ_CONTACTS = 0;

    // UserLoginTask: keep track of the login task to ensure we can cancel it if requested
    @Nullable
    private UserLoginTask mAuthTask = null;

    // UI references
    private AutoCompleteTextView mMailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Delete previous SharedPreferences (SPDoctor)
        SharedPreferences del1 = this.getSharedPreferences("SPDoctor", Context.MODE_PRIVATE);
        del1.edit().clear().commit();

        // Delete previous SharedPreferences (SPPatient)
        SharedPreferences del2 = this.getSharedPreferences("SPPatient", Context.MODE_PRIVATE);
        del2.edit().clear().commit();

        // Set up the login form
        mMailView = findViewById(R.id.mail);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            /**
             * Method: onEditorAction
             *
             * @param textView
             * @param id
             * @param keyEvent
             * @return boolean
             */
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Sign in button
        Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Sign up button
        Button mSignUpButton = findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://se4med.unibg.it/home/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Method: populateAutoComplete
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Method: mayRequestContacts
     *
     * @return boolean
     */
    private boolean mayRequestContacts() {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mMailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Method: onRequestPermissionsResult
     * Callback received when a permissions request has been completed
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Method: attemptLogin
     * Attempts to sign in or register the account specified by the login form;
     * If there are form errors (invalid mail, missing fields), the
     * errors are presented and no actual login attempt is made
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors
        mMailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        String mail = mMailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid mail address
        if (TextUtils.isEmpty(mail)) {
            mMailView.setError(getString(R.string.error_field_required));
            focusView = mMailView;
            cancel = true;
        } else if (!isMailValid(mail)) {
            mMailView.setError(getString(R.string.error_invalid_mail));
            focusView = mMailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt
            showProgress(true);
            mAuthTask = new UserLoginTask(mail, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Method: isMailValid
     *
     * @param mail
     * @return boolean
     */
    private boolean isMailValid(String mail) {
        return mail.contains("@");
    }

    /**
     * Method: isPasswordValid
     *
     * @param password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Method: showProgress
     * Shows the progress UI and hides the login form
     *
     * @param show
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations;
        // If available, use these APIs to fade-in the progress spinner
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Method: onCreateLoader
     *
     * @param i
     * @param bundle
     * @return Loader
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only mail addresses
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary mail addresses first; Note that there won't be
                // a primary mail address if the user hasn't specified one
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /**
     * Method: onLoadFinished
     *
     * @param cursorLoader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, @NonNull Cursor cursor) {
        List<String> mails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addMailsToAutoComplete(mails);
    }

    /**
     * Method: onLoaderReset
     *
     * @param cursorLoader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    /**
     * Method: addMailsToAutoComplete
     *
     * @param mailAddressCollection
     */
    private void addMailsToAutoComplete(@NonNull List<String> mailAddressCollection) {
        // Create adapter to tell the AutoCompleteTextView what to show in its dropdown list
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, mailAddressCollection);

        mMailView.setAdapter(adapter);
    }

    /**
     * Interface: ProfileQuery
     */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Method: onBackPressed
     */
    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * Class: UserLoginTask
     * Represents an asynchronous login/registration task used to authenticate the user
     */
    @SuppressLint("StaticFieldLeak")
    class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * SharedPreferences
         */
        final SharedPreferences SPDoctor = getSharedPreferences(DefaultValues.SPDOCTOR, MODE_PRIVATE);
        private final String mMail;
        @NonNull
        private final String mPassword;
        private boolean neterror;

        /**
         * Constructor: UserLoginTask
         *
         * @param mail
         * @param password
         */
        UserLoginTask(String mail, @NonNull String password) {
            mMail = mail;

            try {
                mPassword = toSha256(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            // SharedPreferences
            SharedPreferences.Editor e = SPDoctor.edit();
            e.putString(DefaultValues.ACTUAL_DOCTOR_MAIL, mMail);
            e.putString(DefaultValues.ACTUAL_DOCTOR_PASSWORD, mPassword); // Password stored in hash
            e.apply();
        }

        /**
         * Method: doInBackground
         *
         * @param params
         * @return Boolean
         */
        @NonNull
        @Override
        protected Boolean doInBackground(Void... params) {
            // Initialized error variable to false
            neterror = false;

            // URI BUILDER
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .encodedAuthority(DefaultValues.AUTHORITY)
                    .appendPath("se4medservice")
                    .appendPath("")
                    .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.AUTHENTICATE_DOCTOR_NS_ACTION)
                    .appendQueryParameter(DefaultValues.EMAIL_PARAM, mMail)
                    .appendQueryParameter(DefaultValues.PASSWORD_PARAM, mPassword)
                    .appendQueryParameter(DefaultValues.IDAPP_PARAM, DefaultValues.STEREOTEST); // Insert semicolon

            String myUri = builder.build().toString();
            System.out.println("URI: " + myUri);

            // URL CONNECTION
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(myUri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                System.out.println("RESPONSE CODE: " + responseCode);

                if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED)) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    // Toast (Handler)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast t = Toast.makeText(getApplicationContext(), "Network error (HTTP)", Toast.LENGTH_LONG);
                            t.show();
                        }
                    });

                    neterror = true;

                    return false;
                }
            } catch (Exception e) {
                // Toast (Handler)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(), "Network error (E)", Toast.LENGTH_LONG);
                        t.show();
                    }
                });

                neterror = true;

                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            String login, name, surname;

            try {
                String response = "{" + result.toString().substring(result.toString().indexOf("{") + 1, result.toString().lastIndexOf("}")) + "}";
                System.out.println("RESPONSE: " + response);

                JSONObject json = new JSONObject(response);
                login = json.getString("login");
                System.out.println("LOGIN: " + login);

                if (login.equals(DefaultValues.LOGIN_OK)) {
                    name = json.getString("name");
                    surname = json.getString("surname");

                    // SharedPreferences
                    SharedPreferences.Editor e = SPDoctor.edit();
                    e.putString(DefaultValues.ACTUAL_DOCTOR_NAME, name);
                    e.putString(DefaultValues.ACTUAL_DOCTOR_SURNAME, surname);
                    e.apply();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Method: onPostExecute
         *
         * @param success
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // Intent
                Intent intent = new Intent(LoginActivity.this, MainDoctor.class);
                startActivity(intent);
                // Toast
                Toast toast = Toast.makeText(LoginActivity.this, "Login succesfull", Toast.LENGTH_LONG);
                toast.show();

                finish();
            } else if (neterror) {
                mMailView.setError("Network error");
                mPasswordView.setError("Network error");
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        /**
         * Method: onCancelled
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}