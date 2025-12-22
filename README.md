# Restorik

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

## Contributing

Contributions are welcome! Feel free to submit issues or pull requests.

## License

[Add your license information here]

---

Built with ❤️ using modern Android development practices
