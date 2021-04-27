package com.example.graduatioproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

public class MagneticDataAdapter extends ArrayAdapter<MagneticListInfo> {
    private int resourceID;
    private TextView tvIndex;
    private TextView tvTitle;
    private TextView tvTime;
    private DatabaseHelper databaseHelper = null;
    private SQLiteDatabase writableDatabase;
    List<MagneticListInfo> nameList;


    public MagneticDataAdapter(Context context, int magnetic_list_layout, List<MagneticListInfo> nameList) {
        super(context, magnetic_list_layout, nameList);
        resourceID = magnetic_list_layout;
        this.nameList = nameList;
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(getContext(),"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);
            writableDatabase = databaseHelper.getWritableDatabase();
        }

    }

    private MagneticListInfo magneticListInfo = null;
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        magneticListInfo = getItem(position);
//        Log.d(TAG, "getView: position:" + position);
        @SuppressLint("ViewHolder") final View view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);

        tvIndex = view.findViewById(R.id.tvIndex);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvTime = view.findViewById(R.id.tvTime);

        tvIndex.setText(String.valueOf(position + 1));
        if (magneticListInfo != null) {
            tvTitle.setText(magneticListInfo.getName());
            tvTime.setText(magneticListInfo.getTime());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magneticListInfo = getItem(position);
                if (magneticListInfo != null) {
                    Constant.CHOSEN_ID = magneticListInfo.getId();
                }
                chooseDIR(getContext(),view);


            }
        });
        //Constant.LIST_NAME

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                magneticListInfo = getItem(position);
                if (magneticListInfo != null) {
                    Constant.LIST_NAME = magneticListInfo.getName();
                }
                if (magneticListInfo != null) {
                    Constant.CHOSEN_ID = magneticListInfo.getId();
                }
                funcMenu(getContext(),view);
                return true;
            }
        });
        return view;
    }

    private void chooseDIR(final Context context, View view){
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.which_dir_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dir_x:
                        Constant.MAGNETIC_DIR = 0;
                        break;
                    case R.id.dir_y:
                        Constant.MAGNETIC_DIR = 1;
                        break;
                    case R.id.dir_z:
                        Constant.MAGNETIC_DIR = 2;
                        break;
                    case R.id.dir_total:
                        Constant.MAGNETIC_DIR = 3;
                        break;
                }

                Intent intent = new Intent(context,DrawContourActivity.class);
                context.startActivity(intent);
                return true;
            }
        });
    }

    private void funcMenu(final Context context, final View view){
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.func_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.func_rename:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("请输入名称");
                        View view1 = LayoutInflater.from(context).inflate(R.layout.alert_rename_layout,null);
                        builder.setView(view1);

                        final EditText etUserName = view1.findViewById(R.id.et_name);

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String userNeme = etUserName.getText().toString();
                                if(userNeme.length() > 0){
                                    magneticListInfo.setName(userNeme);
                                    tvTitle.setText(userNeme);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("label",userNeme);
                                    writableDatabase.update("NameList",contentValues,"name=?",new String[]{magneticListInfo.getId()});
                                }
                            }
                        });

                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.show();

                        break;
                    case R.id.func_delete:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("删除");
                        builder1.setMessage("确定删除此次测量数据？");
                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                writableDatabase.delete("NameList","name=?",new String[]{magneticListInfo.getId()});
                                writableDatabase.delete("MagneticData","time=?",new String[]{magneticListInfo.getId()});
                                Log.d(TAG, "onClick: magneticListInfo" + magneticListInfo.getId());
                                nameList.remove(magneticListInfo);
                                List<MagneticListInfo> temp = new ArrayList<>(nameList);
                                nameList.clear();
                                nameList.addAll(temp);
                                notifyDataSetChanged();
                            }
                        });

                        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder1.show();
                        break;
                    case R.id.func_output:
                        Intent intent = new Intent(getContext(),OutputSettingActivity.class);
                        getContext().startActivity(intent);
                        break;

                    case R.id.func_fix:
                        Constant.FIX_ID = magneticListInfo.getId();
                        Constant.FIX_CHOSEN.add(magneticListInfo);
                        Intent intentFix = new Intent(getContext(),FixActivity.class);
                        getContext().startActivity(intentFix);
                        break;
                }
                return true;
            }
        });
    }
}
