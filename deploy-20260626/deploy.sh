#!/bin/bash
# ============================================================
# 能碳数智化平台 - 一键部署脚本 (Linux)
# 使用方式: chmod +x deploy.sh && ./deploy.sh
# ============================================================

set -e

DEPLOY_BASE="/opt/energy-carbon"
BACKEND_DIR="${DEPLOY_BASE}/backend"
FRONTEND_DIR="${DEPLOY_BASE}/frontend"
MOBILE_DIR="${DEPLOY_BASE}/mobile"
LOG_DIR="${DEPLOY_BASE}/logs"

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}  能碳数智化平台 - 一键部署脚本${NC}"
echo -e "${GREEN}  部署时间: $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo -e "${GREEN}============================================${NC}"

# 1. 创建目录结构
echo -e "${YELLOW}[1/6] 创建部署目录...${NC}"
mkdir -p ${BACKEND_DIR} ${FRONTEND_DIR} ${MOBILE_DIR} ${LOG_DIR}

# 2. 停止旧的后端服务
echo -e "${YELLOW}[2/6] 停止旧的后端服务...${NC}"
OLD_PID=$(ps aux | grep 'energy-carbon-admin' | grep -v grep | awk '{print $2}')
if [ -n "$OLD_PID" ]; then
    echo "  停止旧进程 PID: ${OLD_PID}"
    kill ${OLD_PID} 2>/dev/null || true
    sleep 3
    # 强制杀
    STILL_ALIVE=$(ps aux | grep 'energy-carbon-admin' | grep -v grep | awk '{print $2}')
    if [ -n "$STILL_ALIVE" ]; then
        kill -9 ${STILL_ALIVE} 2>/dev/null || true
        echo "  已强制停止"
    fi
else
    echo "  无运行中的后端服务"
fi

# 3. 部署后端
echo -e "${YELLOW}[3/6] 部署后端服务...${NC}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cp -f "${SCRIPT_DIR}/backend/energy-carbon-admin-master336.jar" "${BACKEND_DIR}/app.jar"
echo "  后端 JAR 已复制到 ${BACKEND_DIR}/app.jar"

# 4. 部署前端
echo -e "${YELLOW}[4/6] 部署前端管理后台...${NC}"
rm -rf ${FRONTEND_DIR}/*
cp -rf "${SCRIPT_DIR}/frontend/"* "${FRONTEND_DIR}/"
echo "  前端文件已复制到 ${FRONTEND_DIR}/"

# 5. 部署移动端
echo -e "${YELLOW}[5/6] 部署移动端H5...${NC}"
rm -rf ${MOBILE_DIR}/*
cp -rf "${SCRIPT_DIR}/mobile/"* "${MOBILE_DIR}/"
echo "  移动端文件已复制到 ${MOBILE_DIR}/"

# 6. 启动后端服务
echo -e "${YELLOW}[6/6] 启动后端服务...${NC}"
cd ${BACKEND_DIR}
nohup java -Xms512m -Xmx2g \
    -jar app.jar \
    --spring.profiles.active=prod \
    > ${LOG_DIR}/app.log 2>&1 &
NEW_PID=$!
echo "  后端服务已启动, PID: ${NEW_PID}"

# 等待启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 5

# 检查是否启动成功
if ps -p ${NEW_PID} > /dev/null 2>&1; then
    echo -e "${GREEN}============================================${NC}"
    echo -e "${GREEN}  部署成功!${NC}"
    echo -e "${GREEN}  后端PID: ${NEW_PID}${NC}"
    echo -e "${GREEN}  日志: tail -f ${LOG_DIR}/app.log${NC}"
    echo -e "${GREEN}============================================${NC}"
else
    echo -e "${RED}============================================${NC}"
    echo -e "${RED}  后端启动失败，请检查日志:${NC}"
    echo -e "${RED}  tail -50 ${LOG_DIR}/app.log${NC}"
    echo -e "${RED}============================================${NC}"
    exit 1
fi
