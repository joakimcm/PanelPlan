# **Architecture**
<br>
The project has a clear layered structure, where each layer has a specific responsibility.
We have the following layering:
<br>
<br>

**UI → ViewModel → Domain/Repository → Datasource → API**   
Where some ViewModels use the domain layer, but not all.

UI is separated from data handling, where the different layers communicate in the sequence shown
above.
We use the MVVM and UDF structures as the foundation for the architecture in this app.
I will return to this type of architecture later in the text.

We use Hilt for dependency injection, which leads to less code duplication and reduces
coupling between the layers.

You’ll find the following layers in the respective packages:
<br>
<br>
/UI <br>
UI elements – What the user sees  
ViewModel → Updates state and handle errors from the data-layer.

/Domain <br>
Class that combines repositories and provides information to the ViewModel

/Data <br>
Repository → Makes queries to the datasource to fetch data from the API. Performs light business
logic  
Datasource → Executes API calls

<br>

### **MVVM**
Here I list the layers we use, and provide an example file that represents each layer. I'll also explain how the layers work to give an understanding of how our application follow MVVM architecture.
<br>
<br>

**UI: FrontScreen (UI)**    
Composables observe the StateFlow in the ViewModel. It can be considered "dumb" and does nothing
other than
displaying data to the user.

**ViewModel: GeoDataViewModel (UI)**   
The ViewModel is part of the UI that holds and exposes the StateFlow.
It calls the repository to fetch data and implements logic for things like validation.
It also handle errors from network calls.

**Domain: CalculationService (not used by FrontScreen) (Domain layer)**   
Here, repositories are combined for the ViewModels that need them.
This is because, in certain cases, data from multiple repositories is required — for example in the
Presentation screen.

**Repository: GeoDataRepository (Data layer)**   
Here, data streams are collected and made easier for the ViewModel to access.
The repository also handles simple logic with the data calls.
Throws exceptions up to viewmodel.

**Data source: GeoDataDataSource (Data layer)**   
Here, API calls are made where the JSON response is serialized into data classes. These are defined
in the "model" package.
Throws exceptions up the layers.

<br>

### **UDF**  
If we take FrontScreen as an example, you can see in FrontScreen.kt in the "screens" package that it
has access to two states:

addressList and selectedAddress.

AddressList is retrieved from the ViewModel and starts as an empty list. As the user types their
address, this list gets updated
with relevant addresses that resemble what the user is typing.
When the user clicks on an address, the ViewModel is notified to update selectedAddress. This is
maintained in the ViewModel and is the one we pass along when the user
clicks the "next" button.

By using StateFlow, we achieve one-way data flow in the UI layer.
UI elements ask the ViewModel to update the StateFlow (which then fetches data from the data layer),
and this is then made available to the user
in its updated state.

Both MVVM, UDF, and Hilt contribute to low coupling and high cohesion where:

1. The layers are not directly dependent on each other due to the use of Hilt → Low coupling
    - GeoDataViewModel doesn’t care how GeoDataRepository fetches data from GeoDataDataSource.
    - You can change the repository and data source without changing the ViewModel (e.g., during
      testing)

2. There is a clear division of responsibilities for each layer → High cohesion
    - UI, data fetching, and how they communicate are clearly structured and divided.

<br>

# **Operations**
**Dependency Injection (Hilt)** <br>
The project uses Hilt for Dependency Injection.  
Hilt simplifies lifecycle and dependency management for ViewModels, repositories, and databases,
making
it easier to write testable code.  
Hilt is integrated via @HiltAndroidApp, @AndroidEntryPoint, and ViewModels are annotated with
@HiltViewModel.  
See for example PresentationViewModel.kt

<br>

**Room Database** <br>
For local storage, Room is used, which is a wrapper around SQLite.  
Room configuration includes KSP compilation and support for @Dao and @Entity.  
See the "favorites" package under "data"

<br>

**API Calls** <br>
Network communication is handled via Ktor combined with kotlinx.serialization for JSON parsing.  
This provides a simpler and more flexible alternative to Retrofit.  
The code is structured with a dedicated repository and data layer for API calls.  
See for example the "frost" package under "data"

<br>

**Navigation** <br>
The project uses Jetpack Navigation Compose with type-safe routes.  
This provides better compile-time safety and easier management of navigation between screens.  
All navigation is collected in a NavHost in Navigation.kt, and routes are defined in
NavigationClasses.kt in
the top-level package

<br>

**API Level** <br>

- minSdk: 26 – Android 7.0
- targetSdk: 35 – Latest Android version (Upside Down Cake)
- compileSdk: 35 – Latest Android version  
  <br>
  We have chosen minSdk 26 to support most devices while still being able to use new
  features from newer Android versions.   
  targetSdk and compileSdk are set to the latest available levels to ensure compatibility and
  best possible performance.
