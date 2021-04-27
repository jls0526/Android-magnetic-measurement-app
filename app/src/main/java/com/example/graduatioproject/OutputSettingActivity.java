package com.example.graduatioproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OutputSettingActivity extends AppCompatActivity {
    CheckBox checkBoxDirX,checkBoxDirY,checkBoxDirZ,checkBoxDirAll;
    EditText editTextPixel,editTextName;
    Button btnOutputOK;
    boolean dirXClicked = false,dirYClicked = false,dirZClicked = false,dirAllClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_setting);
        checkBoxDirX = findViewById(R.id.cb_dir_x);
        checkBoxDirY = findViewById(R.id.cb_dir_y);
        checkBoxDirZ = findViewById(R.id.cb_dir_z);
        checkBoxDirAll = findViewById(R.id.cb_dir_all);
        editTextPixel = findViewById(R.id.et_pixel_setting);
        editTextName = findViewById(R.id.et_output_name);
        btnOutputOK = findViewById(R.id.btn_output_ok);


        editTextName.setText(Constant.LIST_NAME);

        checkBoxDirX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dirXClicked = true;
                }else{
                    dirXClicked = false;
                }
            }
        });

        checkBoxDirY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dirYClicked = true;
                }else{
                    dirYClicked = false;
                }
            }
        });

        checkBoxDirZ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dirZClicked = true;
                }else{
                    dirZClicked = false;
                }
            }
        });

        checkBoxDirAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dirAllClicked = true;
                }else{
                    dirAllClicked = false;
                }
            }
        });
        btnOutputOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String pixelString = editTextPixel.getText().toString();
                if(dirXClicked){
                    Constant.MAGNETIC_DIR = 0;
                    if(pixelString.length() > 0){
                        int pixel = Integer.parseInt(pixelString);
                        if(pixel > 0){
                            DrawMagnetic.outputMagneticPicture(getApplicationContext(),pixel,name + "-x轴磁场强度",4);
                        }
                    }
                }

                if(dirYClicked){
                    Constant.MAGNETIC_DIR = 1;
                    if(pixelString.length() > 0){
                        int pixel = Integer.parseInt(pixelString);
                        if(pixel > 0){
                            DrawMagnetic.outputMagneticPicture(getApplicationContext(),pixel,name + "-y轴磁场强度",4);
                        }
                    }

                }

                if(dirZClicked){
                    Constant.MAGNETIC_DIR = 2;
                    if(pixelString.length() > 0){
                        int pixel = Integer.parseInt(pixelString);
                        if(pixel > 0){
                            DrawMagnetic.outputMagneticPicture(getApplicationContext(),pixel,name + "-z轴磁场强度",4);
                        }
                    }
                }

                if(dirAllClicked){
                    Constant.MAGNETIC_DIR = 3;
                    if(pixelString.length() > 0){
                        int pixel = Integer.parseInt(pixelString);
                        if(pixel > 0){
                            DrawMagnetic.outputMagneticPicture(getApplicationContext(),pixel,name + "-总轴磁场强度",4);
                        }
                    }
                }
            }
        });



    }
}
