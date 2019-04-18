## 前提需要了解的
1. 在Android中运行go代码，需要用到一个工具`gomobile`，后面会说到安装方式。

2. Go开发环境，配置好GOPATH和GOROOT等。

3. AndroidSDK和AndroidNDK

## 准备好Go代码
首先要准备好GO的开发坏境，并且配置好GOPATH，咱们的项目叫`mobilego`
``` shell
mkdir code/mobilego
cd code/mobilego
echo export PATH=\$PATH:$(pwd) >> ~/.zshrc # .zshrc 为你的bash地址
source ~/.zshrc
```
其中`code/mobilego`是咱们go项目地址。那么现在就可以准备go代码了，那么我们可以按照这个样子新建一个项目。
```
mobilego
└── src
    └── mobile
        └── mobile.go
```
其中，src目录是必须要有的，因为gomobile需要从这个路径下查找包。其中`mobile.go`代码为：
``` go
package mobile

import "fmt"

func SayHello() {
      fmt.Println("Hello Mobile")
}

func SayHelloWithParams(name string) {
      fmt.Println("Hello", name)
}

func SayHelloWithParamsAndReturn(name string) string {
      return "Hello" + name
}

func SayHelloWithParamsAndReturnAndException(name string) (string, 
error) {
      return "Hello" + name, fmt.Errorf("some error")
}
```
这个`mobile.go`就是java和go文件通信的入口，其中gomobile会把这个文件的包名，编译成java对应符合java命名规范的类名（`Mobile`）。

`mobile.go`通过四个例子来演示java和go的通信。其中第一个无参数无返回值；第二个有参数无返回值；第三个有参数有返回值（同步返回）；第四个有参数有返回值并且抛出一个异常。由于java不支持多返回值，如果go使用多个返回值的话，会报出错误。

## 准备gomobile
gomobile是一个可以为go编译成android和ios平台使用的工具，他的使用说明在`https://github.com/golang/go/wiki/Mobile#tools`可以找到。

1. 首先下载这个工具
```
go get golang.org/x/mobile/cmd/gomobile
```
golang.org的代码基本上都托管在google服务器，一般来说在国内都不会下载成功的。那么只好换成另外一种下载方式。在这里，我们把` golang.org/x/mobile/cmd/gomobile`中的`golang.org/x/`换成`github.com/golang/`。如下所示：
```
go get github.com/golang/mobile/cmd/gomobile
```
这时候你会发现，这个包下载到了GOPATH/github.com下面了。我们要把它拷贝到golang.org目录下面
```
mv $GOPATH/src/github.com/golang/mobile $GOPATH/src/golang.org/x/mobile 
```
然后重新执行：
```
go get golang.org/x/mobile/cmd/gomobile
```
不出意外，gomobile已经安装完成了。执行`gomobile version`检查一下是否安装成功。

## 编译go代码
这一步要把go代码编译成Android平台使用的机器码。gomobile是一个非常好用的工具，通过一个命令不仅能把go代码编译成平台码，同时还会使用aar包来包装它，也就是说，我们完全不用写恶心的native代码了，直接调用gomobile生成的Java代码就好了。

首先来到go项目目录下面，执行gomobile编译命令。gomobile需要NDK，再次确定一下是否安装NDK。
```
gomobile bind -target=android mobile.go
```
`mobile.go`为入口文件。如果没什么问题的话，在项目目录下面就会多出两个文件。`mobile.aar`和`mobile-sources.jar`。其中`mobile.aar`就是我们编译完成的文件。

## Android调用Go
把上面的生成的`mobile.aar`拷贝到Android项目中的lib下面，同时修改`build.gradle`，在dependencies中加入或者修改一下代码，

**默认值：**
```
implementation fileTree(dir: 'libs', include: ['*.jar'])
```

**修改后：**
```
implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
```
Sync一下工程，通过Java测试一下我们的go代码。
```kotlin
internal class RunTask : AsyncTask<Void, Void, Any?>() {

    override fun doInBackground(vararg params: Void): Any? {
        Mobile.sayHello()
        Mobile.sayHelloWithParams("lecon")
        val result = Mobile.sayHelloWithParamsAndReturn("spawn")
        Log.d("AndroidGo",result)
        try {
            Mobile.sayHelloWithParamsAndReturnAndException("liucl")
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return null
    }
}
```
运行结果:
```
2019-04-18 13:31:04.566 7925-7982/? D/AndroidGo: Hellospawn
2019-04-18 13:31:04.571 7925-7982/? W/System.err:     at mobile.Mobile.sayHelloWithParamsAndReturnAndException(Native Method)
2019-04-18 13:31:04.590 7925-7987/? I/GoLog: Hello Mobile
2019-04-18 13:31:04.590 7925-7987/? I/GoLog: Hello lecon
```
这时候，你也许会发现，这几行代码的执行顺序是不确定的。因为java和go通信是跨进程调用，这几个方法有几个log是在go中输出，就不能保证执行顺序。

项目代码放到github上：https://github.com/leconio/AndroidCallGoDemo
