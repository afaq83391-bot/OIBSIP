
# Taskflow: Secure To-Do App with User Authentication

## 📌 Project Overview
**Taskflow** is a robust, secure, and user-friendly task management Android application developed as part of the OIBSIP internship program. It features a complete authentication system and local data persistence using SQLite, ensuring that user data remains private and secure on the device.

## 🛠️ Tech Stack
* **Language:** Java
* **UI Design:** XML (Material Design Components)
* **Database:** SQLite (`SQLiteOpenHelper`)
* **Security:** SHA-256 Password Hashing with Salt
* **IDE:** Android Studio
* **Architecture:** MVC Pattern (Model-View-Controller)

## ✨ Key Features
### 🔐 Authentication & Security
* **User Registration:** Sign up with name, email, and password.
* **Secure Login:** Email and password verification against the database.
* **Password Hashing:** Passwords are never stored as plain text. They are hashed using SHA-256 combined with a static salt before being saved to SQLite.
* **Session Management:** Uses `SharedPreferences` to maintain login sessions securely.
* **Logout:** Clears the session entirely, preventing unauthorized access upon returning to the app.

### 📝 Task Management
* **Create Tasks:** Add tasks with a title and optional notes via a custom dialog.
* **User-Specific Data:** SQLite database uses Foreign Keys to link tasks to specific user IDs. Users only see their own tasks.
* **Mark as Complete:** Toggle tasks to completed state, visually indicated by a strikethrough effect and color change.
* **Delete Tasks:** Permanently remove tasks via a delete button or swipe-to-delete gesture, with a safety confirmation dialog.

### 🎨 User Interface
* **Modern Dashboard:** Professional UI featuring a personalized greeting and dynamic statistics cards (All, Completed, Pending, Notes).
* **Color-Coded Tasks:** Tasks feature a left accent bar that changes color based on completion status (Indigo for pending, Green for completed).
* **Empty State Handling:** Displays a friendly, illustrated message when no tasks are present.
* **Responsive & Clean:** Utilizes `RecyclerView` for smooth scrolling and `CardView` for modern material design aesthetics.

## 📂 Project Structure
```text
app/src/main/java/com/example/todoapp/
├── adapters/               # RecyclerView adapter for the task list
├── database/               # SQLite database helper and table schemas
├── dialogs/                # Custom DialogFragment for adding tasks
├── models/                 # User and Task data models
├── utils/                  # SessionManager and PasswordHasher utilities
├── LoginActivity.java      # Login screen logic
├── RegisterActivity.java   # Registration screen logic
└── MainActivity.java       # Main task dashboard logic
```

## 🚀 How to Run the Project
1. **Clone the repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/OIBSIP.git
   ```
2. **Open in Android Studio:**
   * Launch Android Studio → Select **"Open an Existing Project"**.
   * Navigate to the cloned folder: `OIBSIP/Android-Task2-ToDoApp/TaskFlow` and open it.
3. **Sync Gradle:** Wait for Android Studio to finish syncing the Gradle files.
4. **Run the App:** Connect an Android phone (with USB Debugging enabled) or start an Android Emulator, then click the **Run** button (Shift + F10).

## 📸 Screenshots

| Registration Screen |
| :---: | 
|(<img width="700px" height="1400px" alt="Registration screen" src="https://github.com/user-attachments/assets/53b5ddda-dedd-453d-a437-adb6f88dc79d" />
) |

| Task Dashboard | Empty State |
| :---: | :---: |
| (<img width="700px" height="1400px" alt="Task list" src="https://github.com/user-attachments/assets/30a401e9-0433-4608-b8fc-cd662101733c" />
) |(<img width="700px" height="1400px" alt="Empty state" src="https://github.com/user-attachments/assets/8202b793-fb32-42fc-8f3c-4589045dbc7a" />
)|

## 🧑‍💻 Author
**Afaqahmad**  
Computer Science Student  
Oasis Infobyte  Android Development Intern
```
