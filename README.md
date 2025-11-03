# 🐾 Petzy – Connect With Pet Lovers Around You

Petzy is a modern *social networking app for pet owners* 🐶🐱🐦 that allows users to share pet photos, write captions, connect with other pet lovers, and explore a community of adorable pets — all powered by *Firebase* and *Jetpack Compose* ❤

## ✨ Features

### 🧩 Authentication
- Secure *Signup / Login* using Firebase Authentication  
- Stores user data in Firestore with unique profiles  
- Persists session with *DataStore*

### 🏠 Home Feed
- Browse all posts (images, captions, hashtags, pet types)  
- Real-time updates using Firestore  
- Like ❤ or Comment 💬 on posts dynamically  
- Filter posts by pet type (Dog, Cat, Bird, etc.)

### 📸 Upload Screen
- Select and upload pet image from gallery  
- Choose *Pet Type (mandatory)*  
- Add caption and hashtags  
- Posts automatically saved to Firestore and Storage  

### 💬 Comments
- Open an *Instagram-style comment bottom sheet*  
- View all comments for a post  
- Add new comments with username and timestamp  

### 🔐 Profile
- View user profile info (name, email, profile picture)  
- Edit profile or bio  
- Track number of pets uploaded  

## 🏗 Tech Stack

| Layer | Technology |
|-------|-------------|
| *UI* | Jetpack Compose, Material3 |
| *Architecture* | MVVM + Repository pattern |
| *DI (Dependency Injection)* | Hilt |
| *Auth & Data Storage* | Firebase Authentication, Firestore, Firebase Storage |
| *Local Storage* | DataStore |
| *Image Loading* | Coil |
| *Navigation* | Jetpack Navigation Compose |


## HERE IS THE VIDEO OF WORKING APPLICATION
https://drive.google.com/file/d/1HDiMqiIW9B6tH_JMxsPny7Mjl_yaBFtr/view?usp=sharing


## ⚙ Project Structure

com.nativeknights.petzy/
│
├── auth/ → Login & Signup Screens
├── home/ → Home feed, PostCard, CommentSheet
├── upload/ → Upload post screen
├── profile/ → User profile
├── navigation/ → NavGraph, Routes
│
├── viewmodel/ → AuthViewModel, HomeViewModel, UploadViewModel
├── di/ → Hilt modules for repositories
├── data/ → Repository implementations
└── domain/
├── model/ → Post, User, Comment, PetType
└── model/repository/ → Repository interfaces

## 🔥 Firebase Setup

Make sure you’ve configured Firebase in your Android project.

1. Create a new Firebase project.
2. Enable:
   - *Authentication (Email/Password)*
   - *Firestore Database*
   - *Firebase Storage*
3. Download google-services.json and place it inside  
   app/ folder.
4. Add the following security rules:

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /posts/{postId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.userId;

      match /comments/{commentId} {
        allow read: if true;
        allow create: if request.auth != null;
        allow delete: if request.auth != null && request.auth.uid == resource.data.userId;
      }

      match /likes/{likeId} {
        allow read: if true;
        allow write: if request.auth != null;
      }
    }

    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}



🚀 How to Run
1.Clone the repository
2.git clone https://github.com/your-username/Petzy.git
cd Petzy
3.Open in Android Studio (Arctic Fox or newer)
4.Add your google-services.json inside the app/ directory
5.Build and run the project on an emulator or real device

🧠 Architecture Overview
1.MVVM (Model–View–ViewModel) pattern for clean separation
2.Repository Layer abstracts Firebase operations
3.Hilt DI injects ViewModels and Repositories
4.LiveData + StateFlow manage real-time UI updates
5.Composable UI ensures modern, reactive, and declarative interface

🧩 Upcoming Features
1.Follow/Unfollow users
2.Realtime chat using Firebase Realtime Database
3.Push notifications (FCM)
4.Dark mode toggle
5.Pet adoption / marketplace integration

💡 Contribution
We welcome contributions!
If you'd like to improve Petzy:
1.Fork the repo
2.Create a new branch (feature/amazing-feature)
3.Commit changes
4.Open a Pull Request 🚀

🧑‍💻 Author
Hemant Vaishnav
📍 Android Developer | Kotlin | MVVM | Firebase | Jetpack Compose

🐾 License
MIT License
Copyright (c) 2025 Hemant

“Every pet deserves love, and every story deserves to be shared — welcome to Petzy 💖”
