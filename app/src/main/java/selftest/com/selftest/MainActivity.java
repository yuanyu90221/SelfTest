package selftest.com.selftest;
// Add this to the header of your file:

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity {
    public CallbackManager callbackManager;
    private AccessToken accessToken;
    private ProfilePictureView userImage;
    private TextView loginInfo;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("[yuanyu][FB]", "activityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.d("[yuanyu]FB", "initialize FB Activity");
        super.onCreate(savedInstanceState);
        Log.d("[yuanyu]FB", "onCreate");


        setContentView(R.layout.activity_main);
        Log.d("[yuanyu]FB", "setContentView");
        callbackManager = CallbackManager.Factory.create();
        Log.d("[yuanyu]FB", "getFB callback");
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        userImage = (ProfilePictureView) findViewById(R.id.login_picture);
        loginInfo = (TextView) findViewById(R.id.logInfo);
        loginButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("[yuanyu][FB]", "onclick");
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                if(AccessToken.getCurrentAccessToken()==null){
                    userImage.setDefaultProfilePicture(null);
                    loginInfo.setText("");
                }
            }


        });
        Log.d("[yuanyu]FB", "setFB callback");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("[yuanyu][FB]", "FB Login Success");
                // accessToken
                accessToken = loginResult.getAccessToken();
                Log.d("[yuanyu][FB]", "access token got.");
                GraphRequest friendList = GraphRequest.newMyFriendsRequest(accessToken,
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray objects, GraphResponse response) {
                                Log.d("[yuanyu][FB]","object "+ objects.toString());
                                Log.d("[yuanyu][FB]","get friend Request Complete");
                                Log.d("[yuanyu][FB]","get total "+objects.length());
                                Log.d("[yuanyu][FB]","raw Data "+response.getRawResponse());
                              for(int i = 0; i < objects.length();i++){
                                  try {
                                      JSONObject obj =  objects.getJSONObject(i);
                                      Log.d("[yuanyu][FB]",obj.optString("name"));
                                      Log.d("[yuanyu][FB]",obj.optString("id"));
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                            }
                        });
                Bundle paramter1 = new Bundle();
                paramter1.putString("edges", "taggable_friends");
                friendList.setParameters(paramter1);

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("[yuanyu][FB]", "login Complete");
                                Log.d("[yuanyu][FB]", object.optString("name"));
                                Log.d("[yuanyu][FB]", object.optString("link"));
                                Log.d("[yuanyu][FB]", object.optString("id"));


                                userImage.setProfileId(object.optString("id"));
                                loginInfo.setText(object.optString("name"));

                            }

                        }
                );

                Log.d("[yuanyu][FB]", "before bundle request send");

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,link,id");
                request.setParameters(parameters);

                Log.d("[yuanyu][FB]", "after bundle request send");
                //  Test code
                GraphRequestBatch requests = new GraphRequestBatch(
                        friendList,
                        request
                      );

                requests.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d("[yuanyu][FB]", "FB Login Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("[yuanyu][FB]", "FB Login Error");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken()==null){
            userImage.setDefaultProfilePicture(null);
            loginInfo.setText("");
        }
        Log.d("[yuanyu]FB", "activity resume");
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AccessToken.getCurrentAccessToken()==null){
            userImage.setDefaultProfilePicture(null);
            loginInfo.setText("");
        }
        Log.d("[yuanyu]FB", "onPAUSE");
        AppEventsLogger.deactivateApp(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(AccessToken.getCurrentAccessToken()==null){
            userImage.setDefaultProfilePicture(null);
            loginInfo.setText("");
        }
        Log.d("[yuanyu]FB", "onStop");
        AppEventsLogger.deactivateApp(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(AccessToken.getCurrentAccessToken()==null){
            userImage.setDefaultProfilePicture(null);
            loginInfo.setText("");
        }
        Log.d("[yuanyu]FB", "onStart");
        AppEventsLogger.deactivateApp(this);

    }
}
