
# 最後的克勞迪亞 cloudia Opencv JAVA + ADB 自動抽卡

## 簡介
最近失業沒事做, 剛好 cloudia 有無限首抽, 閒閒來寫一下自動抽卡, 順便統計機率
這是我測試用的, 所以要用的人至少要懂環境配置.

**先工商一下～我是Android APP工程師 歡迎找我外包 APP.**
> email : voarlese@gmail.com

這功能用到
* Opencv Java 
* ADB
* android模擬器 or 手機
* JAVA SDK

### 請勿拿此程式盈利

## 功能
1. 自動抽卡 - 可選擇想抽到哪些卡
用 ADB 控制手機 或模擬器, 用座標點擊位置, 目前是用1920x1080螢幕測試抽卡, 沒有測試不同解析度, 不過有做自適應.
2. 統計 (需要掛資料庫、不懂的就不用管他)
用mysql 做統計


#### 前置安裝
1. [JAVA](https://www.oracle.com/tw/java/technologies/javase/javase-jdk8-downloads.html)
2. [ADB](https://developer.android.com/studio/releases/platform-tools)
ADB 是用於控制手機, 所以必須設定環境變數讓 cmd 可以使用 adb 指令
3. [OPENCV](https://opencv.org/releases/) 
放到在autotest
需要把opencv 的 c/c++ lib檔案build出來
詳情可以看這篇, build opencv 只需要做到第二步, 把 opencv 的 dylib 弄出來就好了, run java 時候要引入

> 其他懶得寫步驟.... JAVA 、ADB 安裝好後要配置環境變數 請自己上網查囉~

## config 配置
##### path.json
重要的路徑參數

* screenShotDir
 抽卡後截圖比對存檔路徑.
* desire 
 指定要抽的卡
* img 
 用來比對的原圖路徑
* device 
 模擬器名稱路徑
* cards 
 卡片資料路徑
* sql
 資料庫路徑

建議都用絕對路徑 e.g.
```
// windows
C:/Document/autotest/compare/
// mac
/Users/myUser/autotest/compare/
```
```JSON
{
 "screenShotDir" : "your_path/autotest/compare/",
 "desire" : "your_path/autotest/config/desire.json",
 "img" : "your_path/autotest/img",
 "device" : "your_path/autotest/config/device.json",
 "cards" : "your_path/autotest/config/cards.json",
 "sql" : "your_path/autotest/config/sql.json"
}
```
---
##### 卡片資料 cards.json

原本設定用資料庫[請看這邊](https://github.com/voarlese/cloudia/blob/main/README.md),
為了給沒用資料庫的人～卡片資料用這個json檔

---
##### 想要抽卡的組合 desire.json
* desire_card : 想要抽的聖物
 + name : 是要抽的聖物名稱
 + type : 
  1 : 一定要抽到 
  0 : 依照 limit_card 數量來決定至少抽到幾張
   > 以下範例來看, limit_card : 2 中 type : 1 的**白銀雪虹**一定要抽到, type : 0 的兩張聖物中至少要中一張.

 

* desire_role : 想要抽的角色
 + name : 是要抽的角色名稱
 + type : 
  1 : 一定要抽到
  0 : 依照 limit_card 數量來決定至少抽到幾張
   > 以下範例看 limit_role : 1, 表示 type 0 的兩張卡角色中, 至少要抽到其中一個

```json
{
 "desire_card" : [{"name" : "蒼光騎士團", "type" : 0}, {"name" : "白銀雪虹", "type" : 1}, {"name" : "聖火修米萊亞", "type" : 0}],
 "desire_role" : [{"name" : "傳說角色中的盜賊羅賓", "type" : 0},{"name" : "露淇艾爾", "type" : 0}], 
 "limit_card" : 2,
 "limit_role" : 1
}
```
---
##### device.json
模擬器名稱

```JSON
{
 "device" : "192.168.100.139:5558"
}
```
 > 取得方式
  ```zsh
# 輸入 adb devices  查詢 目前連線的 模擬器
xxx@xxx shell % adb devices
List of devices attached
192.168.100.139:5558 device
  ```
192.168.100.139:5558 就是我的模擬器名稱
---
##### sql.json
資料庫參數, 沒有掛資料庫的話 JDBC_URL 不要填東西, 想掛資料庫做統計 可以去看我的[資料庫建置](https://github.com/voarlese/cloudia/blob/main/README.md)
> 連線路徑範例 : "jdbc:mysql://192.168.100.122:3307/cloudia"
```JSON
{
 "JDBC_URL" : "",
 "USERNAME" : "root",
 "PASSWORD" : "1234"
}
```
---
#### USE
打開終端機
``` zsh
#進入 /out/artifacts/autotest_jar, 建議用絕對路徑, 這是已經在 autotest 資料夾內的寫法
cd /out/artifacts/autotest_jar
# 路徑有問題的話可以用絕對位置
java -jar -Djava.library.path=../../../opencv-4.5.1/build/lib autotest.jar
```
接下來會看到 
[](./gif/16119146743021611914674302.gif)

##### 接下來把畫面操作到無限抽的畫面

然後在這裡輸入 auto 就可以開始自動抽卡了
[](./gif/16119148083281611914808328.gif) 

### 停止

**抽到後會自動停止, 或是按CTRL + C**

[](./gif/1611806811579.jpg)


## 統計
我弄了資料庫來統計機率～這才是這專案主要目的
[要掛資料庫～請看這邊](https://github.com/voarlese/cloudia/blob/main/README.md)

