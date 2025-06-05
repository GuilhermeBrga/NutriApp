# NutriApp

Aplicativo para gerenciamento e análise nutricional com suporte a tradução automática via LibreTranslate.

---

## 🛠 Requisitos

Para executar este projeto em máquinas **Windows**, você precisará ter:

- [WSL2 (Windows Subsystem for Linux 2)](https://learn.microsoft.com/pt-br/windows/wsl/install)
- [Docker Desktop com suporte ao WSL2](https://www.docker.com/products/docker-desktop/)

---

## 🚀 Como rodar o projeto?

Siga os passos abaixo para subir todos os serviços corretamente:

### 📥 1. Clone o repositório

Abra um terminal e execute:

```bash
git clone https://github.com/GuilhermeBrga/NutriApp.git
```

### 🧱 2. Inicie o serviço de tradução (LibreTranslate)
Acesse a pasta do serviço:

```bash
cd NutriApp/libre_translate
```

Em seguida, execute um dos comandos abaixo:

```bash
docker compose up
// ou, se necessário: docker-compose up
```
O serviço será iniciado na porta 5000.

### 🌐 3. Inicie a API da aplicação
Abra outro terminal e execute:

```bash
cd NutriApp/api_nutri_app
lein ring server
```

### 🧠 4. Inicie a aplicação principal
Abra um novo terminal e execute:

```bash
cd NutriApp/nutri_app
lein run
```

### ✅ Tudo pronto!
Verifique seu saldo calórico ao longo dos dias!
