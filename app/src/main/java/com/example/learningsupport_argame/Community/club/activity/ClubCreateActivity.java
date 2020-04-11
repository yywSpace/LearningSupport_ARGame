package com.example.learningsupport_argame.Community.club.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.io.ByteArrayOutputStream;

public class ClubCreateActivity extends Activity {
    private static final String TAG = "ClubCreateActivity";
    private static final int ALBUM_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;

    private FrameLayout mReturnLayout;
    private ImageView mClubCoverImage;
    private EditText mClubNameET;
    private EditText mClubDescET;
    private EditText mClubTypeET;
    private TextView mClubMemberNumTV;
    private TextView mClubCoverImageLabel;
    private FrameLayout mSubmitButton;
    private int mClubMaxMemberNum = 10;
    private Club mClub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_create_activity_layout);
        mClub = new Club();
        mReturnLayout = findViewById(R.id.club_create_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mClubCoverImage = findViewById(R.id.club_cover_page);
        mClubCoverImage.setOnClickListener(v -> getPicFromAlbm());
        mClubNameET = findViewById(R.id.club_name_edit_text);
        mClubCoverImageLabel = findViewById(R.id.club_cover_page_label);
        mClubDescET = findViewById(R.id.club_desc_edit_text);
        mClubTypeET = findViewById(R.id.club_type_edit_text);
        mClubMemberNumTV = findViewById(R.id.club_max_member);
        mClubMemberNumTV.setOnClickListener(v -> {
            View view = LayoutInflater.from(this).inflate(R.layout.club_create_max_member_number_picker, null, false);
            NumberPicker numberPicker = view.findViewById(R.id.club_max_member_picker);
            numberPicker.setMaxValue(20);
            numberPicker.setValue(10);
            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> mClubMaxMemberNum = newVal);
            new AlertDialog.Builder(this)
                    .setTitle("社团最大容量")
                    .setView(view)
                    .setPositiveButton("确认", (dialog, which) ->
                            mClubMemberNumTV.setText(mClubMaxMemberNum + "人"))
                    .setNegativeButton("取消", null)
                    .show();
        });
        mSubmitButton = findViewById(R.id.club_create_submit);
        mSubmitButton.setOnClickListener(v -> {
            int maxClubNum = UserLab.getCurrentUser().getLevel() + Club.BASE_CLUB_NUMBER;
            // 如果当前创建社团数量超出 当前等级+BASE_CLUB_NUMBER(3) 或者 >= 20 则无法创建社团
            if (ClubLab.sCreatedClubList.size() >= 20 ||
                    ClubLab.sCreatedClubList.size() >= maxClubNum) {
                Toast.makeText(this, "当前社团数量已达上限，请删除其他无用社团后在做尝试", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = mClubNameET.getText().toString();
            String type = mClubTypeET.getText().toString();
            String desc = mClubDescET.getText().toString();
            if (name.equals("") || type.equals("") || desc.equals("")) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            mClub.setClubName(name);
            mClub.setClubType(type);
            mClub.setClubDesc(desc);
            mClub.setManagerId(UserLab.getCurrentUser().getId());
            mClub.setClubMaxMember(mClubMaxMemberNum);
            Log.d(TAG, "onCreate: " + mClub.getClubName() + " " +
                    mClub.getClubType() + " " + mClub.getClubDesc() + " " +
                    mClub.getClubMaxMember());
            new AlertDialog.Builder(this)
                    .setTitle("提交")
                    .setMessage("确认要创建社团？")
                    .setPositiveButton("确认", (dialog, which) -> {
                        new Thread(() -> {
                            Club club = ClubLab.getClubByName(mClub.getClubName());
                            if (club != null) {
                                Looper.prepare();
                                Toast.makeText(this, "社团名称已存在", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                return;
                            }
                            ClubLab.sCreatedClubList.add(mClub);
                            runOnUiThread(this::finish);
                            ClubLab.insert(mClub);
                            ClubLab.attendClub(mClub);
                        }).start();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ALBUM_REQUEST_CODE:    //调用相册后返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    cropPhoto(uri);
                    //传入intent
                }
                break;
            case CROP_REQUEST_CODE:     //调用剪裁后返回
                if (intent == null)
                    return;
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    // 也可以进行一些保存、压缩等操作后上传
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    Bitmap compressedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), compressedImage);
                    drawable.setCornerRadius(30);
                    mClubCoverImage.setImageDrawable(drawable);
                    mClub.setCoverBitmap(compressedImage);
                    mClubCoverImageLabel.setText("封面设置完毕");
                }
                break;
        }
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }
}
