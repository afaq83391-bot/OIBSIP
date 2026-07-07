package com.example.todoapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.todoapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

/**
 * DialogFragment for creating a new task.
 * Passes the entered title and notes back via the listener.
 */
public class AddTaskDialog extends DialogFragment {

    public interface OnTaskCreatedListener {
        void onTaskCreated(String title, String notes);
    }

    private OnTaskCreatedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskCreatedListener) {
            listener = (OnTaskCreatedListener) context;
        } else if (getParentFragment() instanceof OnTaskCreatedListener) {
            listener = (OnTaskCreatedListener) getParentFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setDimAmount(0.5f);

        // Make dialog wider on larger screens
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputLayout tilTitle = dialog.findViewById(R.id.til_task_title);
        EditText etTitle = dialog.findViewById(R.id.et_task_title);
        EditText etNotes = dialog.findViewById(R.id.et_task_notes);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        MaterialButton btnSave = dialog.findViewById(R.id.btn_dialog_save);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (title.isEmpty()) {
                tilTitle.setError(getString(R.string.err_empty_task));
                etTitle.requestFocus();
                return;
            }
            tilTitle.setError(null);

            if (listener != null) {
                listener.onTaskCreated(title, notes);
            }
            dismiss();
        });

        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}