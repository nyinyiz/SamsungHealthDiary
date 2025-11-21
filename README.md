# Samsung Health Diary - Redesign Edition

> **Gemini 3 Pro Experimental Project** - A modernized redesign of Samsung's Health Diary sample app with stunning Liquid Glass UI aesthetics.
> 
> **Original Project**: [Samsung Health Diary Sample](https://developer.samsung.com/health/data/sample/health-diary.html)

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Samsung Health](https://img.shields.io/badge/Samsung_Health-1428A0?style=for-the-badge&logo=samsung&logoColor=white)

</div>

---

## ✨ Features

| Feature | Description | Status |
|---------|-------------|--------|
| 🚶 **Steps** | Daily/weekly/monthly tracking with swipe navigation | ✅ Complete |
| ❤️ **Heart Rate** | Real-time monitoring with breakdowns | ✅ Complete |
| 😴 **Sleep** | Session tracking with quality metrics | 🚧 In Progress |
| 🍎 **Nutrition** | Meal logging and calorie tracking | ✅ Complete |
| 🌓 **Dark/Light Mode** | Dynamic theme with DataStore persistence | ✅ Complete |

---

## 🎨 Design System

**Liquid Glass + Neo-Neon Aesthetic**

```mermaid
graph LR
    A[Deep Black<br/>#0A0E27] --> B[Gradient Background]
    C[Cosmic Navy<br/>#1A1F3A] --> B
    B --> D[Glass Cards<br/>Semi-transparent<br/>Blur Effects]
    E[Electric Blue<br/>#00D4FF] --> F[Neon Accents]
    G[Magenta<br/>#FF006B] --> F
    F --> D
```

---

## 🏗️ Architecture

```mermaid
flowchart TD
    UI[🎨 UI Layer<br/>Jetpack Compose] --> VM[⚙️ ViewModel<br/>Hilt + StateFlow]
    VM --> UC[🔄 Use Cases<br/>Domain Logic]
    UC --> REPO[💾 Repository<br/>Data Layer]
    REPO --> SDK[📱 Samsung Health SDK]
    
    style UI fill:#00D4FF,stroke:#fff,color:#000
    style VM fill:#7F52FF,stroke:#fff,color:#fff
    style UC fill:#FF006B,stroke:#fff,color:#fff
    style REPO fill:#1A1F3A,stroke:#00D4FF,color:#fff
    style SDK fill:#1428A0,stroke:#fff,color:#fff
```

**Clean Architecture Pattern**: UI → ViewModel → UseCase → Repository → SDK

---

## 🚀 Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Kotlin | 2.0.21 |
| **UI** | Jetpack Compose | BOM 2024.12.01 |
| **DI** | Hilt | 2.54 |
| **Navigation** | Compose Navigation | 2.8.5 |
| **Async** | Coroutines + StateFlow | 1.7.3 |
| **Storage** | DataStore Preferences | 1.1.1 |
| **Health SDK** | Samsung Health Data API | 1.0.0 |

---

## 📦 Project Structure

```
healthdiary/
├── 🎨 ui/
│   ├── screens/          # 6 feature screens
│   ├── components/       # Reusable UI (GlassCard, GlassBox, etc.)
│   └── theme/           # Material 3 + Custom colors
├── 🎯 domain/
│   ├── model/           # Domain entities
│   └── usecase/         # Business logic
├── 💾 data/
│   └── repository/      # Data sources
├── ⚙️ viewmodel/         # Hilt ViewModels
└── 🔧 di/               # Dependency injection modules
```

---

## ⚡ Quick Start

### Prerequisites

- Android Studio Hedgehog+
- JDK 17
- Android device with Samsung Health

### Build & Run

```bash
# Clone and open in Android Studio
./gradlew assembleDebug

# Or build release
./gradlew assembleRelease
```

### First Launch

1. Tap ⚙️ settings icon
2. Grant Samsung Health permissions
3. Start tracking! 🎉

---

## 🔐 Permissions

| Permission | Purpose |
|------------|---------|
| `READ_STEPS` | View step count |
| `READ_HEART_RATE` | Monitor heart rate |
| `READ_SLEEP` | Track sleep sessions |
| `READ_NUTRITION` | View meals |
| `WRITE_NUTRITION` | Log meals |

---

## 🎯 Key Improvements Over Original

| Aspect | Original | This Version |
|--------|----------|--------------|
| **UI Framework** | XML Views | 100% Jetpack Compose |
| **Design** | Material 2 | Liquid Glass + Neo-Neon |
| **Architecture** | Direct SDK calls | Clean Architecture (MVVM + UseCase) |
| **DI** | Manual Factory | Hilt |
| **State** | LiveData | StateFlow |
| **Theme** | Static | Persistent Dark/Light with DataStore |
| **Navigation** | Fragment-based | Compose Navigation |

---

## 📱 Screens Preview

| Screen | Features |
|--------|----------|
| **Home** | Glass cards for each health metric |
| **Steps** | Day/Week/Month views with HorizontalPager |
| **Heart Rate** | Daily breakdown by time periods |
| **Nutrition** | Meal tracking with CRUD operations |

---

## 📄 License

Copyright © 2024 Samsung Electronics Co., Ltd.

---

## 🙏 Attribution

- **Original Sample**: [Samsung Health Diary](https://developer.samsung.com/health/data/sample/health-diary.html)
- **Redesigned with**: Gemini 3 Pro & Antigravity
- **Design Inspiration**: Liquid Glass + Neo-Neon aesthetic

---

<div align="center">

**Built with ❤️ using Jetpack Compose**

</div>
