### Build requirements
You must have a debug key registered for Facebook Developement. Our groups share the same Facebook Developer account. You will need to specify the location of you debug key in ```local.properties```:

```
keys.ledgerGroupDebug=/home/user/XXX.keystore
keys.ledgerGroupDebug.password=XXX

// The following line is optional, and not effective for the 'Release' build variant.
// But be aware that the url is quoted.
debug.server.url="http://abc.com/"
```

JDK 8, and Build tools with version precisely equal to "23.0.2" are required.

### Explanation of Package structure:
![](webImages/packages.png)

### JUnit HOW-TO:
We use JUnit testing system which is different from the default "Android Instrument" system. It enables testing without an Android device. However this requires changes of IDE Settings. Please change the settings for "Build Variants" in your Android Studio, as follows:

![](webImages/JUnit.png)

