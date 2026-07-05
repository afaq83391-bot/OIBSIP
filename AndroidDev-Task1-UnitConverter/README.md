
 📱 Unit Converter Application

An intuitive and visually appealing Android application developed as part of the **Oasis Infobyte Summer Internship (OIBSIP)**. This app allows users to seamlessly convert values across six different measurement categories.



## 🌟 Features

- **6 Measurement Categories:** Length, Weight, Temperature, Volume, Area, and Speed.
- **Dynamic Dropdowns:** Source and target unit spinners update automatically based on the selected category.
- **Swap Functionality:** Instantly reverse the source and target units with a single tap.
- **Input Validation:** Prevents empty or invalid numeric inputs with clear error messages.
- **Formula Display:** Shows the mathematical conversion formula used for the current calculation.
- **Copy to Clipboard:** Easily copy the converted result with one click.
- **Material Design UI:** Clean, modern interface with cards, chips, and smooth animations.


## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java |
| **IDE** | Android Studio |
| **UI Layout** | XML (ConstraintLayout, ScrollView) |
| **UI Components** | Material Design Components (Chips, TextFields, Buttons) |
| **Build System** | Gradle (Groovy DSL) |
| **Min SDK** | API 24 (Android 7.0) |


## 📸 Screenshots

| Home Screen and  conversion result|
| :---: |
|<img width="500" height="900" alt="app overview1" src="<img width="500" height="900" alt="app overview1" src="https://github.com/user-attachments/assets/a278fcad-1bc4-4a47-9362-4c386cbb4ded" />
" />|
<img width="500" height="900" alt="app overview2" src="<img width="500" height="900" alt="app overview2" src="https://github.com/user-attachments/assets/720581c9-2a51-48c0-a597-62d2d74be902" />
" /> |



## 🏗️ Project Architecture

The project follows a simple and clean architecture separating UI and Business Logic:

- `MainActivity.java`: Handles UI interactions, button clicks, and dropdown management.
- `ConverterHelper.java`: Contains the core conversion logic. Uses a "Base Unit" pattern (e.g., converting everything to Meters first, then to the target unit) for scalable calculations, with special handling for Temperature.


## 🚀 How to Run Locally

### Prerequisites
- **Android Studio** (Latest version recommended, Arctic Fox or newer)
- An **Android Emulator** or a **Physical Android Device** with USB Debugging enabled.

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/OIBSIP.git
   ```
2. **Navigate to the project folder:**
   ```bash
   cd OIBSIP/AndroidDev-Task1-UnitConverter
   ```
3. **Open in Android Studio:** 
   - Open Android Studio → Select "Open an existing project"
   - Navigate to the folder you just cloned and select it.
4. **Sync Gradle:** 
   - Let Android Studio download the required Gradle dependencies (might take a minute on first run).
5. **Run the App:**
   - Connect your device or start an emulator.
   - Click the green **▶ Run** button.

---

## 📄 License

This project is created for educational purposes as part of the OIBSIP internship program.

---
**Developed by:** [Afaq ahmad]  
**Track:** Android Development | Oasis Infobyte
```

### ⚠️ Important things to do before saving:
1. Replace `YOUR_USERNAME` in the clone link with your actual GitHub username.
2. Replace `[Your Full Name]` at the very bottom with your actual name.
3. Make sure you create a folder named `screenshots` inside your project folder and put 2 images named `home.png` and `result.png` in it (or change the names in the README to match your actual image files).
