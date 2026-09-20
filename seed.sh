#!/bin/bash

set -euo pipefail

# ─── Конфигурация ─────────────────────────────────────────────────
PAYMENT_URL="${PAYMENT_URL:-http://localhost:8083}"
STOCK_URL="${STOCK_URL:-http://localhost:8082}"

# ─── Цветной вывод ────────────────────────────────────────────────
GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()  { echo -e "${GREEN}[seed]${NC} $*"; }
warn() { echo -e "${YELLOW}[warn]${NC} $*"; }
fail() { echo -e "${RED}[fail]${NC} $*" >&2; exit 1; }
head() { echo -e "${CYAN}── $* ──${NC}"; }

# ─── POST-обёртка ─────────────────────────────────────────────────
post() {
    local url="$1" json="$2" name="$3"
    local response http_code body

    response=$(curl -s -w "\n%{http_code}" -X POST "$url" \
        -H "Content-Type: application/json" \
        -d "$json") || { warn "  ✗ $name — curl упал"; return 1; }

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    case "$http_code" in
        2*)  log "  ✓ $name" ;;
        409) warn "  ~ $name уже существует" ;;
        *)   warn "  ✗ $name — HTTP $http_code: $body"; return 1 ;;
    esac
}


# ─── Аккаунты ─────────────────────────────────────────────────────
seed_accounts() {
    head "Аккаунты (payment-service)"

    # ── «Богатые» — платежи проходят всегда
    echo "$PAYMENT_URL/account" '{"ownerName":"Alice","balance":10000}'    "Alice        (10000)"
    post "$PAYMENT_URL/account" '{"ownerName":"Alice","balance":10000}'    "Alice        (10000)"
    post "$PAYMENT_URL/account" '{"ownerName":"Fedor","balance":5000}'     "Fedor        (5000)"
    post "$PAYMENT_URL/account" '{"ownerName":"Ivan","balance":3000}'      "Ivan         (3000)"

    # ── «Средние» — хватает на часть заказов
    post "$PAYMENT_URL/account" '{"ownerName":"Bob","balance":500}'        "Bob          (500)"
    post "$PAYMENT_URL/account" '{"ownerName":"Elena","balance":250}'      "Elena        (250)"
    post "$PAYMENT_URL/account" '{"ownerName":"Oleg","balance":100}'       "Oleg         (100)"

    # ── «На грани» — ровно на один товар
    post "$PAYMENT_URL/account" '{"ownerName":"Kate","balance":100}'       "Kate         (100 — ровно на Car)"
    post "$PAYMENT_URL/account" '{"ownerName":"Max","balance":50}'         "Max          (50  — ровно на Widget)"

    # ── «Пустые» — для теста отказа/retry
    post "$PAYMENT_URL/account" '{"ownerName":"Carol","balance":0}'        "Carol        (0   — недостаточно средств)"
    post "$PAYMENT_URL/account" '{"ownerName":"Ghost","balance":0}'        "Ghost        (0   — для retry-логики)"
}

# ─── Товары ───────────────────────────────────────────────────────
seed_products() {
    head "Товары: Электроника"
    post "$STOCK_URL/product/create" '{"name":"iPhone 15","price":999,"available":25}'          "iPhone 15          (999, 25)"
    post "$STOCK_URL/product/create" '{"name":"Samsung Galaxy S24","price":899,"available":30}' "Samsung S24        (899, 30)"
    post "$STOCK_URL/product/create" '{"name":"MacBook Pro 14","price":1999,"available":10}'    "MacBook Pro 14     (1999, 10)"
    post "$STOCK_URL/product/create" '{"name":"AirPods Pro","price":249,"available":50}'        "AirPods Pro        (249, 50)"
    post "$STOCK_URL/product/create" '{"name":"iPad Air","price":599,"available":15}'           "iPad Air           (599, 15)"

    head "Товары: Дом и быт"
    post "$STOCK_URL/product/create" '{"name":"Coffee Machine","price":149,"available":20}'     "Coffee Machine     (149, 20)"
    post "$STOCK_URL/product/create" '{"name":"Vacuum Cleaner","price":299,"available":12}'     "Vacuum Cleaner     (299, 12)"
    post "$STOCK_URL/product/create" '{"name":"Microwave","price":199,"available":8}'           "Microwave          (199, 8)"

    head "Товары: Одежда"
    post "$STOCK_URL/product/create" '{"name":"T-Shirt","price":29,"available":200}'            "T-Shirt            (29, 200)"
    post "$STOCK_URL/product/create" '{"name":"Jeans","price":79,"available":75}'               "Jeans              (79, 75)"
    post "$STOCK_URL/product/create" '{"name":"Sneakers","price":129,"available":40}'           "Sneakers           (129, 40)"

    head "Товары: Книги"
    post "$STOCK_URL/product/create" '{"name":"Clean Code","price":45,"available":100}'         "Clean Code         (45, 100)"
    post "$STOCK_URL/product/create" '{"name":"Effective Java","price":55,"available":80}'      "Effective Java     (55, 80)"

    head "Товары: пограничные случаи (для тестов)"
    post "$STOCK_URL/product/create" '{"name":"LastOne","price":100,"available":1}'             "LastOne            (100, 1  — последний экземпляр)"
    post "$STOCK_URL/product/create" '{"name":"AlmostGone","price":500,"available":2}'          "AlmostGone         (500, 2  — почти кончился)"
    post "$STOCK_URL/product/create" '{"name":"OutOfStock","price":100,"available":0}'          "OutOfStock         (100, 0  — нет на складе)"
    post "$STOCK_URL/product/create" '{"name":"Freebie","price":0,"available":10}'              "Freebie            (0,   10 — бесплатный)"
    post "$STOCK_URL/product/create" '{"name":"Expensive","price":99999,"available":1}'         "Expensive          (99999, 1 — дороже баланса Alice)"
}

# ─── Main ─────────────────────────────────────────────────────────
main() {

    echo
    seed_accounts
    echo
    seed_products
    echo
    log "Готово. Создано: 10 аккаунтов, 20 товаров."
}

main "$@"