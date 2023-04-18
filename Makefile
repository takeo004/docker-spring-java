# 本番では make [cmd] e=prod のように入力

# dev = ローカル上にコンテナを作成する場合
# stg = ローカル上に立てたhypervの仮想マシン上にコンテナを作成する場合
# prod = ec2に立てた仮想マシン上にコンテナを作成する場合
ENV=dev

pre:
ifdef e
ENV=${e}
endif

up: pre
	docker-compose -f docker-compose.yml -f docker-compose.$(ENV).yml up -d

stop: pre
	docker-compose -f docker-compose.yml -f docker-compose.$(ENV).yml stop

down: pre
	docker-compose -f docker-compose.yml -f docker-compose.$(ENV).yml down

build: pre
	docker-compose -f docker-compose.yml -f docker-compose.$(ENV).yml build

create-machine: pre
ifeq ($(ENV), stg)
# --hyperv-virtual-switchの名前は設定に合わせて変更してください
	docker-machine --native-ssh create -d hyperv --hyperv-virtual-switch "docker-virtual-switch" line-secretary-stg
else ifeq ($(ENV), prod)
	docker-machine create -d amazonec2 --amazonec2-open-port 8080 --amazonec2-region ap-northeast-1 --amazonec2-ami ami-00bc9b7f0e98dc134 line-secretary
endif

stop-machine: pre
ifeq ($(ENV), stg)
	docker-machine stop line-secretary-stg
else ifeq ($(ENV), prod)
	docker-machine stop line-secretary
endif

start-machine: pre
ifeq ($(ENV), stg)
	docker-machine start line-secretary-stg
else ifeq ($(ENV), prod)
	docker-machine start line-secretary
endif

remove-machine: pre
ifeq ($(ENV), stg)
	docker-machine rm line-secretary-stg
else ifeq ($(ENV), prod)
	docker-machine rm line-secretary
endif