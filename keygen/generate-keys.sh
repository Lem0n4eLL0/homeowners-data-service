#!/bin/sh
set -e

KEY_DIR="/keys"
PRIVATE_KEY="$KEY_DIR/private.pem"
PUBLIC_KEY="$KEY_DIR/public.pem"
HMAC_KEY="$KEY_DIR/otp-hmac.key"

mkdir -p "$KEY_DIR"

# ===== ECC KEYS =====
if [ -f "$PRIVATE_KEY" ] && [ -f "$PUBLIC_KEY" ]; then
  echo "ECC ключи уже существуют. Пропускаем генерацию."
else
  echo "Генерация ECC-ключевой пары (P-256)..."

  # Генерируем приватный ключ в PKCS#8
  openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -out "$PRIVATE_KEY"

  # Извлекаем публичный ключ
  openssl pkey -in "$PRIVATE_KEY" -pubout -out "$PUBLIC_KEY"

  chmod 644 "$PRIVATE_KEY"
  chmod 644 "$PUBLIC_KEY"

  echo "ECC ключи успешно созданы в $KEY_DIR"
fi

# ===== OTP HMAC SECRET =====
if [ -f "$HMAC_KEY" ]; then
  echo "OTP HMAC секрет уже существует. Пропускаем генерацию."
else
  echo "Генерация OTP HMAC секрета..."

  # 32 байта = 256 бит
  head -c 32 /dev/urandom | base64 > "$HMAC_KEY"

  chmod 644 "$HMAC_KEY"

  echo "OTP HMAC секрет создан в $KEY_DIR"
fi

echo "Генерация завершена."
