# Pathshare SDK for Android

![Platform](https://img.shields.io/badge/platform-android-green.svg?style=flat)
![Language](https://img.shields.io/badge/language-java-brightgreen.svg?style=flat)

**Pathshare** is a realtime location sharing platform. For more information please visit the [Pathshare Developer Page](https://pathsha.re/developers).

- [Requirements](#requirements)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
  - [Initialize Pathshare](#init-pathshare)
  - [Save Username](#save-username)
  - [Create Session](#create-session)
  - [Join Session](#join-session)
  - [Leave Session](#leave-session)
  - [Find Session](#find-session)

## Requirements

`PathshareSDK` for Android supports Android 5.x and 6.x.

## Installation

The installation of the `PathshareSDK` is simple. Just unzip the `pathshare-sdk-android.zip` file and copy the `repo` folder into the root of your project.

Next, reference the `repo` folder in your application `build.gradle` file:

```gradle
repositories {
    maven { url "file://$projectDir/../repo" }
}

dependencies {
    ...
    compile 'ch.freshbits.pathshare.sdk:pathshare-sdk:1.0.0'
}
```

## Basic Usage

### Initialization

In order to initialize the `PathshareSDK`, create a file named `pathshare.xml` inside the `res/value` folder and add your account token:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="pathshare_account_token">your Pathshare account token</string>
</resources>
```

Next, add the following to the `onCreate` method of your `Application` class:

```java
public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Pathshare.initialize(this, getString(R.string.pathshare_account_token));
    }
}
```

### Save Username

Before creating a session, you need to set a username:

```java
Pathshare.client().saveUserName("Candice", new ResponseListener() {
    @Override
    public void onSuccess() {
        // ...
    }

    @Override
    public void onError() {
        // ...
    }
});
```

### Create Session

To create a session, use the session builder:

```java
session = new Session.Builder()
    .setExpirationDate(expirationDate)
    .setName("Shopping")
    .setTrackingMode(TrackingMode.SMART) // optional
    .build();
```

A session needs an expiration date and a name. Optionally, you can specify a tracking mode to configure the behavior of the location tracker. The following tracking modes are available:

Tracking Mode      | Description
-------------------|------------------------------------------------------------
`SMART`            | Adapts intelligently to the environment and usage of the device. Includes awareness of battery level, travel speed and motion activity.
`ECO`              | Static mode providing constant tracking data with very low accuracy (several kilometers) and great distance between single locations and ensuring maximum battery life.
`APPROXIMATE`      | Static mode providing constant tracking data with low accuracy (several hundred meters) and middle distance between single locations. Useful when a low battery drain is a important criteria.
`ACCURATE`         | Static mode providing constant tracking with the highest accuracy possible (few meters) and small distances between locations. Useful for scenarios where a high accuracy is an essential requirement.


Make sure to save the session after building:

```java
session.save(new ResponseListener() { ... });

session.getIdentifier() // => 3fd919fe824d8e7b78e2c11c1570a6f168d2c...
session.isExpired() // => false
session.getURL() // => https://pathsha.re/6d39d5
```

#### Expiration

In order to react to the expiration of the session, add an `ExpirationListener`:

```java
session = new Session.Builder()
    // ...
    .setExpirationListener(new SessionExpirationListener() {
        @Override
        public void onExpiration() {
            // ...
        }
    })
    .build();
```

#### Destination

Optionally, you can add a destination to the session. Sessions with destination will show the estimated time of arrival (ETA) for each user. The destination identifier is used to group sessions by destination.

```java
Destination destination = new Destination.Builder()
    .setIdentifier("W2342")
    .setLatitude(47.378178)
    .setLongitude(8.539256)
    .build();

session = new Session.Builder()
    // ...
    .setDestination(destination)
    .build();
```

### Join Session

To join the session you created, call the `joinUser()` method on the session object:

```java
session.joinUser(new ResponseListener() { ... });

session.isUserJoined() // => true
```

This call will add your Pathshare user to the session and you will be able to see his location on a map in realtime in the Pathshare Professional web interface.

### Leave Session

In order to stop sending user location and remove the user from the session, call the `leaveUser()` method:

```java
session.leaveUser(new ResponseListener() { ... });
```

### Find Session

To find an existing session, use the `findSession()` method with the corresponding session identifier:

```java
Pathshare.client().findSession(identifier, new SessionResponseListener() {
    @Override
    public void onSuccess(Session session) {
        session.setSessionExpirationListener(
            new SessionExpirationListener() { ... }
        );
    }

    @Override
    public void onError() { ... }
}
```
