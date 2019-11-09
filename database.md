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
  - task_type  
      个人任务（个人任务直接加入任务参与列表），好友任务，社团任务，AR任务
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
  此表链接用户及任务，记录某一任务当前的参与人物，也可通过此表查此人接受了哪些任务
  - task_participant_id
  - task_id  
  - participant_id  
    参与者的Id


- Friend
    - id
    - user_id
        当前用户ID
    - friend_id  
        其朋友Id，链接至User表


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
