# AT2 - CRUD de Estoque com Kotlin + Ktor

Projeto desenvolvido para a disciplina de Programação para Dispositivos Móveis II, utilizando Kotlin, Ktor e Supabase para criação de uma API REST de gerenciamento de produtos e estoque.

---

# Tecnologias Utilizadas

- Kotlin
- Ktor
- Supabase
- PostgreSQL
- Gradle
- Kotlinx Serialization

---

# Funcionalidades

## Produtos
- Listar produtos
- Buscar produto por ID
- Criar produto
- Atualizar produto
- Remover produto

## Estoque
- Listar itens do estoque
- Buscar item por ID
- Criar item de estoque
- Atualizar item de estoque
- Remover item de estoque
- Resumo do estoque agrupado por produto

---

# Estrutura do Projeto

```bash
src/main/kotlin/com/estocadao
│
├── config
├── models
├── routes
└── services
```

---

# Configuração do Banco de Dados

O projeto utiliza o Supabase como banco de dados PostgreSQL.

## Passos

1. Criar um projeto no Supabase
2. Abrir o SQL Editor
3. Executar o script localizado em:

```bash
sql/create_tables.sql
```

---

# Variáveis de Ambiente

Criar um arquivo `.env` na raiz do projeto:

```env
SUPABASE_URL=https://SEU_PROJETO.supabase.co
SUPABASE_KEY=sua_chave_anon
PORT=8080
```

Importante:

- Não compartilhar a chave da API no GitHub
- O arquivo `.env` deve estar no `.gitignore`

---

# Como Executar o Projeto

## 1. Clonar o repositório

```bash
git clone LINK_DO_REPOSITORIO
```

---

## 2. Entrar na pasta do projeto

```bash
cd at2-crud-estoque
```

---

## 3. Executar o projeto

Windows PowerShell:

```powershell
.\gradlew.bat run
```

Servidor disponível em:

```text
http://127.0.0.1:8080
```

---

# Endpoints

# Produtos

| Método | Endpoint | Descrição |
|---|---|---|
| GET | /products | Lista todos os produtos |
| GET | /products/{id} | Busca produto por ID |
| POST | /products | Cria um produto |
| PUT | /products/{id} | Atualiza um produto |
| DELETE | /products/{id} | Remove um produto |

---

# Estoque

| Método | Endpoint | Descrição |
|---|---|---|
| GET | /stock | Lista todos os itens |
| GET | /stock/{id} | Busca item por ID |
| GET | /stock/summary | Resumo do estoque |
| POST | /stock | Cria item de estoque |
| PUT | /stock/{id} | Atualiza item |
| DELETE | /stock/{id} | Remove item |

---

# Resumo do Estoque

O endpoint `/stock/summary` realiza agregação diretamente no banco de dados utilizando:

- `GROUP BY`
- `SUM`

A agregação é realizada via SQL no Supabase.

---

# Integridade Referencial

A tabela `stock_items` possui chave estrangeira para `products`.

Ao remover um produto, os itens relacionados do estoque são tratados utilizando integridade referencial no banco de dados.

---

# Schema do Banco

Os prints do schema e das tabelas do banco estão disponíveis na pasta:

```bash
/prints
```

---

# Autor

João Lima