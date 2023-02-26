# Pathshare SDK for Android


![GitHub release](https://img.shields.io/github/release/freshbits/pathshare-sdk-android.svg?style=flat)
![Platform](https://img.shields.io/badge/platform-android-green.svg?style=flat)
![Language](https://img.shields.io/badge/language-java-brightgreen.svg?style=flat)
![Language](https://img.shields.io/badge/language-kotlin-brightgreen.svg?style=flat)

**Pathshare** is a realtime location sharing platform. For more information please visit the [Pathshare Developer Page](https://pathsha.re/professional/developers).

<img src="/assets/android-example-app.png" height="600">

- [Requirements](#requirements)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
  - [Initialize Pathshare](#init-pathshare)
  - [Save Username](#save-username)
  - [Create Session](#create-session)
  - [Join Session](#join-session)
  - [Invite Customer](#invite-customer)
  - [Leave Session](#leave-session)
  - [Find Session](#find-session)

## Requirements

`PathshareSDK` for Android supports Android 5.x, 6.x, 7.x, 8.x, 9.x, 10.x, 11.x, 12.x and 13.x.

## Installation

The installation of the `PathshareSDK` is simple. Download the latest `pathshare-sdk-android-[version].zip` from [Releases](https://github.com/freshbits/pathshare-sdk-android/releases), unzip and copy the `PathshareSDK` folder into the root of your project.

Next, reference the `PathareSDK` folder in your application `build.gradle` file:

```gradle
repositories {
    maven { url "file://$projectDir/../PathshareSDK" }
}

dependencies {
    ...
    compile 'ch.freshbits.pathshare.sdk:pathshare-sdk:2.3.3'
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

###### Java
```java
public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Pathshare.initialize(this, getString(R.string.pathshare_account_token), TrackingMode.SMART);
    }
}
```
###### Kotlin
```kotlin
class ExampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Pathshare.initialize(this, getString(R.string.pathshare_account_token), TrackingMode.SMART)
    }
}
```

Optionally, you can specify a tracking mode to configure the behavior of the location tracker. The following tracking modes are available:

Tracking Mode      | Description
-------------------|------------------------------------------------------------
`SMART`            | Adapts intelligently to the environment and usage of the device. Includes awareness of battery level, travel speed and motion activity.
`ECO`              | Static mode providing constant tracking data with very low accuracy (several kilometers) and great distance between single locations and ensuring maximum battery life.
`APPROXIMATE`      | Static mode providing constant tracking data with low accuracy (several hundred meters) and middle distance between single locations. Useful when a low battery drain is a important criteria.
`ACCURATE`         | Static mode providing constant tracking with the highest accuracy possible (few meters) and small distances between locations. Useful for scenarios where a high accuracy is an essential requirement.

### Save User

Before creating a session, you need to save a user:

###### Java
```java
Pathshare.client().saveUser("Candice", "me@email.com", "+12345678901", UserType.DRIVER, getResources().getDrawable(R.drawable.face, null), new ResponseListener() {
    @Override
    public void onSuccess() {
        // ...
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        // ...
    }
});
```

###### Kotlin
```kotlin
Pathshare.client().saveUser("SDK User Android", "me@email.com", "+12345678901", UserType.DRIVER, resources.getDrawable(R.drawable.face, null), object: ResponseListener {
    override fun onSuccess() {
        // ...
    }

    override fun onError(throwable: Throwable) {
        // ...
    }
})
```
The email address can be `null`.

Use the same method `Pathshare.client().saveUser()` to create or update the user.

There are different types of users for specific industries:

User Types                  | Description
----------------------------|------------------------------------------------------------
`TECHNICIAN`, `MOTORIST`    | For roadside assitance industry or similar
`DRIVER`, `RECIPIENT`       | For delivery services or similar
`INVESTIGATOR`, `CLIENT`    | For legal services industry or similar

### Create Session

To create a session, use the session builder:

###### Java
```java
session = new Session.Builder()
    .setExpirationDate(expirationDate)
    .setName("Shopping")
    .build();
```

###### Kotlin
```kotlin
session = Session.Builder()
    .setExpirationDate(expirationDate)
    .setName("Shopping")
    .build()
```

A session needs an expiration date and a name. You can create multiple sessions at the same time, the SDK will manage them for you.


Make sure to save the session after building:

###### Java
```java
session.save(new ResponseListener() { ... });

session.getIdentifier() // => 3fd919fe824d8e7b78e2c11c1570a6f168d2c...
session.isExpired() // => false
session.getURL() // => https://pathsha.re/6d39d5
```

###### Kotlin
```kotlin
session.save(object: ResponseListener { ... })

session.identifier // => 3fd919fe824d8e7b78e2c11c1570a6f168d2c...
session.isExpired // => false
session.url // => https://pathsha.re/6d39d5
```

#### Expiration

In order to react to the expiration of the session, add an `ExpirationListener`:

###### Java
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

###### Kotlin
```kotlin
session = Session.Builder()
    // ...
    .setSessionExpirationListener { // ... }
    .build()
```

#### Destination

Optionally, you can add a destination to the session. Sessions with destination will show the estimated time of arrival (ETA) for each user. The destination identifier is used to group sessions by destination.

###### Java
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

###### Kotlin
```kotlin
val destination = Destination.Builder()
    .setIdentifier("W2342")
    .setLatitude(47.378178)
    .setLongitude(8.539256)
    .build()

session = Session.Builder()
    // ...
    .setDestination(destination)
    .build()
```

### Join Session

To join the session you created, call the `joinUser()` method on the session object:

###### Java
```java
session.join(new ResponseListener() { ... });

session.isUserJoined() // => true
```

###### Kotlin
```kotlin
session.join(object: ResponseListener { ... })

session.isUserJoined // => true
```

This call will add your Pathshare user to the session and you will be able to see his location on a map in realtime in the Pathshare Professional web interface.

### Invite customer

To invite a customer to the session, call the `inviteUser()` method on the session object:

###### Java
```java
session.inviteUser("Customer", UserType.CLIENT, "customer@email.com", "+12345678901", new InvitationResponseListener() {
    @Override
    public void onSuccess(URL url) {
        // ...
        Log.d("URL", url.toString()); // => https://m.pathsha.re/12s83a
    }

    public void onError(@NonNull Throwable throwable) {
        // ...
    }
});
```

###### Kotlin
```kotlin
session.inviteUser("Customer", UserType.CLIENT, "customer@me.com", "+12345678901", object: InvitationResponseListener {
    override fun onSuccess(url: URL?) {
        // ...
        Log.d("URL", url.toString()) // => https://m.pathsha.re/12s83a
    }

    override fun onError(throwable: Throwable) {
        // ...
    }
})
```

This call will create a customer user and return an invitation URL that can be sent to the customer using your preffered channel. The customer will then see the driver's location in realtime as well as the ETA in a white-labeled view with your corporate identity.

The customer will be able to enjoy the full realtime experience in the web browser of their smartphone:

<img src="/assets/web-view-customer.png" height="600">

### Leave Session

In order to stop sending user location and remove the user from the session, call the `leaveUser()` method:

###### Java
```java
session.leave(new ResponseListener() { ... });
```

###### Kotlin
```kotlin
session.leave(object: ResponseListener { ... })
```

### Find Session

To find an existing session, use the `findSession()` method with the corresponding session identifier:

###### Java
```java
Pathshare.client().findSession(identifier, new SessionResponseListener() {
    @Override
    public void onSuccess(Session session) {
        session.setSessionExpirationListener(
            new SessionExpirationListener() { ... }
        );
    }

    @Override
    public void onError(@NonNull Throwable throwable) { ... }
}
```

###### Kotlin
```kotlin
Pathshare.client().findSession(identifier, object: SessionResponseListener {
    override fun onSuccess(session: Session?) {
        session.sessionExpirationListener = SessionExpirationListener { ... }
    }

    override fun onError(throwable: Throwable) { ... }
})
```
