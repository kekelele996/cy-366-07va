# 电竞馆上机管理系统

面向电竞馆/网咖门店，提供机位状态监控、会员充值消费和赛事管理的运营工具。

## Docker Compose 快速启动

首次启动前复制环境变量文件：

```bash
cp .env.example .env
docker compose up -d
```

访问地址：

- 前端：http://localhost:28506
- 后端健康检查：http://localhost:29506/health
- API 示例：http://localhost:28506/api/overview

## 项目主要功能

- 机位/包厢实时状态看板：以网格或列表形式展示所有机位/包厢的实时状态（空闲/使用中/故障/预约），支持按区域筛选，点击可查看详情。
- 会员充值与时长包：会员账户支持充值余额，购买时长包（如10小时/30小时/月卡），消费时优先扣除时长包余额，不足时扣余额。
- 机位预约与续费：会员可提前预约指定机位和时段，到店扫码开机，使用过程中可续费延长上机时间，快到期前提醒续费。
- 上机时长排行榜：记录会员累计上机时长，生成日/周/月排行榜，激励高频玩家，支持按游戏类型分类统计。
- 赛事报名与战队管理：门店发布电竞赛事（如LOL/CSGO/王者荣耀），玩家以个人或战队形式报名，系统自动抽签分组，记录比赛结果和战绩。

## 到店开机流程

会员到店后，前台在「前台开机」页选择预约单即可完成开机，无需人工改状态：

1. 选择预约单，系统核验会员、机位与预约时段（故障机位、重复开机、未到/超时都会被拦截并提示原因）。
2. 按「时长包优先、余额兜底」预扣首小时（60 分钟）：先按到期时间顺序消费可用时长包，不足分钟按时租折算扣余额。
3. 扣费成功后写入上机记录与扣费明细，预约置为已到店、机位切为使用中。
4. 余额不足时**不写入任何数据**：预约单与机位均保持原状态，页面明确提示差额金额，会员充值后可直接重试。
5. 「运营台」可查看机位实时状态、上机中的机位以及每笔开机扣费（时长包/余额分别列示），并可回看最近扣费记录。

### 主要接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/front/reservations` | 前台预约单（默认待开机），含会员余额、时长包分钟、机位核验信息 |
| POST | `/api/front/reservations/{id}/checkin` | 到店开机；余额不足返回 200 且 `success=false`，携带 `shortfall` 差额 |
| GET | `/api/console/seats` | 运营台机位看板 |
| GET | `/api/console/sessions/active` | 上机中的机位与本次扣费明细 |
| GET | `/api/console/sessions/recent` | 最近上机与扣费记录 |

## 本地开发方式

前端：

```bash
cd frontend
npm install
npm run dev
```

后端：

```bash
cd backend
mvn spring-boot:run
```

## 技术栈

| 分层 | 技术 |
| --- | --- |
| 前端 | Vue 3 + TypeScript、Vant、Vite |
| 后端 | Spring Boot + Java |
| 数据库 | MySQL 8.0 |
| 认证 | JWT |
| 依赖 | MyBatis、Maven |

## 项目目录结构

```text
.
├── backend/              # 后端服务
├── database/             # 数据库脚本
├── frontend/             # 前端应用
├── docker-compose.yml    # 一键部署编排
├── .env.example          # 环境变量示例
└── README.md
```

## 环境变量说明

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| COMPOSE_PROJECT_NAME | Compose 项目名，避免中文目录名导致项目名为空 | ldesportsbar |
| DB_NAME | 数据库名称 | app |
| DB_USER | 数据库用户 | app |
| DB_PASSWORD | 数据库密码 | app_pwd |
| DB_ROOT_PASSWORD | 数据库 root 密码 | root_pwd |
| JWT_SECRET | JWT 签名密钥 | change_me_to_a_long_random_string |
| FRONTEND_PORT | 前端宿主机端口 | 28506 |
| BACKEND_PORT | 后端宿主机端口 | 29506 |
| DB_PORT | 数据库宿主机端口 | 3306 |

## Docker 部署说明

- 使用 `docker compose up -d` 启动，不需要额外传入 `-p`。
- `docker-compose.yml` 顶层已声明 `name: ldesportsbar`，并且 `.env` 包含 `COMPOSE_PROJECT_NAME=ldesportsbar`，可在中文目录名下启动。
- 数据库数据保存在命名卷 `db_data` 中，不依赖当前目录名。
- 前端容器由 Nginx 托管静态资源，并把 `/api/` 反向代理到 `backend:29506`。
- 若本地端口冲突，可修改 `.env` 中的 `FRONTEND_PORT`、`BACKEND_PORT`、`DB_PORT`。

常用命令：

```bash
docker compose config --quiet
docker compose ps
docker compose down
```

## License

MIT
