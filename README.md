# Restorik
<table>
  <tr>
    <td><img width="300" alt="image1" src="https://github.com/user-attachments/assets/f463af18-c27e-499b-a0d2-8e45d53d464c" /></td>
    <td><img width="300" alt="image1bis" src="https://github.com/user-attachments/assets/be2b3e89-85ba-4a5c-9cd0-07593dcfaa15" /></td>
    <td><img width="300" alt="image1 2-bis" src="https://github.com/user-attachments/assets/86c60318-d19b-496b-b250-4619df37696e" /></td>
  </tr>
  <tr>
    <td><img width="300" alt="image2" src="https://github.com/user-attachments/assets/7f548d10-88e8-4240-8c3c-4bad10151d1d" /></td>
    <td><img width="300" alt="image3" src="https://github.com/user-attachments/assets/43329699-4ccb-4283-b402-e889b0ae12d0" /></td>
    <td><img width="300" alt="image4" src="https://github.com/user-attachments/assets/30a7a5d7-b7aa-479b-9da2-4568b5dc9fcf" /></td>
  </tr>
  <tr>
    <td><img width="300" alt="image5" src="https://github.com/user-attachments/assets/46aa5148-f903-4964-b137-718680aa8405" /></td>
    <td><img width="300" alt="image6" src="https://github.com/user-attachments/assets/a854b0cf-0397-4e9d-bfa2-6e763aa7d3fc" /></td>
    <td><img width="300" alt="image7" src="https://github.com/user-attachments/assets/c8ac5121-3505-4279-8a62-0f5d0196d266" /></td>
  </tr>
  <tr>
    <td><img width="300" alt="image8" src="https://github.com/user-attachments/assets/cde595e4-4bd0-418d-b41c-19bb93cc3b10" /></td>
    <td><img width="300" alt="image9" src="https://github.com/user-attachments/assets/f7d8958b-f659-4277-a396-6b877f1eb78a" /></td>
    <td></td>
  </tr>
</table>

Restorik is an Android application designed to help you track and manage your restaurant dining experiences. Whether you're a food enthusiast, a restaurant explorer, or simply want to keep a record of your meals, Restorik provides an intuitive way to document every dining moment.

## What is Restorik?

Restorik allows you to:

- **Track your meals**: Record detailed information about each meal including the restaurant, dish name, price, rating, photos, and personal notes
- **Discover your dining patterns**: View comprehensive monthly statistics about your spending, favorite restaurants, and dining habits
- **Organize your experiences**: Search and filter through your meal history to find specific dishes or restaurants
- **Analyze your spending**: Get insights into your monthly restaurant spending with comparisons to previous months
- **Rate your experiences**: Keep track of which meals and restaurants you loved most with a 5-star rating system

## Features

### Meal Tracking
- Add detailed meal entries with photos, ratings, and comments
- Associate meals with restaurants (with autocomplete suggestions)
- Track the date, time, and price of each meal
- Edit or update meal information anytime

### Profile & Statistics
- View monthly spending totals with previous month comparisons
- Track the number of meals and unique restaurants visited each month
- See your average meal rating and spending
- Discover which restaurants you spend the most at
- Monitor new restaurants you've tried each month

### Search & Filter
- Search meals by name, restaurant, or comments
- Filter by date ranges, price ranges, and ratings
- Sort results by date, price, or rating

## Architecture

This app's architecture is heavily inspired by the [Now in Android](https://github.com/android/nowinandroid) project, following modern Android development best practices.

### Key Technologies

- **Jetpack Compose**: Modern declarative UI toolkit for building native Android interfaces
- **Material Design 3**: Latest Material Design components and theming
- **Kotlin Coroutines & Flow**: For asynchronous programming and reactive data streams
- **Room Database**: Local data persistence with SQLite
- **Hilt**: Dependency injection for better code organization and testability
- **Navigation Compose**: Type-safe navigation between screens
- **Coil**: Image loading and caching
- **ViewModel & StateFlow**: Managing UI state with lifecycle awareness

### Modular Architecture

The project follows a multi-module architecture pattern, separating concerns into distinct modules:

- **Feature modules** (`feature:meal`, `feature:profile`, `feature:search`): Contain UI and business logic for specific features
- **Core modules** (`core:data`, `core:database`, `core:model`, `core:ui`, `core:designsystem`): Provide shared functionality and common components
- **App module**: Main application module that brings everything together

This modular approach provides:
- Better separation of concerns
- Improved build times through parallel builds and build caching
- Reusability of components across features
- Easier testing and maintenance

### Feature Module Architecture

Each feature module follows a consistent Route/Screen pattern for clear separation between stateful and stateless components:

#### Route Composable (Stateful)
The Route is the **stateful** entry point for each feature:
- Manages the ViewModel using `hiltViewModel()`
- Collects and manages UI state with `collectAsState()`
- Handles `LaunchedEffect` side effects (navigation, callbacks to parent, etc.)
- Manages Android-specific dependencies (FocusManager, ActivityResultLaunchers, etc.)
- Passes clean, simple callbacks to the Screen

Example: `MealEditorRoute`, `SearchRoute`

#### Screen Composable (Stateless)
The Screen is a **pure, stateless** UI component:
- Receives all state as a single `UiState` parameter
- Receives all interactions as simple callback functions
- No ViewModel or state management
- No LaunchedEffect or side effects
- No Android-specific dependencies
- Easy to preview and test

Example: `MealEditorScreen`, `SearchScreen`

**Benefits of this pattern:**
- **Testability**: Screens can be tested easily by passing different states
- **Reusability**: Screens can be reused in different contexts (previews, tests, other features)
- **Separation of concerns**: State management is separate from UI rendering
- **Preview-friendly**: No complex dependencies needed for Compose previews
- **Maintainability**: Clear boundaries between logic and presentation

### Design Patterns

- **Repository Pattern**: Abstracting data sources and providing a clean API for data access
- **MVVM (Model-View-ViewModel)**: Separating UI logic from business logic
- **Unidirectional Data Flow**: Ensuring predictable state management
- **Dependency Injection**: Facilitating loose coupling and testability

## Building the Project

Clone the repository and open it in Android Studio:

```bash
git clone <repository-url>
cd Restorik
```

Open the project in Android Studio and sync Gradle files. The project uses Gradle's convention plugins for consistent build configuration across modules.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Built with ❤️ using modern Android development practices
