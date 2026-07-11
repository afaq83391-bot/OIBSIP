package com.example.unitconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    // ── Views ───────────────────────────────────────────────────
    private ChipGroup chipGroupCategory;
    private TextInputLayout tilInput;
    private TextInputEditText etInputValue;
    private AutoCompleteTextView actvFromUnit;
    private AutoCompleteTextView actvToUnit;
    private MaterialButton btnConvert;
    private MaterialButton btnCopy;
    private ImageButton btnSwap;
    private TextView tvResult;
    private TextView tvFormula;
    private TextView tvError;

    // ── State ───────────────────────────────────────────────────
    private String currentCategory = ConverterHelper.LENGTH;
    private String lastResultText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupCategoryChips();
        setupUnitDropdowns();
        setupListeners();
    }

    // ── Initialization ──────────────────────────────────────────

    private void initViews() {
        chipGroupCategory = findViewById(R.id.chipGroupCategory);
        tilInput          = findViewById(R.id.tilInput);
        etInputValue      = findViewById(R.id.etInputValue);
        actvFromUnit      = findViewById(R.id.actvFromUnit);
        actvToUnit        = findViewById(R.id.actvToUnit);
        btnConvert        = findViewById(R.id.btnConvert);
        btnCopy           = findViewById(R.id.btnCopy);
        btnSwap           = findViewById(R.id.btnSwap);
        tvResult          = findViewById(R.id.tvResult);
        tvFormula         = findViewById(R.id.tvFormula);
        tvError           = findViewById(R.id.tvError);
    }

    private void setupCategoryChips() {
        chipGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                currentCategory = chip.getText().toString();
                resetResult();
                hideError();
                setupUnitDropdowns();

                // Animate the converter card
                findViewById(R.id.converterCard).startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            }
        });
    }

    private void setupUnitDropdowns() {
        String[] units = ConverterHelper.getUnits(currentCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, units);

        actvFromUnit.setAdapter(adapter);
        actvToUnit.setAdapter(adapter);

        // Default: first and second item
        if (units.length > 0) actvFromUnit.setText(units[0], false);
        if (units.length > 1) actvToUnit.setText(units[1], false);
    }

    private void setupListeners() {
        // Convert button
        btnConvert.setOnClickListener(v -> performConversion());

        // Swap button
        btnSwap.setOnClickListener(v -> {
            String from = actvFromUnit.getText().toString();
            String to   = actvToUnit.getText().toString();
            actvFromUnit.setText(to, false);
            actvToUnit.setText(from, false);
            v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

            // If there's already a result, re-convert
            if (!lastResultText.isEmpty()) {
                performConversion();
            }
        });

        // Copy button
        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Conversion Result", lastResultText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        });

        // Clear error when user types
        etInputValue.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError();
                tilInput.setError(null);
            }
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    // ── Conversion Logic ────────────────────────────────────────

    private void performConversion() {
        hideError();

        // 1. Validate input
        String inputStr = etInputValue.getText().toString().trim();
        if (inputStr.isEmpty()) {
            showError(getString(R.string.error_empty));
            tilInput.setError(getString(R.string.error_empty));
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputStr);
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid));
            tilInput.setError(getString(R.string.error_invalid));
            return;
        }

        // 2. Get units
        String fromUnit = actvFromUnit.getText().toString();
        String toUnit   = actvToUnit.getText().toString();

        if (fromUnit.isEmpty() || toUnit.isEmpty()) {
            showError("Please select both units");
            return;
        }

        // 3. Convert
        String result = ConverterHelper.convert(
                currentCategory, fromUnit, toUnit, inputValue);

        // 4. Build display text
        String shortFrom = extractShort(fromUnit);
        String shortTo   = extractShort(toUnit);
        lastResultText = result + " " + shortTo;

        tvResult.setText(lastResultText);
        tvResult.startAnimation(
                AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        // 5. Show formula
        String formula = ConverterHelper.getFormula(
                currentCategory, fromUnit, toUnit);
        tvFormula.setText(formula);
        tvFormula.setVisibility(View.VISIBLE);

        // 6. Show copy button
        btnCopy.setVisibility(View.VISIBLE);
    }

    // ── Helpers ─────────────────────────────────────────────────

    private void resetResult() {
        tvResult.setText(getString(R.string.result_placeholder));
        tvFormula.setVisibility(View.GONE);
        btnCopy.setVisibility(View.GONE);
        lastResultText = "";
        etInputValue.setText("");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        tvError.startAnimation(
                AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    /**
     * Extracts short name from e.g. "Kilometer (km)" → "km"
     * Falls back to full string if no parentheses.
     */
    private String extractShort(String full) {
        int start = full.indexOf('(');
        int end   = full.indexOf(')', start);
        if (start != -1 && end != -1) {
            return full.substring(start + 1, end);
        }
        return full;
    }
}