#  Estrutura do Projeto

Este projeto segue uma arquitetura organizada em camadas.

## Estrutura de Pastas

### **config**
Contém todas as **configurações da aplicação**, incluindo:
- Segurança
- WebClient
- Beans

### **controller**
Camada REST da aplicação.  
Responsável por **receber requisições HTTP** e retornar respostas.
> ️ **Importante:** Não coloque regra de negócio aqui.

### **service**
Contém a **regra de negócio** da aplicação.  
Aqui é onde todas as regras e validações devem ocorrer.

### **repository**
Interface JPA para acesso a dados.

### **model**
Contém as **entidades do banco de dados** anotadas com `@Entity`.

### **dto**
Objetos de transporte (**Data Transfer Objects**).  
Usados para **evitar expor diretamente as entidades** nas respostas da API.

### **client**
Integrações externas, como chamadas a APIs de terceiros (ex: API da Blizzard).

### **exception**
Tratamento global de erros da aplicação.

### **util**
Helpers e conversores usados em toda a aplicação.

---
