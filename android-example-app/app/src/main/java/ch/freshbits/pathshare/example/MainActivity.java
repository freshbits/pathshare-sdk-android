package ch.freshbits.pathshare.example;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

import ch.freshbits.pathshare.sdk.Pathshare;
import ch.freshbits.pathshare.sdk.helper.PermissionRequester;
import ch.freshbits.pathshare.sdk.helper.ResponseListener;
import ch.freshbits.pathshare.sdk.helper.SessionExpirationListener;
import ch.freshbits.pathshare.sdk.helper.SessionResponseListener;
import ch.freshbits.pathshare.sdk.location.TrackingMode;
import ch.freshbits.pathshare.sdk.model.Destination;
import ch.freshbits.pathshare.sdk.model.Session;

public class MainActivity extends Activity {
    private static final int TAG_PERMISSIONS_REQUEST_LOCATION_ACCESS = 1;
    private static final String SESSION_PREFERENCES= "session";
    private static final String SESSION_ID_KEY = "session_id";

    Session mSession;
    Button mCreateButton;
    Button mJoinButton;
    Button mLeaveButton;

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

        findSession();
    }

    private void initializeCreateButton() {
        getCreateButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void createSession() {
        try {
            Destination destination = new Destination.Builder()
                .setIdentifier("w9823")
                .setLatitude(37.7951616)
                .setLongitude(-122.4049222)
                .build();

            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);

            mSession = new Session.Builder()
                    .setDestination(destination)
                    .setExpirationDate(expirationDate)
                    .setName("simple session")
                    .setTrackingMode(TrackingMode.SMART)
                    .setSessionExpirationListener(new SessionExpirationListener() {
                        @Override
                        public void onExpiration() {
                            handleSessionExpiration();
                        }
                    })
                    .build();

            getSession().save(new ResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d("Session", "Success");
                    saveSessionIdentifier();

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

        if (hasLocationPermission()) {
            performJoinSession();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        PermissionRequester.requestPermission(this, TAG_PERMISSIONS_REQUEST_LOCATION_ACCESS, Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_access_fine_location_rationale);
    }

    private boolean hasLocationPermission() {
        return PermissionRequester.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAG_PERMISSIONS_REQUEST_LOCATION_ACCESS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performJoinSession();
                } else {
                    Toast.makeText(this, R.string.permission_access_fine_location_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void performJoinSession() {
        try {
            if (getSession().isUserJoined()) { return; }

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

                    deleteSessionIdentifier();
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

    private void saveSessionIdentifier() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(SESSION_ID_KEY, getSession().getIdentifier());
        editor.apply();
    }

    private void deleteSessionIdentifier() {
        getPreferences().edit().clear().commit();
    }

    private void handleSessionExpiration() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getLeaveButton().setEnabled(false);
                getCreateButton().setEnabled(true);
                showToast("Session expired.");
            }
        });
    }

    public void setSession(Session session) {
        mSession = session;
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

    private void findSession() {
        String identifier = getPreferences().getString(SESSION_ID_KEY, null);

        if (identifier == null) { return; }

        Pathshare.client().findSession(identifier, new SessionResponseListener() {
            @Override
            public void onSuccess(Session session) {
                Log.d("Session", "Name: " + session.getName());

                session.setSessionExpirationListener(new SessionExpirationListener() {
                    @Override
                    public void onExpiration() {
                        handleSessionExpiration();
                    }
                });

                setSession(session);

                getCreateButton().setEnabled(false);
                getJoinButton().setEnabled(true);
                getLeaveButton().setEnabled(false);
            }

            @Override
            public void onError() {
                showToast("Something went wrong.");
            }
        });
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private SharedPreferences getPreferences() {
        return getApplicationContext().getSharedPreferences(SESSION_PREFERENCES, Context.MODE_MULTI_PROCESS);
    }
}
