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