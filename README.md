  ## Maven库项目脚手架

## 打包项目

1. `gradle.properties`中配置下面参数

   ```properties
   # 生成文件位置
   mavenPath=D:/maven/
   #库名
   NAME=gson
   #版本
   VERSION=1.0
   #包组
   GROUDID=com.fojrking.gson
   #尽量和名字一样
   ARTIFACTID=gson
   # aar 或者 jar
   PACKAGING=aar
   #库的描述
   DESCRIPTION=android base gson
   ```

2. 新建`module`后在`build.gradle`中最下面添加代码

   ```groovy
   apply plugin: 'maven'
   uploadArchives {
       repositories.mavenDeployer {
           //maven 本地文件地址
           def mavenLib = file(getProperty('mavenPath'))
           repository(url: "file://${mavenLib.absolutePath}")
           pom.project {
               name NAME
               version VERSION
               groupId GROUDID
               artifactId ARTIFACTID
               packaging PACKAGING
               description DESCRIPTION
           }
       }
   }
   ```


3. `gradlew uploadArchives` 找到对应文件夹然后 git commit 到自己搭建的仓库

## 引用远程Maven

1. 项目根目录下`build.gradle`添加自己仓库的*maven地址*

   ```groovy
   allprojects {
       repositories {
         	...
           maven { url 'https://raw.github.com/forJrking/maven/master/' }
       }
   }
   ```

2. 在使用的项目下面引用

   ```
   dependencies {
       implementation fileTree(dir: 'libs', include: ['*.jar'])
       implementation 'com.fojrking.mmap:mapped:1.0.0'
       //注意和你配置的 
       implementation 'com.fojrking.gson:gson:1.0.0'
   }
   ```

   