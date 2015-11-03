package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class InputDialog extends Dialog implements View.OnClickListener {

    private Activity parent;
    private TextInputLayout textInputLayout;

    public InputDialog(Activity context) {
        super(context);
        parent = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        ImageView submitBtn = (ImageView) findViewById(R.id.textSubmit);
        submitBtn.setOnClickListener(this);

        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textInput);
        this.textInputLayout = textInputLayout;
        textInputLayout.setHint("Please room name");


        EditText editText = textInputLayout.getEditText();
        assert editText != null;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEmailValid(s.toString())) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError("Only a-z, A-Z and 0-9 can be used.");
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (textInputLayout.isErrorEnabled()) return;
        dismiss();
        Intent intent = new Intent(parent, QuestionActivity.class);
        intent.putExtra(QuestionActivity.ROOM_NAME, textInputLayout.getEditText().getText().toString());
        parent.startActivity(intent);
    }

    public static boolean isEmailValid(String room_name) {
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}