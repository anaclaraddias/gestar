# Gestar

### Problema identificado
Hospitais e unidades de saúde pública enfrentam problemas organizacionais ligados a triagem e filas de atendimento, para gerenciar um alto volume de pacientes com necessidades distintas de urgência, especialidade e continuidade de tratamento. Grande parte desse gerenciamento acontece de forma fragmentada, em diversos sistemas que normalmente são legado ou de forma manual, sem integração direta entre todas as etapas.

### Usuários envolvidos
- Médico
- Profissional de triagem
- Paciente

### Dificuldades que existem atualmente
- Sistemas parcialmente digitalizados
- Gerência ineficiente em grande quantidade de pacientes
- Organização de prioridades e urgências
- Perda de informação
- Demora no atendimento

### Como a solução proposta poderá melhorar o processo.
Gestar é um sistema unificado e escalável que foca em gerenciar filas, triagens e exames. Contendo: triagem digital com acesso ao prontuário, fila priorizada por urgência clínica, alertas clínicos e histórico na tela do atendimento. Agilizando o atendimento, unindo informações e processos, e organizando filas.

Participantes:
- Ana Clara Domingos Dias Silva - 12316965
- Samuel Zappala Batista - 12411504
- Gabriel Victor Dornelas Ferreira Sathler - 12319216
- Larissa Antunes Corrêa Morais - 1242021588

### Como executar o projeto

#### Pré-requisitos
- Java 17
- Maven 3.9+
- Docker e Docker Compose

#### Subir o banco de dados
O projeto usa PostgreSQL via `docker-compose.yml`.

```bash
docker compose up -d
```

Isso cria o banco `gestar_db` com o usuário `admin` e senha `1234`.

#### Executar a aplicação
Com o banco rodando, inicie a API com:

```bash
mvn compile exec:java
```

A aplicação sobe na porta `8080` por padrão.

Se quiser usar outra porta:

```bash
mvn compile exec:java -Dexec.args="8081"
```

#### Verificar se está funcionando
Rota de health check:

```bash
curl http://localhost:8080/health
```

Se estiver tudo certo, a API responde com sucesso e você já pode usar as rotas de `patient`, `medical-care`, `referral` e `queue`.

### Rotas da API

#### `GET /health`
Verifica se a API e o banco estão disponíveis.

```bash
curl http://localhost:8080/health
```

Resposta esperada:
```json
{"database_status":"UP"}
```

#### `POST /patient`
Cria um paciente novo ou atualiza um paciente existente com o mesmo `name + birthDate`.

```bash
curl -X POST http://localhost:8080/patient \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "birthDate": "1990-05-12"
  }'
```

#### `GET /patients`
Lista todos os pacientes cadastrados.

```bash
curl http://localhost:8080/patients
```

#### `POST /medical-care`
Cria um atendimento de medical care, faz a classificação e envia o paciente para a fila.

```bash
curl -X POST http://localhost:8080/medical-care \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "birthDate": "1990-05-12",
    "complaint": "Dor no peito",
    "category": "ADULT",
    "systolic": 140,
    "diastolic": 90,
    "heartRate": 110,
    "respiratoryRate": 22,
    "temperature": 37.8,
    "spo2": 96,
    "pain": 8
  }'
```

#### `GET /medical-care`
Lista todos os atendimentos salvos.

```bash
curl http://localhost:8080/medical-care
```

#### `GET /medical-care/{id}`
Busca um atendimento específico pelo `id`.

```bash
curl http://localhost:8080/medical-care/ID_AQUI
```

#### `PATCH /medical-care/{id}`
Atualiza um atendimento. Você pode:
- finalizar o atendimento com `status: FINISHED`
- ou reclassificar enviando novos sinais vitais

Finalizar:
```bash
curl -X PATCH http://localhost:8080/medical-care/ID_AQUI \
  -H "Content-Type: application/json" \
  -d '{
    "status": "FINISHED"
  }'
```

Reclassificar:
```bash
curl -X PATCH http://localhost:8080/medical-care/ID_AQUI \
  -H "Content-Type: application/json" \
  -d '{
    "systolic": 160,
    "diastolic": 100,
    "heartRate": 120,
    "respiratoryRate": 24,
    "temperature": 38.5,
    "spo2": 94,
    "pain": 9
  }'
```

#### `POST /referral`
Registra um encaminhamento de atendimento para outra unidade.

```bash
curl -X POST http://localhost:8080/referral \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "birthDate": "1990-05-12",
    "complaint": "Dor persistente",
    "category": "ADULT",
    "referralReason": "Necessita avaliação especializada",
    "destinationUnit": "Cardiologia"
  }'
```

#### `GET /queue`
Mostra o estado atual da fila de atendimento.

```bash
curl http://localhost:8080/queue
```

#### `POST /queue/call`
Chama o próximo paciente da fila para atendimento.

```bash
curl -X POST http://localhost:8080/queue/call
```
