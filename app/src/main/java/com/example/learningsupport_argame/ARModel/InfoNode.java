package com.example.learningsupport_argame.ARModel;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class InfoNode extends Node implements Node.OnTapListener {
    private String TAG = "InfoNode";
    private Node mInfoCard;
    private Context mContext;
    private Task mTask;

    public InfoNode(Context context, Task task) {
        mContext = context;
        mTask = task;
        Log.d(TAG, "InfoNode: ");
    }

    @Override
    public void onActivate() {
        super.onActivate();
        if (mInfoCard == null) {
            Log.d(TAG, "onActivate: ");

            mInfoCard = new Node();
            mInfoCard.setParent(this);
            mInfoCard.setEnabled(false);
            ViewRenderable.builder()
                    .setView(mContext, R.layout.ar_activity_model_interactive_view)
                    .build()
                    .thenAccept(
                            (renderable) -> {
                                initTaskInfoView(renderable.getView());
                                mInfoCard.setRenderable(renderable);
                                // 斜向上45度
                                // mInfoCard.setLocalRotation(Quaternion.eulerAngles(new Vector3(-45, 0, 0)));
                                mInfoCard.setLocalScale(new Vector3(0.5f, 0.7f, 0.5f));
                                mInfoCard.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));
                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load plane card view.", throwable);
                            });
        }
    }

    private void initTaskInfoView(View view) {
        Log.d(TAG, "initTaskInfoView: ");
        TextView name = view.findViewById(R.id.task_name);
        name.setText(mTask.getTaskName());
        TextView type = view.findViewById(R.id.task_type);
        type.setText(mTask.getTaskType());
        TextView time = view.findViewById(R.id.task_time);
        time.setText(mTask.getTaskStartAt());
        String[] locationArray = mTask.getAccomplishTaskLocation().split(",");
        TextView location = view.findViewById(R.id.task_location);
        location.setText(locationArray[0]);
        TextView desc = view.findViewById(R.id.task_desc);
        desc.setText(mTask.getTaskContent());
        Button acceptButton = view.findViewById(R.id.ar_task_accept_button);
        acceptButton.setOnClickListener(v -> {
            new Thread(() ->
            {
                Task task = TaskLab.getParticipantTask(mTask.getTaskId(), mTask.getUserId());
                if (task == null) {
                    Looper.prepare();
                    Toast.makeText(mContext, "您已经接受过此任务，无法重复接取", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(mContext, "任务接受成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    TaskLab.acceptTask(mTask);
                }
            }).start();

            mInfoCard.setEnabled(false);
        });

    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        mInfoCard.setEnabled(!mInfoCard.isEnabled());
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);
        if (getScene() == null) {
            return;
        }

        Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
        Vector3 cardPosition = mInfoCard.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        mInfoCard.setWorldRotation(lookRotation);
    }
}
