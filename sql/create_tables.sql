-- ============================================================
-- Script SQL para criar as tabelas no Supabase (PostgreSQL)
-- Execute no SQL Editor do Supabase Dashboard
-- ============================================================

-- Habilitar extensão para geração de UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- Tabela: products
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    sku         VARCHAR(100) NOT NULL UNIQUE,
    category    VARCHAR(100),
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- Tabela: stock_items
-- ============================================================
CREATE TABLE IF NOT EXISTS stock_items (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id  UUID        NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity    INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    unit_price  DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (unit_price >= 0),
    location    VARCHAR(255),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_stock_items_updated_at
    BEFORE UPDATE ON stock_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- Habilitar Row Level Security (RLS) — necessário no Supabase
-- ============================================================
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE stock_items ENABLE ROW LEVEL SECURITY;

-- Políticas de acesso (permissão total via service_role / anon key)
CREATE POLICY "Allow all for anon" ON products
    FOR ALL USING (true) WITH CHECK (true);

CREATE POLICY "Allow all for anon" ON stock_items
    FOR ALL USING (true) WITH CHECK (true);

-- ============================================================
-- Dados de exemplo (opcional)
-- ============================================================
INSERT INTO products (name, description, sku, category)
VALUES
    ('Caneta Azul', 'Caneta esferográfica ponta média', 'CAN-AZ-001', 'Papelaria'),
    ('Caderno A4', 'Caderno universitário 200 folhas', 'CAD-A4-001', 'Papelaria'),
    ('Borracha Branca', 'Borracha para papel', 'BOR-BR-001', 'Papelaria');

INSERT INTO stock_items (product_id, quantity, unit_price, location)
SELECT id, 350, 1.50, 'Prateleira A1' FROM products WHERE sku = 'CAN-AZ-001';

INSERT INTO stock_items (product_id, quantity, unit_price, location)
SELECT id, 120, 12.90, 'Prateleira B2' FROM products WHERE sku = 'CAD-A4-001';

INSERT INTO stock_items (product_id, quantity, unit_price, location)
SELECT id, 500, 0.80, 'Prateleira A3' FROM products WHERE sku = 'BOR-BR-001';
