
# 🧠 QuizMaster - Android Quiz App

## 📋 Project Information
* **Track:** Android Development
* **Task:** Task 3
* **Tech Stack:** Android Studio, Java, XML
* **Topic:** Science & General Knowledge
* **Application ID:** com.example.myapplication

---

## ✅ Features Implemented
This project strictly follows the feature checklist provided for the task:

- [x] **Welcome Screen:** Clean UI with a Start button and app branding.
- [x] **Question Screen:** Displays the question text, 4 option buttons, and a dynamic question counter (e.g., "Question 3 of 12").
- [x] **Hardcoded Questions:** Contains 12 questions loaded from a local Java ArrayList.
- [x] **Immediate Feedback:** Upon selecting an option, the correct answer highlights **Green**, and if the user chose wrong, their selection highlights **Red**.
- [x] **Next Button:** Appears automatically after an answer is selected to advance to the next question.
- [x] **Score Tracking:** A live score counter at the top of the screen updates immediately after each correct answer (+10 points).
- [x] **Results Screen:** Displays the final score out of the maximum, the exact number of correct answers, incorrect answers, and a "Restart Quiz" button.
- [x] **Shuffled Order:** Questions are randomized using `Collections.shuffle()` every time the quiz starts or restarts, ensuring a unique experience.


## 📸 Screenshots

### 1. Welcome Screen
The landing page where the user initiates the quiz.<br/><br/>
<img width="700px" height="1400px" alt="Welcome Screen" src="https://github.com/user-attachments/assets/9511909a-fcbe-451a-8b80-a1e45bb57009" />


### 2. Question Screen (Active Feedback)
Demonstrating the immediate green/red highlighting when a user selects an incorrect answer.<br/><br/>
<img width="700px" height="1400px" alt="Question Screen" src="https://github.com/user-attachments/assets/7f4cb84c-ca2b-4ae0-b49a-02f7358b8679" />


### 3. Results Screen
The final scorecard displaying statistics and an option to restart.<br/><br/>
<img width="700px" height="1400px" alt="Result Screen" src="https://github.com/user-attachments/assets/9b9de359-9555-471f-95d6-44a0fce023bc" />


---

## 🏗️ Architecture & Logic

The application follows standard Android Activity-based architecture with data passing via Intents.

1. **Data Model (`Question.java`):** 
   A plain Java class (POJO) that holds the question text, four options (A, B, C, D), and an integer representing the correct answer (1-4).

2. **WelcomeActivity:** 
   Serves as the launcher activity. Handles initial UI animations and navigates to `QuestionActivity` on button click.

3. **QuestionActivity (Core Logic):** 
   - Generates and shuffles the `List<Question>`.
   - Dynamically updates the UI (`TextView`, `Button` texts, `ProgressBar`).
   - Uses a single `OnClickListener` assigned to all 4 option buttons to determine which was clicked.
   - Compares the selected option against the correct answer to change button backgrounds (using custom XML drawables).
   - Tracks `score`, `correctCount`, and `wrongCount`.
   - Passes the final data to `ResultActivity` using `Intent.putExtra()`.

4. **ResultActivity:** 
   - Retrieves the score data using `getIntent().getIntExtra()`.
   - Calculates the percentage to display a dynamic grade/message (🏆 Outstanding, ⭐ Great Job, etc.).
   - The "Restart Quiz" button uses `FLAG_ACTIVITY_CLEAR_TOP` to destroy the back stack and return to `WelcomeActivity` for a fresh start.

5. **UI/UX Components:**
   - **CardView:** Used to frame the question text elegantly.
   - **Custom Drawables:** XML shapes used for normal, correct (green), and wrong (red) button states.
   - **Animations:** Fade-in, slide-up, and scale-in animations applied to text and buttons for a smooth user experience.

---

## ▶️ How to Run Locally

1. **Prerequisites:** Install Android Studio (latest version recommended) and set up an Android Emulator or connect a physical device via USB Debugging.
2. **Clone the Repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/OIBSIP.git
   ```
3. **Open Project:**
   - Open Android Studio.
   - Select `File` -> `Open`.
   - Navigate to `OIBSIP/AndroidDev-Task3-QuizApp` and select it.
4. **Sync & Build:**
   - Let Gradle sync finish automatically.
   - Go to `Build` -> `Rebuild Project` to generate the `R.java` file.
5. **Run:**
   - Select your device/emulator from the top dropdown.
   - Click the green `Play` button (Shift + F10).

---

## 📂 Project Structure
```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/myapplication/
│   ├── Question.java              # Data Model
│   ├── WelcomeActivity.java       # Launcher UI
│   ├── QuestionActivity.java      # Quiz Logic & UI
│   └── ResultActivity.java        # Final Score UI
└── res/
    ├── layout/
    │   ├── activity_welcome.xml
    │   ├── activity_question.xml
    │   └── activity_result.xml
    ├── drawable/                  # Custom button backgrounds & gradients
    ├── anim/                      # Fade, slide, and scale animations
    └── values/                    # Colors, strings, and themes
```

---
**Developed as part of the OIBSIP Android Development Internship.**
```
Made by Afaqahmad


