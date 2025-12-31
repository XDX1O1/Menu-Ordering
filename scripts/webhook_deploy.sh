#!/bin/bash

# ChopChop - Webhook Auto Deploy Script
# Triggered by GitHub webhook

DEPLOY_USER="chopchop"

echo "Ensuring correct permissions for $DEPLOY_USER..."

# 1. Ensure the directory is owned by the deployment user
chown -R $DEPLOY_USER:$DEPLOY_USER $REPO_DIR

# 2. Add the safe directory exception specifically for the deployment user
sudo -u $DEPLOY_USER git config --global --add safe.directory $REPO_DIR 2>/dev/null || true

# 3. Clean up any stale lock files that cause "Permission Denied"
rm -f $REPO_DIR/.git/index.lock
rm -f $REPO_DIR/.git/FETCH_HEAD

set -e

# Logging
LOG_FILE="/var/log/chopchop-deploy.log"
exec > >(tee -a "$LOG_FILE") 2>&1

echo "=========================================="
echo "Auto-Deploy triggered at $(date)"
echo "=========================================="

# Navigate to app directory
cd /opt/Menu-Ordering

# Backup current version (just in case)
echo "[1/5] Creating backup..."
cp target/menu-ordering-app-0.0.1-SNAPSHOT.jar target/menu-ordering-app-backup-$(date +%Y%m%d_%H%M%S).jar || true

# Pull latest code
echo "[2/5] Pulling latest code..."
sudo -u chopchop git fetch origin
sudo -u chopchop git reset --hard origin/main  # Force update to latest

# Build application
echo "[3/5] Building application..."
sudo -u chopchop mvn clean package -DskipTests

# Check if build successful
if [ ! -f "target/menu-ordering-app-0.0.1-SNAPSHOT.jar" ]; then
    echo "ERROR: Build failed! JAR not found."
    echo "Restoring from backup..."
    cp target/menu-ordering-app-backup-*.jar target/menu-ordering-app-0.0.1-SNAPSHOT.jar
    exit 1
fi

# Restart service
echo "[4/5] Restarting service..."
sudo systemctl restart chopchop

# Wait for service to start
sleep 5

# Verify service is running
echo "[5/5] Verifying service..."
if systemctl is-active --quiet chopchop; then
    echo "SUCCESS: Application deployed and running!"

    # Cleanup old backups (keep last 5)
    cd target
    ls -t menu-ordering-app-backup-*.jar 2>/dev/null | tail -n +6 | xargs rm -f 2>/dev/null || true

    exit 0
else
    echo "ERROR: Service failed to start!"
    echo "Check logs: sudo journalctl -u chopchop -n 50"
    exit 1
fi
