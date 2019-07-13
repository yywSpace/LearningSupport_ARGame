# 注意  

1. 项目gradle的版本取四人中最小为,其中
   - 插件为：com.android.tools.build:gradle:3.1.2
   - gradle版本为：gradle-4.4-all.zip
2. 项目中文件命名规范  
   - 类名  
      为所编写模块创建包，每个模块的类放置于相应包中，命名不做要求
   - 资源名  
      由于资源放于工程的相同地方(layout,drawable等), 有冲突的可能性，为便于管理与查找需要规范命名
      - 命名规则  
        资源名：模块名+其原来名字      
         > 例：  
           模块名或者包名: ARModel  
           资源名: activity_main.xml  
           应改为: armodel_activity_main.xml
         
# 资料  

[功能列表](./function.md)  
[数据库表定义](./database.md)  
[所用到的相关技术](./requiredTechnology.md)  
