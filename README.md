# NutriApp

Aplicativo para gerenciamento e análise nutricional com suporte a tradução automática via LibreTranslate.

---

## 🛠 Requisitos

Para executar este projeto em máquinas **Windows**, você precisará ter:

- [WSL2 (Windows Subsystem for Linux 2)](https://learn.microsoft.com/pt-br/windows/wsl/install)
- [Docker Desktop com suporte ao WSL2](https://www.docker.com/products/docker-desktop/)

Além disso, é necessário ter as chaves de API, que podem ser encontradas nos sites:
- [USDA FoodData Central API](https://fdc.nal.usda.gov/api-guide)
- [Calories Burned API](https://api-ninjas.com/api/caloriesburned)

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
O serviço será iniciado na porta 5000. Teste-o com a url:
```bash
http://localhost:5000/languages
```

### 🌐 3. Inicie a API da aplicação
Abra outro terminal e crie o arquivo **.lein-env** colocando o seguinte:
```bash
{
 :usda-api-key "SUA-CHAVE-API-1"
 :ninjas-api-key "SUA-CHAVE-API-2"
}
```

Salve e execute no terminal:
```bash
cd NutriApp/api_nutri_app
lein ring server
```
O serviço será iniciado na porta 3000. Teste-o com a url:
```bash
http://localhost:3000/
```


### 🧠 4. Inicie a aplicação principal
Abra um novo terminal e execute:

```bash
cd NutriApp/nutri_app
lein run
```

### ✅ Tudo pronto!
Verifique seu saldo calórico ao longo dos dias!
