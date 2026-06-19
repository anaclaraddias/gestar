# Documento de Requisitos do Gestar

**Sistema de Triagem e Fila Priorizada de Atendimento**
Unidade Curricular: Modelos, Métodos e Técnicas de Engenharia de Software, UniBH

---

## 1. Contexto e Problema

Hospitais e unidades de saúde enfrentam dificuldades organizacionais ligadas à
triagem e às filas de atendimento ao lidar com alto volume de pacientes com
necessidades distintas de urgência. Boa parte desse gerenciamento ocorre de forma
fragmentada, em sistemas distintos que não se integram.

Esse cenário foi confirmado em entrevista com uma enfermeira de pronto atendimento
(ver `entrevista-elicitacao.md`): na prática, a classificação de risco é feita em um
sistema (MV PEP) que **não exibe o histórico do paciente**, exigindo abrir um segundo
sistema (SOUL MV) para consultá-lo. A informação se perde entre etapas e sistemas, há
risco de duplicidade no painel de triagem e a priorização inadequada tem impacto
clínico direto.

O **Gestar** propõe uma triagem digital com classificação de risco, fila priorizada
por urgência clínica e alertas para casos críticos, integrando informações que hoje
ficam dispersas.

## 2. Escopo do Trabalho

Conforme orientação da disciplina, **não se pretende implementar a solução completa**.
Delimita-se uma fatia coesa e relevante:

**Dentro do escopo (implementado):**

- Cadastro de paciente para atendimento.
- Triagem digital com **classificação de risco** pelo Protocolo de Manchester.
- **Verificação de elegibilidade**: casos não atendidos pela unidade são
  **encaminhados** em vez de entrarem na fila.
- **Fila priorizada** por urgência clínica, com **prioridade ao idoso** dentro da
  mesma cor e, por fim, ordem de chegada.
- **Reavaliação/reclassificação** de pacientes que pioram (regra monotônica).
- Controle do **ciclo de vida do atendimento** (estados).
- **Alertas clínicos** para casos críticos.
- **Exposição das operações por uma API REST** (triagem, fila, encaminhamento).
- **Persistência em PostgreSQL**, mantendo a fila de prioridade em memória.

**Fora do escopo (trabalhos futuros, levantados na entrevista):**

- Exclusividade do atendimento em triagem (evitar duplicidade entre profissionais).
- Protocolos especiais com metas de tempo (AVC, IAM) e orquestração de condutas.
- Fixação de doenças de base na triagem para interpretar o basal do paciente.
- Acessibilidade para pacientes surdos/mudos (Libras).
- Gestão de exames, prontuário longitudinal completo, integração com sistemas legados
  e interface gráfica (front-end).

> **Justificativa e evolução do recorte:** a triagem com fila priorizada é o núcleo
> diferenciador do Gestar e a fatia que melhor exercita os conceitos avaliados (OO,
> SOLID, padrões e testes). A abstração da persistência por contrato (RNF04) permitiu,
> durante a implementação, adotar **PostgreSQL** e expor o fluxo por uma **API REST**
> (Javalin), sem alterar a regra de negócio. A fila de prioridade permanece em memória.

## 3. Atores

| Ator | Descrição |
|------|-----------|
| **Técnica de Enfermagem** | Coleta a queixa principal na chegada do paciente. |
| **Enfermeiro de Triagem** | Realiza a classificação de risco pelo Protocolo de Manchester (atividade privativa do enfermeiro). |
| **Recepção** | Realiza o cadastro do paciente após a classificação. |
| **Médico** | Consulta a fila priorizada, chama o próximo paciente e conduz o atendimento. |
| **Paciente** | Pessoa que busca atendimento. |

> **Correção em relação à versão inicial:** "Triagem" não é um ator, e sim um processo.
> A entrevista revelou ainda que coleta de queixa (técnica) e classificação de risco
> (enfermeiro) são atividades de atores distintos.

## 4. Levantamento de Requisitos (Elicitação)

### 4.1 Técnicas utilizadas

- **Entrevista com profissional da área** (fonte primária): enfermeira plantonista do
  PA do Hospital Felício Rocho, que executa a classificação de risco. Registro completo
  em `entrevista-elicitacao.md`.
- **Análise documental:** Protocolo de Manchester e diretrizes de Acolhimento com
  Classificação de Risco.

### 4.2 Principais achados (resumo; detalhe completo em `entrevista-elicitacao.md`)

- A priorização é por gravidade (cor), não por ordem de chegada. **Confirma a fila.**
- O protocolo usado é o **Manchester**: queixa, fluxograma, discriminadores, cor.
  **Confirma a estratégia de classificação.**
- Dentro da mesma cor, **idoso tem prioridade**, não é FIFO puro. **Corrige a fila.**
- A reclassificação **não regride**, só aumenta a urgência. **Nova regra.**
- Há casos **não aceitos** pela unidade, que são **encaminhados**. **Novo requisito.**
- Sistemas fragmentados (triagem x histórico em sistemas diferentes). **Confirma o problema.**

## 5. Fluxo do Processo (baseado na entrevista)

`Senha, Queixa (técnica), Classificação de risco (enfermeiro), Cadastro (recepção),
Fila priorizada, Atendimento (médico)`

Casos não elegíveis (ex.: obstétricos, psiquiátricos) são **encaminhados** a outra
unidade na etapa de triagem, não entrando na fila.

## 6. Requisitos Funcionais

| ID | Requisito |
|----|-----------|
| RF01 | Cadastrar um paciente para atendimento. |
| RF02 | Registrar os dados de triagem (queixa principal e sinais vitais). |
| RF03 | Classificar o risco segundo o protocolo configurado (Manchester), atribuindo uma cor. |
| RF04 | Verificar a elegibilidade do caso; se não atendido pela unidade, registrar **encaminhamento** em vez de enfileirar. |
| RF05 | Inserir o atendimento elegível em uma fila priorizada pela cor. |
| RF06 | Ordenar, dentro da mesma cor, por **categoria de prioridade** (idoso 80+ > PCD/idoso 60+ > normal) e, depois, por **ordem de chegada**. |
| RF07 | Consultar/chamar o próximo paciente a ser atendido. |
| RF08 | Reavaliar um paciente e **reclassificá-lo apenas para maior urgência** (nunca menor). |
| RF09 | Controlar o estado do atendimento (aguardando, em triagem, na fila, em atendimento, finalizado, encaminhado). |
| RF10 | Emitir alerta clínico quando o atendimento for classificado como Vermelho (emergência). |
| RF11 | Expor as operações de triagem, fila e encaminhamento por uma API REST. |

## 7. Requisitos Não-Funcionais

| ID | Requisito |
|----|-----------|
| RNF01 | O protocolo de classificação deve ser substituível sem alterar a lógica da fila. |
| RNF02 | Solução em Java seguindo os princípios SOLID. |
| RNF03 | Regras de negócio principais (classificação, ordenação, reclassificação) cobertas por testes unitários. |
| RNF04 | Persistência abstraída por contrato (`MedicalCareRepositoryContract`), realizada com PostgreSQL sem impactar a regra de negócio. |

## 8. Casos de Uso

```
Técnica de Enfermagem --> (UC01 Registrar queixa)
Enfermeiro --> (UC02 Classificar risco)
                    | «include» -> (UC03 Verificar elegibilidade)
                    | «extend» (não elegível) -> (UC04 Encaminhar paciente)
                    | «extend» (elegível) -> (UC05 Inserir na fila priorizada)
                    | «extend» (Vermelho) -> (UC06 Emitir alerta clínico)
Enfermeiro --> (UC07 Reavaliar e reclassificar)
Médico --> (UC08 Chamar próximo paciente)
Médico --> (UC09 Finalizar atendimento)
```

### UC02: Classificar risco
Enfermeiro registra sinais, o sistema aplica o protocolo, atribui a cor, verifica
elegibilidade (UC03) e enfileira (UC05) ou encaminha (UC04).

### UC04: Encaminhar paciente
Caso o caso não seja atendido pela unidade, o atendimento recebe estado "encaminhado"
e não entra na fila.

### UC07: Reavaliar e reclassificar
Diante de piora, novos sinais são aferidos; a urgência só pode aumentar (RN05).

### UC08: Chamar próximo paciente
Sistema retorna o atendimento de maior prioridade (cor, depois idoso, depois chegada)
e o move para "em atendimento".

## 9. Regras de Negócio

| ID | Regra |
|----|-------|
| RN01 | Ordem de prioridade das cores: Vermelho > Laranja > Amarelo > Verde. A unidade **não utiliza** a cor Azul. |
| RN02 | Tempos-alvo: Vermelho imediato; Laranja 10 min; Amarelo 60 min; Verde 120 min. |
| RN03 | Dentro da mesma cor, a ordem segue a **categoria de prioridade** da senha, sendo prioridade máxima para idoso 80+, depois preferencial (PCD e idoso 60+) e, por último, normal; havendo empate, vale a ordem de chegada. |
| RN04 | Estados seguem sequência válida (não se atende quem não foi triado). |
| RN05 | Reclassificação é monotônica: a urgência só aumenta, nunca diminui. |
| RN06 | Casos não atendidos pela unidade (ex.: obstétricos, psiquiátricos) são encaminhados, não enfileirados. O encaminhamento é **registrado** (motivo, sinais vitais, unidade de destino, profissional responsável) por rastreabilidade legal. Pacientes do SUS dependem de **autorização da coordenação médica** (ex.: transplantados). |
| RN07 | Classificação Vermelho dispara alerta clínico imediato. |
| RN08 | Discriminadores podem impor urgência mínima por queixa, independentemente dos sinais vitais (ex.: dor torácica é, no mínimo, urgente). |

## 10. Rastreabilidade

| RF | Caso de Uso | Padrão/Componente (código em inglês) |
|----|-------------|--------------------------------------|
| RF03 | UC02 | *Strategy* (`ClassificationStrategy`, `ManchesterClassification`) |
| RF04 | UC03, UC04 | Regra de elegibilidade + estado `REFERRED` (`MedicalCare.markReferred`) |
| RF05, RF06, RF07 | UC05, UC08 | `QueueManager` (PriorityQueue + Comparator cor, categoria, chegada) |
| RF08 | UC07 | Reclassificação monotônica (`MedicalCare.reclassify`, RN05) |
| RF09 | UC04, UC08, UC09 | Estados do atendimento (enum `MedicalCareStatus`) |
| RF10 | UC06 | *Observer* (`ClinicalNotifier`, `AlertObserver`) |
| RNF04 | (geral) | *Repository* (`MedicalCareRepositoryContract`, implementado por `PostgresMedicalCareRepository`) |
| RF01, RF02 | UC01, UC02 | *Factory Method* (`ClassificationStrategyFactory`); orquestração em `MedicalCareService` |
| RF11 | UC02, UC08 | API REST (`ApiServer`, `MedicalCareController`) |

---

*Documento atualizado após a entrevista de elicitação e a evolução da implementação (junho/2026).*
