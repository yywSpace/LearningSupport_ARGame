# 数据库

- User
    - user_id
    - user_name
    - user_sex
    - user_birthday
    - user_city
    - user_exp
    - user_credits
    - user_Items_id  
        链接至道具表，表示用户所拥有的物品

- Friend
    - friend_id
    - friend_current_user  
        外键，代表当前用户
    - friend  
        链接至user表, 表示此user为当前用户的朋友

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


- Task
   - task_id
   - task_user_id  
        外键，代表此任务的所有者
   - task_name  
        任务名
    - task_content  
        任务内容
    - task_type  
        任务类型：自身任务，好友间任务，社团任务，一般任务
    - task_status  
        任务状态：执行中，普通
    - task_notification  
        任务是否提醒
    - task_participant  
        任务参与人员
    - task_start_at  
        任务开始时间
    - task_end_in  
        任务结束时间
        
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

