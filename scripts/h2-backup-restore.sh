#!/bin/sh
# Usage:
#   ./h2-backup-restore.sh backup /data/shortener.mv.db /backup/shortener-backup.mv.db
#   ./h2-backup-restore.sh restore /backup/shortener-backup.mv.db /data/shortener.mv.db

set -e

if [ "$1" = "backup" ]; then
  cp "$2" "$3"
  echo "Backup complete: $2 -> $3"
elif [ "$1" = "restore" ]; then
  cp "$2" "$3"
  echo "Restore complete: $2 -> $3"
else
  echo "Usage: $0 backup|restore <src> <dest>"
  exit 1
fi