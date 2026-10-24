-- ============================================================================
-- Migração idempotente do ERP JDBC anterior para a versão Hibernate.
-- Faça backup antes de executar no Supabase.
-- ============================================================================

BEGIN;

-- Novo atributo previsto no DER do trabalho com Hibernate.
ALTER TABLE produto
    ADD COLUMN IF NOT EXISTS pro_tipo CHAR(1);

-- Garante o tipo exato esperado pelas anotações JPA.
ALTER TABLE produto
    ALTER COLUMN pro_tipo TYPE CHAR(1) USING LEFT(pro_tipo, 1);

-- BCrypt ocupa 60 caracteres; 100 deixa margem para evolução do algoritmo.
ALTER TABLE usuario
    ALTER COLUMN usu_senha TYPE VARCHAR(100);

-- Extensão de autorização preservada da versão anterior.
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS usu_admin CHAR(1) NOT NULL DEFAULT 'N';

UPDATE usuario
SET usu_admin = 'N'
WHERE usu_admin IS NULL;

-- Mantém o usuário acadêmico inicial como administrador em bancos antigos.
UPDATE usuario
SET usu_admin = 'S'
WHERE usu_login = 'admin';

-- As associações Cliente/Pessoa e Fornecedor/Pessoa são 1:1 nas entidades.
CREATE UNIQUE INDEX IF NOT EXISTS uq_cliente_pessoa
    ON cliente (pes_codigo);

CREATE UNIQUE INDEX IF NOT EXISTS uq_fornecedor_pessoa
    ON fornecedor (pes_codigo);

-- Índices usados nos históricos, detalhes, relatórios e dashboard.
CREATE INDEX IF NOT EXISTS idx_venda_data
    ON venda (vda_data);

CREATE INDEX IF NOT EXISTS idx_compra_emissao
    ON compra (cpr_emissao);

CREATE INDEX IF NOT EXISTS idx_vep_venda
    ON venda_produto (vda_codigo);

CREATE INDEX IF NOT EXISTS idx_vdp_venda
    ON venda_pagto (vda_codigo);

CREATE INDEX IF NOT EXISTS idx_cpp_compra
    ON compra_produto (cpr_codigo);

COMMIT;
