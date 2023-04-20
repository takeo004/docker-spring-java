# LINE秘書
## 前提
windowsを想定して以下の手順は記載されています。
## デプロイ手順
## デプロイ
### makeコマンド準備
[windowsの場合](https://zenn.dev/genki86web/articles/6e61c167fbe926)

### 仮想環境準備（仮想環境を立てずにローカルでコンテナを起動する場合は、実施不要）
#### ローカルに仮想環境を建てる場合
※windows 11 pro でhypervが使用できる前提です
```
make create-machine e=stg
```

#### AWS上のEC2に仮想環境を建てる場合
```
make create-machine e=prod
```

### 仮想環境にリポジトリをプル（仮想環境を立てずにローカルでコンテナを起動する場合は、実施不要）
1. 仮想間環境にssh
```
docker-machine (hypervの場合：--native-ssh) ssh line-secretary(-stg)
```
2. 仮想環境上で [リポジトリ](https://github.com/takeo004/line-secretary) をプル
```
git clone https://github.com/takeo004/line-secretary.git
```
3. 仮想環境を出る
```
exit
```
### 設定ファイル配置
#### 仮想環境を立てている場合は、仮想環境に移動
```
docker-machine (hypervの場合：--native-ssh) ssh line-secretary(-stg)
```
#### init.sql
1. *initdb.d* ディレクトリに配置されている、*init.sql* の *username* と *userpassword* を変更する

#### application-sec.yml
1. *server/src/main/resources* にある *application-sec-template.yml* を同ディレクトリにコピーする
2. ファイル名を *application-sec.yml* に変更する
3. 設定値を入力する

### google-calendar-credentials.json
1. [こちら ](https://qiita.com/ho-rai/items/df82e4fb2e46217e6f4e)の1.2.6までをもとに、GCPのアクセスキーをjson形式で取得する
2. *serve/src/main/resources* 配下に配置して名前を *google-calendar-credentials.json* に変更する
### 操作対象変更（仮想環境を立てない場合は、実施不要）
```
docker-machine env line-secretary(-stg) | Invoke-Expression
```

### コンテナ起動
#### 環境説明
dev = ローカル上にコンテナを作成する場合\
stg = ローカル上に立てたhypervの仮想マシン上にコンテナを作成する場合\
prod = ec2に立てた仮想マシン上にコンテナを作成する場合\

#### 手順
1. docker-composeのbuild
```
make build e=dev/stg/prod
```
2. docker-composeの起動
```
make up e=dev/stg/prod
```

### アプリケーションの起動
1. コンテナに入る
```
docker-compose exec app bash
```
2. jarファイル作成
```
sh gradlew build
```
3. アプリケーションの起動
```
java -classpath -jar build/libs/(jarの名前：tabキー押せば出ます)
```

## 補足
### EC2のメモリについて
AWSの無料枠で動くようにしているので、Springを起動した時点で落ちる可能性があります。\
その場合は、[こちら](https://karakunphoto.com/development/server/1218/) を参照にして、仮想環境のswapの設定を行ってください。\
仮想環境に入る場合は以下のコマンドになります
```
docker-machine ssh line-secretary
```

### LINEのWebhook設定について
EC2環境の場合は、IPアドレスを直でLINEのWebhookに設定してあげればよいかと思うのですが、ローカルでコンテナ起動した場合や、ローカルに仮想環境を立てた場合は [こちら](https://qiita.com/Esfahan/items/74e71edfe17d9935d47e) を参考に、外部にポート解放することで、LINEのWebhookにひっかけることができるかと思います。（セキュリティ的にはあまりよろしくなさそう）

### makeコマンドについて
実際に実行している処理は、Makefileに記載しているので、気になったら参考にしてください

### 実装について
#### 基本
機能を追加する場合は、[コントローラ](https://github.com/takeo004/line-secretary/tree/master/server/src/main/java/com/example/api/controller)、[サービス](https://github.com/takeo004/line-secretary/tree/master/server/src/main/java/com/example/api/service)、[リポジトリ](https://github.com/takeo004/line-secretary/tree/master/server/src/main/java/com/example/api/repository)、を追加もしくは修正してください。

#### コントローラについて
基本的にLINEから来たリクエストをいい感じに、リフレクションでコントローラに振り分けています。（ちょっときもいのでほかのやり方があれば教えてほしい）
コントローラを追加する場合に修正が必要なクラスは以下になります。
- [ChatGptService](https://github.com/takeo004/line-secretary/blob/2b4867d43e008ad8567784f090b65eb0eddc8ec1/server/src/main/java/com/example/api/service/ChatGptService.java#L58)
    - chatGPTにリクエストの文章を読み込ませて、json型で内容を整理してもらっています。<br>
    機能を追加する場合は、このjsonの形式を指定してあげる必要があります。<br>
    基本的には形式は自由ですが、一番最初の *method* と *methodDetail* は形式を変えないようにして、新たに採番してあげてください。
- [MethodType](https://github.com/takeo004/line-secretary/blob/master/server/src/main/java/com/example/api/constant/MethodType.java)
    - Enum形式で処理内容の大枠を定義しています。（例：予定管理機能を「SCHEDULE」として定義しています。）<br>
    - *method* には、jsonの形式を指定した際に設定したmethodを入れてあげてください。
    - *controllerName* には、処理対象になるコントローラクラスのクラス名を、頭文字小文字のキャメルケースで設定してください。（リフレクションに使います）
- [MethodDetailType](https://github.com/takeo004/line-secretary/blob/master/server/src/main/java/com/example/api/constant/MethodDetailType.java)
    - Enum形式で処理内容の詳細を定義しています。（例：予定登録を「SCHEDULE_REGIST」として定義しています。）<br>
    - *method* には、MethodTypeを設定してあげてください。
    - *methodDetail* には、jsonの形式を指定した際に設定した *methodDetail* を設定してあげてください。
    - *methodName* には、コントローラに定義したメソッド名を記載してください。
    - *requestClass* には、その処理用に用意したリクエストクラスのClassオブジェクトを渡してあげてください。
コントローラを作成する場合は、[BaseController](https://github.com/takeo004/line-secretary/blob/master/server/src/main/java/com/example/api/controller/BaseController.java) を継承してください。<br>
メソッドを作成した場合は、最初に [super.generateRequest](https://github.com/takeo004/line-secretary/blob/2b4867d43e008ad8567784f090b65eb0eddc8ec1/server/src/main/java/com/example/api/controller/BaseController.java#L21) を呼び出して、リクエストクラスを初期化してください。

#### 処理の一時中断について
処理の途中で一度ユーザーに入力を求める場合、一旦情報を退避する用のテーブルとして、[UserState](https://github.com/takeo004/line-secretary/blob/master/server/src/main/java/com/example/api/entity/UserState.java) テーブルを用意しています。<br>
LINEでメッセージが送られた際に、対象のユーザーのレコードがこのテーブルにあった場合は、[ProcessContinueHandler](https://github.com/takeo004/line-secretary/blob/master/server/src/main/java/com/example/api/handler/ProcessContinueHandler.java) をに処理が飛ぶので、ここのswitch文に処理を記載して、サービスを呼び出すなどして旨い事やってください<br>
*UserState* があれば、とりあえずこのハンドラーに飛んでくるので、中断する時にレコードを追加、処理が終わったらレコードを削除を徹底してください。

#### DBアクセスについて
*JPA* を使っています。「[java jpa](https://www.google.com/search?q=java+jpa&rlz=1C1TKQJ_jaJP1020JP1020&sxsrf=APwXEde8cqYcvxH60dABaIf0OXHFlDTvCQ%3A1681994070932&ei=VjFBZN3COIyN-AbinJjoDg&ved=0ahUKEwjdxfKYvLj-AhWMBt4KHWIOBu0Q4dUDCA8&uact=5&oq=java+jpa&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQAzIECCMQJzIECCMQJzIHCAAQigUQQzIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQ6CggAEEcQ1gQQsANKBAhBGABQjgZYjgZgmQpoAXAAeACAAbcBiAG3AZIBAzAuMZgBAKABAqABAcgBCsABAQ&sclient=gws-wiz-serp)」で検索してください。