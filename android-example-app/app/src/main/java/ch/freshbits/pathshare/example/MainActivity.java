package ch.freshbits.pathshare.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;

import ch.freshbits.pathshare.sdk.Pathshare;
import ch.freshbits.pathshare.sdk.helper.ResponseListener;
import ch.freshbits.pathshare.sdk.location.TrackingMode;
import ch.freshbits.pathshare.sdk.model.Destination;
import ch.freshbits.pathshare.sdk.model.Session;

public class MainActivity extends Activity {
    private Session mSession;
    private Button mCreateButton;
    private Button mJoinButton;
    private Button mLeaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateButton = (Button) findViewById(R.id.create_session);
        mJoinButton = (Button) findViewById(R.id.join_session);
        mLeaveButton = (Button) findViewById(R.id.leave_session);

        initializeCreateButton();
        initializeJoinButton();
        initializeLeaveButton();
    }

    private void initializeCreateButton() {
        getCreateButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser();
            }
        });
    }

    private void initializeJoinButton() {
        getJoinButton().setEnabled(false);
        getJoinButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinSession();
            }
        });
    }

    private void initializeLeaveButton() {
        getLeaveButton().setEnabled(false);
        getLeaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveSession();
            }
        });
    }

    private void saveUser() {
        Pathshare.client().saveUserName("SDK User 1", new ResponseListener() {
            @Override
            public void onSuccess() {
                Log.d("User", "Success");
                createSession();
            }

            @Override
            public void onError() {
                Log.e("User", "Error");
            }
        });
    }

    private void createSession() {
        try {
            Destination destination = new Destination.Builder()
                .setIdentifier("w9823")
                .setLatitude(47.378178)
                .setLongitude(8.539256)
                .build();

            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);

            mSession = new Session.Builder()
                    .setDestination(destination)
                    .setExpirationDate(expirationDate)
                    .setName("simple session")
                    .setTrackingMode(TrackingMode.SMART)
                    .build();
            // TODO: set expiration listener

            getSession().save(new ResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d("Session", "Success");
                    getCreateButton().setEnabled(false);
                    getJoinButton().setEnabled(true);
                }

                @Override
                public void onError() {
                    Log.e("Session", "Error");
                }
            });

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void joinSession() {
        if (getSession().isExpired()) { return; }

        try {
            getSession().joinUser(new ResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d("Join", "Success");
                    getJoinButton().setEnabled(false);
                    getLeaveButton().setEnabled(true);
                }

                @Override
                public void onError() {
                    Log.e("Join", "Error");
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void leaveSession() {
        if (getSession().isExpired()) { return; }

        try {
            getSession().leaveUser(new ResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d("Leave", "Success");
                    getLeaveButton().setEnabled(false);
                    getCreateButton().setEnabled(true);
                }

                @Override
                public void onError() {
                    Log.e("Leave", "Error");
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return mSession;
    }

    public Button getCreateButton() {
        return mCreateButton;
    }

    public Button getJoinButton() {
        return mJoinButton;
    }

    public Button getLeaveButton() {
        return mLeaveButton;
    }
}
