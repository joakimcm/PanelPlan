# NB!
## This is not the original repository for the project. The original is under UiO-github, making it unaccessible for people outside this organisation.
<br>
The code is produced by the following people: <br>
- Joakim Caspar Meen <br>
- Helma Stavem <br>
- Rodas Micael Gebrigiwet <br>
- Pernille Aass <br>
- Maryan Ali Nor <br>
- Deka Ali Bulhan <br>

# *Panelplan*
The app Panelplan gives users the possibility to see how much energy they can produce from installing solar panels on their roof. The app also gives information about how much can be saved on the power bill and gives real life examples on what you can do with the given power outcome. 

**The app runs by accessing it through the emulator in Android Studio and press "Run".
You can clone this repository to run it.
We have optimized the code for the device called "Resizable (Experimental) API 34."**

<br>

## **Permissions:**

We use this permission to give the app internet-access. This is important to execute API-calls.

````
<uses-permission android:name="android.permission.INTERNET"/>
````

<br>

## **Libraries:**

We use these to get access to the Ktor library.
Ktor is a HTTP-client for Kotlin and Android, it support asynchronized network calls and
serialization.

````
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation(libs.ktor.client.core)
implementation(libs.ktor.client.cio)
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.serialization.kotlinx.json)
````

<br>

We use the library from MapBox to show a map in our application.
It is a third party service that is very developer friendly.
There is a SDK as well, something that makes it easy to implement.

````
implementation("com.mapbox.maps:android:11.11.0")
implementation("com.mapbox.extension:maps-compose:11.11.0")

````

<br>

We use Room-database to save different roofs the user wants to view multiple times.
We save the data locally with Room.

````
implementation(libs.androidx.room.ktx)
implementation(libs.androidx.room.runtime)
ksp(libs.androidx.room.compiler)
annotationProcessor(libs.androidx.room.compiler)

````

<br>

We use Hilt for dependency injection and cleaner code.
Hilt is used to inject dependencies to the different components. For example repository in
viewmodel.

````
implementation(libs.hilt.android)
kapt(libs.hilt.android.compiler)
implementation(libs.androidx.hilt.navigation.compose.v110)

````

<br>

We use these libraries for testing.
Mock is for making fake repositories and data sources. Theres also some libraries for testing of
coroutines.

````
 testImplementation(libs.junit)
 testImplementation("io.mockk:mockk:1.14.0")
 testImplementation(libs.androidx.core.testing)
 testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

````

<br>

We use different libraries for UI.
These include icons and animation-libraries.

````
Ikoner:
 implementation("androidx.compose.material3:material3:1.1.2")
 implementation("androidx.compose.material3:material3:1.2.0-beta01")
 implementation(libs.androidx.material.icons.extended)
    
Animation:
 implementation(libs.lottie.compose)

Splash screen:
 implementation("androidx.core:core-splashscreen:1.0.1")
 implementation ("com.google.android.material:material:1.9.0")

````

<br>

## **Warnings and errors:**

During development, several warnings were observed, primarily related to file access, performance,
and API usage. We did not have time to resolve these issues, but I'll list them up here
and mention the actions that could be taken to resolve these issues:
<br>
<br>

### File Access Warnings:
Multiple warnings indicated that certain files could not be opened, 
suggesting potential issues in the APK build process. 
Suggested action involves verifying the build process and performing a clean rebuild.
However, a clean rebuild did not work and we would have to dig deeper into this issue. 

<br>

### Performance Concerns:
Lock verification warnings for SnapshotStateList point to potential 
ProGuard misconfigurations, which could degrade performance. 
A review and adjustment of ProGuard settings are recommended.

<br>

### OpenGL Renderer Issues: 
Warnings related to OpenGL configurations may affect visual performance. 
It is advisable to run tests on actual devices to assess the impact.
It is hard to know for sure what the issue is, because we only have access to the emulator. 


<br>

### Skipped Frames: 
A warning regarding skipped frames indicates that the app 
may perform heavy processing on the main thread. 
To enhance user experience, optimizations should be applied 
to ensure smoother UI interactions.

We are doing network calls on another thread, and making sure these a coroutine
functions. The warnings seem difficult to avoid, and we therefore address them here.


<br>

### Accessing Hidden APIs:
Warnings about accessing hidden fields signal a need for caution as they may lead to
future compatibility issues. 
Refactoring the code to use official APIs is recommended.


<br>

### Lottie Animation:
Multiple warnings indicate that layer effects 
and merge paths are not supported in certain versions.
Adjustments to animations could be made for optimal performance.


<br>

### Surface Management Issues:
Warnings about trying to destroy already destroyed surfaces 
suggest potential lifecycle management improvements in the application.
We did not have the time to resolve this properly, even though we've tried to find the root cause. 

