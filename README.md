# MyAndroid

Sample Library settings

https://github.com/chenFlyingkite/QQ

1. **引用 library 使用的 project : 參考 [jitpack][1]**
* 在 build.gradle 加上
```gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io" 
        }
    }
}
```

* 在 yourLib/build.gradle 加上
```gradle
dependencies { 
    ...
    //compile 'com.github.User:Repo:Tag'
    compile 'com.github.chenFlyingkite:MyAndroid:0.0.2'
}
```
[1]: https://jitpack.io/
