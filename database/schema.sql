-- ============================================================================
-- Sistema ERP com Hibernate - PostgreSQL / Supabase
-- Instalação limpa das 11 tabelas do DER.
-- ATENÇÃO: este script remove as tabelas existentes.
-- ============================================================================

DROP TABLE IF EXISTS compra_produto CASCADE;
DROP TABLE IF EXISTS compra CASCADE;
DROP TABLE IF EXISTS venda_pagto CASCADE;
DROP TABLE IF EXISTS venda_produto CASCADE;
DROP TABLE IF EXISTS venda CASCADE;
DROP TABLE IF EXISTS formapagto CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS fornecedor CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS pessoa CASCADE;

CREATE TABLE pessoa (
    pes_codigo      SERIAL       PRIMARY KEY,
    pes_nome        VARCHAR(80)  NOT NULL,
    pes_fantasia    VARCHAR(80),
    pes_fisica      CHAR(1)      NOT NULL DEFAULT 'F'
                                  CHECK (pes_fisica IN ('F', 'J')),
    pes_cpfcnpj     VARCHAR(20),
    pes_rgie        VARCHAR(20),
    pes_cadastro    DATE         DEFAULT CURRENT_DATE,
    pes_endereco    VARCHAR(120),
    pes_numero      VARCHAR(10),
    pes_complemento VARCHAR(30),
    pes_bairro      VARCHAR(50),
    pes_cidade      VARCHAR(80),
    pes_uf          CHAR(2),
    pes_cep         VARCHAR(9),
    pes_fone1       VARCHAR(16),
    pes_fone2       VARCHAR(16),
    pes_celular     VARCHAR(16),
    pes_site        VARCHAR(200),
    pes_email       VARCHAR(200),
    pes_ativo       CHAR(1)      NOT NULL DEFAULT 'S'
                                  CHECK (pes_ativo IN ('S', 'N'))
);

CREATE TABLE cliente (
    cli_codigo     SERIAL        PRIMARY KEY,
    pes_codigo     INTEGER       NOT NULL UNIQUE,
    cli_limitecred NUMERIC(18,2) DEFAULT 0 CHECK (cli_limitecred >= 0),
    CONSTRAINT fk_cliente_pessoa
        FOREIGN KEY (pes_codigo) REFERENCES pessoa (pes_codigo)
);

CREATE TABLE fornecedor (
    for_codigo SERIAL      PRIMARY KEY,
    pes_codigo INTEGER     NOT NULL UNIQUE,
    for_contato VARCHAR(80),
    CONSTRAINT fk_fornecedor_pessoa
        FOREIGN KEY (pes_codigo) REFERENCES pessoa (pes_codigo)
);

CREATE TABLE usuario (
    usu_codigo   SERIAL       PRIMARY KEY,
    usu_nome     VARCHAR(80),
    usu_login    VARCHAR(20)  NOT NULL UNIQUE,
    usu_senha    VARCHAR(100),
    usu_cadastro DATE         DEFAULT CURRENT_DATE,
    usu_ativo    CHAR(1)      NOT NULL DEFAULT 'S'
                               CHECK (usu_ativo IN ('S', 'N')),
    usu_admin    CHAR(1)      NOT NULL DEFAULT 'N'
                               CHECK (usu_admin IN ('S', 'N'))
);

CREATE TABLE produto (
    pro_codigo    SERIAL        PRIMARY KEY,
    pro_nome      VARCHAR(80)   NOT NULL,
    pro_tipo      CHAR(1),
    pro_estoque   NUMERIC(14,4) DEFAULT 0 CHECK (pro_estoque >= 0),
    pro_unidade   VARCHAR(5),
    pro_preco     NUMERIC(18,2) DEFAULT 0,
    pro_custo     NUMERIC(18,2) DEFAULT 0,
    pro_atacado   NUMERIC(18,2) DEFAULT 0,
    pro_min       NUMERIC(14,4) DEFAULT 0,
    pro_max       NUMERIC(14,4) DEFAULT 0,
    pro_embalagem NUMERIC(9,0)  DEFAULT 0,
    pro_peso      NUMERIC(14,4) DEFAULT 0,
    pro_cadastro  DATE          DEFAULT CURRENT_DATE,
    pro_obs       TEXT,
    pro_ativo     CHAR(1)       NOT NULL DEFAULT 'S'
                                CHECK (pro_ativo IN ('S', 'N'))
);

CREATE TABLE formapagto (
    fpg_codigo SERIAL      PRIMARY KEY,
    fpg_nome   VARCHAR(80) NOT NULL,
    fpg_ativo  CHAR(1)     NOT NULL DEFAULT 'S'
                            CHECK (fpg_ativo IN ('S', 'N'))
);

CREATE TABLE venda (
    vda_codigo   SERIAL        PRIMARY KEY,
    usu_codigo   INTEGER       NOT NULL,
    cli_codigo   INTEGER       NOT NULL,
    vda_data     DATE          NOT NULL DEFAULT CURRENT_DATE,
    vda_valor    NUMERIC(18,2) DEFAULT 0,
    vda_desconto NUMERIC(18,2) DEFAULT 0,
    vda_total    NUMERIC(18,2) DEFAULT 0,
    vda_obs      TEXT,
    CONSTRAINT fk_venda_usuario
        FOREIGN KEY (usu_codigo) REFERENCES usuario (usu_codigo),
    CONSTRAINT fk_venda_cliente
        FOREIGN KEY (cli_codigo) REFERENCES cliente (cli_codigo)
);

CREATE TABLE venda_produto (
    vep_codigo   SERIAL        PRIMARY KEY,
    vda_codigo   INTEGER       NOT NULL,
    pro_codigo   INTEGER       NOT NULL,
    vep_qtde     NUMERIC(14,4) NOT NULL CHECK (vep_qtde > 0),
    vep_preco    NUMERIC(18,2) NOT NULL CHECK (vep_preco > 0),
    vep_desconto NUMERIC(18,2) DEFAULT 0 CHECK (vep_desconto >= 0),
    vep_total    NUMERIC(18,2) DEFAULT 0 CHECK (vep_total >= 0),
    CONSTRAINT fk_venda_produto_venda
        FOREIGN KEY (vda_codigo) REFERENCES venda (vda_codigo) ON DELETE CASCADE,
    CONSTRAINT fk_venda_produto_produto
        FOREIGN KEY (pro_codigo) REFERENCES produto (pro_codigo)
);

CREATE TABLE venda_pagto (
    vdp_codigo SERIAL        PRIMARY KEY,
    vda_codigo INTEGER       NOT NULL,
    fpg_codigo INTEGER       NOT NULL,
    vdp_valor  NUMERIC(18,2) NOT NULL CHECK (vdp_valor > 0),
    CONSTRAINT fk_venda_pagto_venda
        FOREIGN KEY (vda_codigo) REFERENCES venda (vda_codigo) ON DELETE CASCADE,
    CONSTRAINT fk_venda_pagto_formapagto
        FOREIGN KEY (fpg_codigo) REFERENCES formapagto (fpg_codigo)
);

CREATE TABLE compra (
    cpr_codigo    SERIAL        PRIMARY KEY,
    usu_codigo    INTEGER       NOT NULL,
    for_codigo    INTEGER       NOT NULL,
    cpr_emissao   DATE          NOT NULL DEFAULT CURRENT_DATE,
    cpr_valor     NUMERIC(18,2) DEFAULT 0,
    cpr_desconto  NUMERIC(18,2) DEFAULT 0,
    cpr_total     NUMERIC(18,2) DEFAULT 0,
    cpr_dtentrada DATE,
    cpr_obs       TEXT,
    CONSTRAINT fk_compra_usuario
        FOREIGN KEY (usu_codigo) REFERENCES usuario (usu_codigo),
    CONSTRAINT fk_compra_fornecedor
        FOREIGN KEY (for_codigo) REFERENCES fornecedor (for_codigo)
);

CREATE TABLE compra_produto (
    cpp_codigo   SERIAL        PRIMARY KEY,
    cpr_codigo   INTEGER       NOT NULL,
    pro_codigo   INTEGER       NOT NULL,
    cpp_qtde     NUMERIC(14,4) NOT NULL CHECK (cpp_qtde > 0),
    cpp_preco    NUMERIC(18,2) NOT NULL CHECK (cpp_preco > 0),
    cpp_desconto NUMERIC(18,2) DEFAULT 0 CHECK (cpp_desconto >= 0),
    cpp_total    NUMERIC(18,2) DEFAULT 0 CHECK (cpp_total >= 0),
    CONSTRAINT fk_compra_produto_compra
        FOREIGN KEY (cpr_codigo) REFERENCES compra (cpr_codigo) ON DELETE CASCADE,
    CONSTRAINT fk_compra_produto_produto
        FOREIGN KEY (pro_codigo) REFERENCES produto (pro_codigo)
);

-- Índices das consultas de histórico, detalhes e dashboard.
CREATE INDEX idx_venda_data ON venda (vda_data);
CREATE INDEX idx_compra_emissao ON compra (cpr_emissao);
CREATE INDEX idx_vep_venda ON venda_produto (vda_codigo);
CREATE INDEX idx_vdp_venda ON venda_pagto (vda_codigo);
CREATE INDEX idx_cpp_compra ON compra_produto (cpr_codigo);

-- Credencial acadêmica inicial. No primeiro login válido, a aplicação converte
-- automaticamente a senha legada para BCrypt.
INSERT INTO usuario (usu_nome, usu_login, usu_senha, usu_ativo, usu_admin)
VALUES ('Administrador', 'admin', 'admin', 'S', 'S');

INSERT INTO formapagto (fpg_nome, fpg_ativo) VALUES
    ('Dinheiro', 'S'),
    ('Cartão de Crédito', 'S'),
    ('Cartão de Débito', 'S'),
    ('PIX', 'S'),
    ('Boleto', 'S');
