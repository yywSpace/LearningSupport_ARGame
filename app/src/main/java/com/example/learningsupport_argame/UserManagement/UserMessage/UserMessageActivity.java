package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.ChangePasswordActivity;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.address.AddressManager;
import com.example.learningsupport_argame.UserManagement.address.City;
import com.example.learningsupport_argame.UserManagement.address.Province;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class UserMessageActivity extends Activity {
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int ALBUM_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;
    private RecyclerView mMessageRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private File mTempFile;
    private List<MessageItem> mMessageItems;
    private FrameLayout mReturnButton;

    //地址选择s
    private List<Province> mProvinceList;
    private NumberPicker mProvincePicker, mCityPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_activity_message);
        mReturnButton = findViewById(R.id.user_management_message_return);
        mReturnButton.setOnClickListener((view) -> finish());
        mProvinceList = AddressManager.getProvince(this);
        mMessageRecyclerView = findViewById(R.id.user_management_message_recycle_view);
        mMessageItems = new ArrayList<>(
                Arrays.asList(
                        new MessageItem("头像", BitmapFactory.decodeResource(getResources(), R.drawable.user_management_avatar), ItemType.AVATAR_ITEM),
                        new MessageItem("等级", "等级", ItemType.NORMAL_ITEM),
                        new MessageItem("血量", "血量", ItemType.BLOOD_ITEM),
                        new MessageItem("经验", "经验", ItemType.NORMAL_ITEM),
                        new MessageItem("用户名", "用户名", ItemType.NORMAL_ITEM),
                        new MessageItem("标签", "标签", ItemType.NORMAL_ITEM),
                        new MessageItem("性别", "性别", ItemType.NORMAL_ITEM),
                        new MessageItem("生日", "生日", ItemType.NORMAL_ITEM),
                        new MessageItem("城市", "城市", ItemType.NORMAL_ITEM),
                        new MessageItem("发布的任务", R.drawable.user_management_next, ItemType.IMAGE_ITEM),
                        new MessageItem("接受的任务", R.drawable.user_management_next, ItemType.IMAGE_ITEM),
                        new MessageItem("完成的任务", R.drawable.user_management_next, ItemType.IMAGE_ITEM),
                        new MessageItem("更多信息", R.drawable.user_management_next, ItemType.IMAGE_ITEM),
                        new MessageItem("修改密码", R.drawable.user_management_next, ItemType.IMAGE_ITEM),
                        new MessageItem("登出", R.drawable.user_management_next, ItemType.LOGOUT_ITEM)

                ));
        mAdapter = new MessageItemAdapter(mMessageItems);
        mMessageRecyclerView.addItemDecoration(new MyItemDecoration(mMessageItems));
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(mAdapter);

        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            runOnUiThread(() -> {
                updateAdapter(UserLab.getCurrentUser());
                mAdapter.notifyDataSetChanged();
            });
        }).start();

        ActivityUtil.addActivity(this);

    }


    class MessageItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<MessageItem> mMessageItemList;

        public MessageItemAdapter(List<MessageItem> messageItemList) {
            mMessageItemList = messageItemList;
        }

        @Override
        public int getItemViewType(int position) {
            return mMessageItemList.get(position).getItemType().ordinal();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ItemType.AVATAR_ITEM.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_message_avatar_item, parent, false);
                return new AvatarItemViewHolder(view);
            }
            if (viewType == ItemType.NORMAL_ITEM.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_message_item, parent, false);
                return new NormalItemViewHolder(view);
            }
            if (viewType == ItemType.IMAGE_ITEM.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_message_image_item, parent, false);
                return new ImageItemViewHolder(view);
            }
            if (viewType == ItemType.LOGOUT_ITEM.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_message_logout_item, parent, false);
                return new LogoutItemViewHolder(view);
            }
            if (viewType == ItemType.BLOOD_ITEM.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_message_blood_item, parent, false);
                try {
                    return new BloodItemViewHolder(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AvatarItemViewHolder) {
                AvatarItemViewHolder itemViewHolder = (AvatarItemViewHolder) holder;
                MessageItem data = mMessageItemList.get(position);
                itemViewHolder.label.setText(data.getItemLabel());
                itemViewHolder.avatar.setImageBitmap(data.getItemImageBitmap());
            }

            if (holder instanceof NormalItemViewHolder) {
                NormalItemViewHolder itemViewHolder = (NormalItemViewHolder) holder;
                MessageItem data = mMessageItemList.get(position);
                itemViewHolder.label.setText(data.getItemLabel());
                itemViewHolder.content.setText(data.getItemContent());
            }

            if (holder instanceof ImageItemViewHolder) {
                ImageItemViewHolder itemViewHolder = (ImageItemViewHolder) holder;
                MessageItem data = mMessageItemList.get(position);
                itemViewHolder.label.setText(data.getItemLabel());
                itemViewHolder.image.setBackgroundResource(data.getItemImageId());
            }
            holder.itemView.setOnClickListener(v ->
            {
                switch (position) {
                    case 0:
                        // 头像
                        changeAvatar();
                        break;
                    case 4:
                        // 用户名
                        changeNameDialog();
                        break;
                    case 5:
                        // label
                        changeLabelDialog();
                        break;
                    case 6:
                        // 性别
                        changeSexDialog();
                        break;
                    case 7:
                        // 生日
                        showDatePickDialog();
                        break;
                    case 8:
                        // 城市
                        changeAddressDialog();
                        break;
                    case 9:
                        // 发布的任务
                        startActivity(new Intent(UserMessageActivity.this, UserReleasedTaskActivity.class));
                        break;
                    case 10:
                        // 接受的任务
                        startActivity(new Intent(UserMessageActivity.this, UserAcceptedTaskActivity.class));
                        break;
                    case 11:
                        // 完成的任务
                        startActivity(new Intent(UserMessageActivity.this, UserAccomplishTaskActivity.class));
                        break;
                    case 12:
                        startActivity(new Intent(UserMessageActivity.this, FeedbackDetailsActivity.class));
                        // 更多信息
                        break;
                    case 13:
                        // 修改密码
                        startActivity(new Intent(UserMessageActivity.this, ChangePasswordActivity.class));
                        break;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMessageItemList.size();
        }


        public class LogoutItemViewHolder extends RecyclerView.ViewHolder {
            private Button mLogoutButton;

            public LogoutItemViewHolder(@NonNull View itemView) {
                super(itemView);
                mLogoutButton = itemView.findViewById(R.id.user_management_message_logout);

                mLogoutButton.setOnClickListener((view) -> {
                    SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = userInfo.edit();//获取Editor
                    editor.clear();
                    editor.commit();
                    ActivityUtil.destroyAll();
                });
            }
        }

        public class NormalItemViewHolder extends RecyclerView.ViewHolder {

            public TextView label;
            public TextView content;

            public NormalItemViewHolder(@NonNull View itemView) {
                super(itemView);
                label = itemView.findViewById(R.id.user_management_message_label);
                content = itemView.findViewById(R.id.user_management_message_content);
            }
        }

        public class AvatarItemViewHolder extends RecyclerView.ViewHolder {
            public TextView label;
            public ImageView avatar;

            public AvatarItemViewHolder(@NonNull View itemView) {
                super(itemView);
                label = itemView.findViewById(R.id.user_management_message_label);
                avatar = itemView.findViewById(R.id.user_management_message_image);
            }
        }

        public class ImageItemViewHolder extends RecyclerView.ViewHolder {
            public TextView label;
            public ImageView image;

            public ImageItemViewHolder(@NonNull View itemView) {
                super(itemView);
                label = itemView.findViewById(R.id.user_management_message_label);
                image = itemView.findViewById(R.id.user_management_message_image);
            }
        }

        public class BloodItemViewHolder extends RecyclerView.ViewHolder {
            public TextView label;
            public TextView content;
            public TextView bloodResetTime;

            public BloodItemViewHolder(@NonNull View itemView) throws ParseException {
                super(itemView);
                label = itemView.findViewById(R.id.user_management_message_label);
                bloodResetTime = itemView.findViewById(R.id.user_management_blood_reset_time);
                content = itemView.findViewById(R.id.user_management_message_content);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date nowDate = new Date();
                User user = UserLab.getCurrentUser();
                Date lastLogin = df.parse(user.getLastLoginTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastLogin);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date resetTime = calendar.getTime();
                new Timer().schedule(new TimerTask() {
                    long resetDuration = (resetTime.getTime() - nowDate.getTime()) / 1000;

                    @Override
                    public void run() {
                        resetDuration--;
                        long hour = (resetDuration / 60 / 60) % 60;
                        long minute = (resetDuration / 60) % 60;
                        long second = resetDuration % 60;
                        bloodResetTime.post(() -> bloodResetTime.setText(String.format("%02d:%02d:%02d", hour, minute, second)));
                        if (resetDuration <= 0) {
                            new Thread(() -> {
                                user.setHp(User.BASIC_HP + user.getLevel());
                                user.setLastLoginTime(df.format(nowDate));
                                UserLab.updateUser(user);
                            }).start();
                            cancel();
                        }
                    }
                }, 0, 1000);
            }
        }
    }

    public enum ItemType {
        IMAGE_ITEM,
        NORMAL_ITEM,
        AVATAR_ITEM,
        LOGOUT_ITEM,
        BLOOD_ITEM
    }

    static class MyItemDecoration extends RecyclerView.ItemDecoration {

        List<MessageItem> mMessageItemList;

        MyItemDecoration(List<MessageItem> messageItemList) {
            mMessageItemList = messageItemList;
        }

        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // 设定所有Item底部边距为1px
            outRect.set(0, 0, 0, 1);
            // 设置特定Item底边距
            int position = parent.getChildAdapterPosition(view);
            MessageItem itemData = mMessageItemList.get(position);
            if (itemData.getItemLabel().equals("头像") ||
                    itemData.getItemLabel().equals("城市") ||
                    itemData.getItemLabel().equals("经验") ||
                    itemData.getItemLabel().equals("完成任务") ||
                    itemData.getItemLabel().equals("更多信息")) {
                outRect.bottom = 10;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        new Thread(() -> {
            UserLab.updateUser(UserLab.getCurrentUser());
        }).start();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:   //调用相机后返回
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(UserMessageActivity.this, "com.example.learningsupport_argame", mTempFile);
                        cropPhoto(contentUri);
                    } else {
                        cropPhoto(Uri.fromFile(mTempFile));
                    }
                }
                break;
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
                    //设置到ImageView上
                    Optional<MessageItem> userNameItem = mMessageItems
                            .stream()
                            .filter((item) -> item.getItemLabel().equals("头像"))
                            .findFirst();
                    userNameItem.get().setItemImageBitmap(image);
                    mAdapter.notifyDataSetChanged();
                    // 也可以进行一些保存、压缩等操作后上传
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    Bitmap compressedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    UserLab.getCurrentUser().setAvatar(compressedImage);

                    // saveImage("crop", image);
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

    /**
     * 从相机获取图片
     */
    private void getPicFromCamera() {
        //用于保存调用相机拍照后所生成的文件
        mTempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(UserMessageActivity.this, "com.example.learningsupport_argame", mTempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showDatePickDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String birthday = year + "-" + monthOfYear + "-" + dayOfMonth;
                UserLab.getCurrentUser().setBirthday(birthday);
                Optional<MessageItem> userNameItem = mMessageItems
                        .stream()
                        .filter((item) -> item.getItemLabel().equals("生日"))
                        .findFirst();
                userNameItem.get().setItemContent(birthday);
                mAdapter.notifyDataSetChanged();

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void changeNameDialog() {
        View view = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.user_management_message_dialog_edit_text, null, false);
        EditText getNameEditText = view.findViewById(R.id.user_message_dialog_edit_text);
        getNameEditText.setText(UserLab.getCurrentUser().getName());
        new AlertDialog.Builder(this)
                .setTitle("请输入姓名")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = getNameEditText.getText().toString();
                    if (name.equals("")) {
                        Toast.makeText(getApplicationContext(), "名字不能为空！" + name, Toast.LENGTH_LONG).show();
                    } else {
                        UserLab.getCurrentUser().setName(name);
                        Optional<MessageItem> userNameItem = mMessageItems
                                .stream()
                                .filter((item) -> item.getItemLabel().equals("用户名"))
                                .findFirst();
                        userNameItem.get().setItemContent(name);
                        mAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();

    }

    private void changeLabelDialog() {
        View view = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.user_management_message_dialog_edit_text, null, false);
        EditText getNameEditText = view.findViewById(R.id.user_message_dialog_edit_text);
        getNameEditText.setText(UserLab.getCurrentUser().getName());
        new AlertDialog.Builder(this)
                .setTitle("请输入标签")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String label = getNameEditText.getText().toString();
                    UserLab.getCurrentUser().setLabel(label);
                    Optional<MessageItem> userNameItem = mMessageItems
                            .stream()
                            .filter((item) -> item.getItemLabel().equals("标签"))
                            .findFirst();
                    userNameItem.get().setItemContent(label);
                    mAdapter.notifyDataSetChanged();
                }).setNegativeButton("取消", null).show();
    }

    private void changeSexDialog() {
        final String[] sex = new String[]{"男", "女"};
        new AlertDialog.Builder(this)
                .setTitle("请选择性别")
                .setSingleChoiceItems(sex, "男".equals(UserLab.getCurrentUser().getSex()) ? 0 : 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserLab.getCurrentUser().setSex(sex[which]);
                        Optional<MessageItem> sexItem = mMessageItems
                                .stream()
                                .filter((item) -> item.getItemLabel().equals("性别"))
                                .findFirst();
                        sexItem.get().setItemContent(sex[which]);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    private void changeAvatar() {
        View contentView = LayoutInflater.from(UserMessageActivity.this).inflate(R.layout.user_management_select_camera, null);
        final PopupWindow selectCamera = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        selectCamera.setOutsideTouchable(true);
        selectCamera.setAnimationStyle(R.style.user_management_pop_window_anim_style);
        View root = LayoutInflater.from(UserMessageActivity.this).inflate(R.layout.user_management_activity_message, null);
        selectCamera.showAtLocation(root, Gravity.BOTTOM, 0, 0);
        TextView popCamera = contentView.findViewById(R.id.pop_camera);
        popCamera.setOnClickListener(v -> {
            getPicFromCamera();
            selectCamera.dismiss();
        });
        TextView popPic = contentView.findViewById(R.id.pop_pic);
        popPic.setOnClickListener(v -> {
            getPicFromAlbm();
            selectCamera.dismiss();
        });
        TextView popCancel = contentView.findViewById(R.id.pop_cancel);
        popCancel.setOnClickListener(v -> selectCamera.dismiss());
    }

    void changeAddressDialog() {
        View view = LayoutInflater.from(UserMessageActivity.this).inflate(R.layout.user_management_address_picker, null);
        initAddressView(view);
        initAddressPickerProvince();
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String province = mProvincePicker.getDisplayedValues()[mProvincePicker.getValue()];
                    String city = mCityPicker.getDisplayedValues()[mCityPicker.getValue()];
                    UserLab.getCurrentUser().setCity(province + " " + city);
                    Optional<MessageItem> userAddressItem = mMessageItems
                            .stream()
                            .filter((item) -> item.getItemLabel().equals("城市"))
                            .findFirst();
                    userAddressItem.get().setItemContent(province + " " + city);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(this, province + " " + city, Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .show();
    }


    void initAddressView(View view) {
        mProvincePicker = view.findViewById(R.id.province_picker);
        mCityPicker = view.findViewById(R.id.city_picker);
    }

    //club_list_item_setting“省份”选择器的数据
    private void initAddressPickerProvince() {
        List<String> provinceList = mProvinceList.stream().map(Province::getName).collect(Collectors.toList());
        String[] provinces = provinceList.toArray(new String[provinceList.size()]);
        mProvincePicker.setDisplayedValues(provinces);
        mProvincePicker.setMinValue(0); //设置第一个值
        mProvincePicker.setMaxValue(provinces.length - 1); //设置最后一个值
        mProvincePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            Toast.makeText(this, provinces[newVal], Toast.LENGTH_SHORT).show();
        });

        //默认的省份的位置
        int defaultProvince = provinces.length / 2 == 0 ? provinces.length / 2 : provinces.length / 2 + 1;
        mProvincePicker.setValue(defaultProvince); //设置当前值

        //根据当前默认的省份来设置对应的市
        showCityByProvince(provinces[mProvincePicker.getValue()]);

        //省份的选择事件
        mProvincePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            Log.i("tag", "newVal--->" + newVal);
            showCityByProvince(provinces[newVal]);
        });
    }

    //根据选择的省份名来显示城市名
    private void showCityByProvince(String province) {
        List<City> cityList = mProvinceList
                .stream()
                .filter((p) -> p.getName().equals(province))
                .findFirst()
                .get()
                .getCityList();
        List<String> cityNameList = cityList.stream().map(City::getName).collect(Collectors.toList());
        //设置城市的值
        final String[] cities = cityNameList.toArray(new String[cityList.size()]);
        if (cities.length <= 0) {
            mCityPicker.setDisplayedValues(null);
            mCityPicker.setMaxValue(0);
            return;
        }
        mCityPicker.setDisplayedValues(null); //清空之前的选择的数据
        mCityPicker.setMinValue(0); //设置第一个值
        mCityPicker.setMaxValue(cities.length - 1); //设置最后一个值
        mCityPicker.setDisplayedValues(cities);

    }

    private void updateAdapter(User user) {
        int maxHp = User.BASIC_HP + user.getLevel();
        int hp = user.getHp();
        int maxExp = User.BASIC_EXP + user.getLevel() * 500;
        int exp = user.getExp();
        int level = user.getLevel();
        mMessageItems.get(0).setItemImageBitmap(user.getAvatar());
        mMessageItems.get(1).setItemContent("Lv." + level);
        mMessageItems.get(2).setItemContent(hp + "/" + maxHp);
        mMessageItems.get(3).setItemContent(exp + "/" + maxExp);
        mMessageItems.get(4).setItemContent(user.getName());
        mMessageItems.get(5).setItemContent(user.getLabel());
        mMessageItems.get(6).setItemContent(user.getSex());
        mMessageItems.get(7).setItemContent(user.getBirthday());
        mMessageItems.get(8).setItemContent(user.getCity());
    }
}
