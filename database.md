# 数据库

- user
    - user_id
    - user_account
    - user_name
    - user_avatar
    - user_password
    - user_sex
    - user_birthday
    - user_city
    - user_hp
    - user_max_hp
    - user_level
    - user_exp
    - user_credits


- task
  - task_id
  - user_id  
      外键，代表此任务的所有者
  - task_name  
      任务名
  - task_content  
      任务内容
  - task_release_for  
      任务发布类型，及发布对象（自身，好友，社团，全部）
  - task_type  
      相对于任务本身(发布，接取)
  - task_status  
      任务状态（执行中，未开始，已完成）
  - task_notification  
      任务是否提醒
  - task_participant  
      任务参与人员
  - task_accomplish_location  
      完成任务所需要到达的地点（地点名,x,y）
  - task_start_at  
      任务开始时间
  - task_end_in  
      任务结束时间
  - task_create_time  
      不同类型的任务，代表不同的意义  
      发布的任务：任务创建的时间  
      接取的任务：任务完成的时间


- task_participant  
  此表链接用户及任务，记录某一任务当前的参与人物
  - task_participant_id
  - task_id  
  - participant_id  
    参与者的Id


---
以下数据数据库中还未涉及，只供参考

- Course
    - course_id
    - course_user_id  
        外键，代表此课程用户的id
    - course_name
    - course_teacher
    - course_location
    - course_start_at
    - course_end_in
    - course_notification


- Friend
    - friend_id
    - friend_current_user  
        外键，代表当前用户
    - friend  
        链接至user表, 表示此user为当前用户的朋友



- Reward
    - reward_id
    - reward_task_id  
        外键，代表此奖励所对应的任务
    - reward_exp  
        经验
    - reward_credits  
        积分

- Monitor
    - monitor_id
    - monitor_task_id  
        外键，代表此监督所代表的任务
    - monitior_level  
        监督等级:轻松, 严格
    - monitior_phone_total_time  
        手机使用总时间
    - monitior_task_screenOn_time  
        任务过程中手机亮屏时间
    - monitior_task_screenOff_time  
        任务过程中手机非亮屏时间
    - monitior_screenOn_attention_span  
        亮屏过程中专注时间
    - monitior_screenOn_inattention_span  
        亮屏过程中不专注时间
    - monitior_phone_use_count  
        手机使用次数

- Location
    - location_id
    - location_user_id  
        外键，代表当前用户
    - location_task_id  
        外键，代表当前任务
    - location_longitude  
        经度
    - location_latitude  
        纬度

- Items  
    道具表
