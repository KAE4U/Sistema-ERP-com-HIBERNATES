# Sistema ERP com Hibernate

Aplicação desktop Java 17/Swing para cadastros, compras, vendas, estoque, relatórios PDF e dashboard gerencial. O projeto preserva o fluxo visual do ERP original e substitui integralmente o JDBC manual por JPA/Hibernate ORM.

## Funcionalidades

- Login com usuários ativos, senha BCrypt e migração automática de senhas legadas em texto no primeiro acesso válido.
- Cadastro público no login: cria **sempre** perfil comum e ativo; privilégios administrativos só podem ser definidos na tela autenticada por um administrador.
- Cadastros de Usuário, Cliente, Fornecedor, Produto (incluindo tipo e estoque mínimo) e Forma de Pagamento.
- Venda com múltiplos itens e pagamentos, validação de estoque e conciliação exata dos pagamentos.
- Compra com múltiplos itens; inclusão e exclusão/cancelamento revertem estoque atomicamente.
- Históricos, detalhes, busca, máscaras de campos, CPF/CNPJ, sessão e exclusões restritas a administrador.
- FlatLaf escuro/claro.
- PDFs de usuários (sem senha/hash), clientes, fornecedores, produtos/estoque, formas de pagamento, vendas detalhadas e compras detalhadas.
- Dashboard escuro com quatro KPIs, linha vendas × compras dos últimos 12 meses, donut de recebimentos e alerta de estoque mínimo. Consultas e PDFs usam `SwingWorker` para não bloquear a interface.

## Arquitetura

O package raiz é `br.unip.erp`:

- `model`: 11 entidades anotadas com `jakarta.persistence`; `RelatorioLinha` e `DashboardDTO` são DTOs e não entidades.
- `view`: Java Swing/MDI e JFreeChart.
- `controller`: mediação MVC, validação e autorização.
- `dao`: `GenericDAO<T,ID>`, `JpaGenericDAO<T,ID>`, DAOs concretos e consultas JPQL/nativas.
- `service`: transações de venda/compra, autenticação BCrypt, autorização e PDFBox.
- `util/JpaUtil`: único bootstrap do `EntityManagerFactory` via `META-INF/persistence.xml`.

`hibernate.hbm2ddl.auto=validate`: a aplicação nunca cria nem destrói tabelas automaticamente.

## Banco PostgreSQL / Supabase

1. Banco novo: execute `database/schema.sql`.
2. Banco do ERP anterior: faça backup e execute `database/migration_v2_hibernate.sql`.
3. Configure por variáveis (recomendado):

```bash
export DB_URL='jdbc:postgresql://aws-0-REGION.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0'
export DB_USER='postgres.PROJECT-REF'
export DB_PASSWORD='SUA_SENHA'
```

Como alternativa, copie as chaves de `src/main/resources/config.properties` para `config.local.properties` na raiz. Esse arquivo é ignorado pelo Git. O template não contém credenciais reais.

## Build e execução

Requer JDK 17+ e Maven 3.9+.

```bash
mvn clean package -DskipTests
mvn exec:java
# ou
java -jar target/sistema-erp-hibernate.jar
```

O schema inicial cria `admin` / `admin` em formato legado apenas para demonstração. No primeiro login bem-sucedido a senha é substituída por BCrypt; altere-a em ambientes reais. Na tela de Usuário, deixar senha vazia ao editar preserva a senha atual e hashes nunca são exibidos.

Os PDFs são gravados em `relatorios/` (ignorada pelo Git). Cada formulário/histórico possui **Relatório PDF**, e o menu Relatórios permite gerar um tipo ou todos.

## Integridade e segurança

Venda e compra usam `EntityManagerTransaction`, lock pessimista de produto e uma única transação para documento, itens e estoque. Quantidades/preços devem ser positivos; descontos não podem ser negativos; a venda exige pagamentos cuja soma seja igual ao total. Exclusões administrativas revertem o estoque antes de remover o agregado. A autorização é repetida em controller/service, não apenas na View.

## Fontes de versões

- [Hibernate ORM 6.6 Quickstart](https://docs.hibernate.org/orm/6.6/quickstart/pdf/index.pdf)
- [PDFBox 3.0.6 — Maven Central](https://central.sonatype.com/artifact/org.apache.pdfbox/pdfbox/3.0.6)
- [JFreeChart 1.5.5 — Maven Central](https://central.sonatype.com/artifact/org.jfree/jfreechart/1.5.5)
- [FlatLaf 3.7.1 — Maven Central](https://central.sonatype.com/artifact/com.formdev/flatlaf/3.7.1)

Conteúdo reescrito para conformidade com licenciamento.

## Atendimento ao enunciado

A matriz completa entre os itens do trabalho e as classes implementadas está em [`docs/requisitos-atendidos.md`](docs/requisitos-atendidos.md). O dashboard é aberto automaticamente após o login e pode ser atualizado pelo menu **Dashboard**.

## Validação realizada

- `mvn clean package -DskipTests`: build concluído e fat JAR gerado.
- Inicialização do fat JAR verificada sem erro de assinatura.
- Smoke test temporário com Hibernate em banco compatível com PostgreSQL: login legado e migração BCrypt, cadastro público, compra, duas vendas consecutivas, estoque, reversão, detalhes, dashboard e os sete PDFs.
- O smoke test foi somente uma validação de execução e não foi incluído no projeto.

Se o Hibernate informar incompatibilidade ou ausência de coluna ao iniciar, execute o script correspondente antes de abrir a aplicação:

- banco existente: `database/migration_v2_hibernate.sql`;
- banco vazio: `database/schema.sql`.
