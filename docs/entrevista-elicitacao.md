# Registro de Elicitação de Requisitos (Entrevista)

## Identificação

- **Técnica:** entrevista semiestruturada (assíncrona, por mensagens).
- **Entrevistada:** Camila, enfermeira plantonista no Pronto Atendimento adulto do
  Hospital Felício Rocho (Belo Horizonte/MG), atuando diretamente na classificação
  de risco; mestranda na área da saúde.
- **Data:** junho/2026.
- **Objetivo:** compreender o processo real de triagem e fila de atendimento,
  validar as premissas do projeto e identificar novos requisitos.

> A escolha de uma profissional que executa a classificação de risco no dia a dia
> torna a elicitação primária (fonte direta), e não apenas documental.

## Perfil da unidade

O Hospital Felício Rocho é um hospital geral de alta complexidade, com PA adulto 24h.
Nem todos os casos são atendidos no PA: **pacientes psiquiátricos, gestantes/queixas
ginecológicas e pacientes do SUS** (atendidos apenas quando transplantados) **não são
aceitos**, pois a unidade não dispõe de obstetra/ginecologista, psiquiatra e psicólogo
no PA. Esses casos são **referenciados a outras unidades** (ex.: Odete Valadares, UPA,
André Luiz, ou unidade Unimed com gineco/obstetra).

> Isso responde diretamente à dúvida de escopo sobre "o que desclassifica um paciente
> na triagem": existe uma **regra de elegibilidade** anterior à classificação de risco.

## Fluxo atual do processo

1. Paciente chega ao PA e **retira a senha**.
2. Uma **técnica de enfermagem** coleta a **queixa principal**.
3. Paciente aguarda o **enfermeiro** chamar para a **classificação de risco**.
4. Retorna à recepção e aguarda o **guichê** chamar pelo nome para o **cadastro**.
5. Aguarda o **médico** chamar para atendimento.

> Observação relevante: o **cadastro ocorre após a classificação de risco**, e a
> coleta da queixa (técnica) é uma etapa distinta da classificação (enfermeiro).

## Achados por tema

**Protocolo de classificação**
- Utilizam o **Protocolo de Manchester**, obrigatório; o COREN exige certificação do
  enfermeiro para classificar risco. *Valida a estratégia `ManchesterClassification`.*
- A classificação não é um simples limite sobre sinais vitais: parte da **queixa
  principal** → abre o **fluxograma** correspondente → avalia os **discriminadores**;
  cada discriminador alterado define a **cor**. Sinais usados na maioria dos casos:
  pressão arterial, frequência cardíaca, frequência respiratória e temperatura
  (glicemia só em fluxogramas específicos; dor torácica não solicita pressão arterial).

**Cores e tempos de atendimento**
- Verde: pouco urgente, até 120 min
- Amarelo: urgente, até 60 min
- Laranja: muito urgente, até 10 min
- Vermelho: emergência, imediato
- *(A unidade não utiliza a cor Azul do Manchester padrão.)*

**Quem faz**
- Apenas o **enfermeiro** classifica o risco; a **técnica** somente coleta a queixa.

**Sistemas (confirma a fragmentação descrita no problema)**
- A classificação é feita no sistema **MV PEP** (específico da triagem); o papel é só
  plano de contingência. Esse sistema mostra as senhas aguardando classificação e as
  classificações já feitas, mas **não mostra o histórico do paciente**.
- Para ver o histórico, é preciso abrir **outro sistema (SOUL MV)** e buscar pelo nome.
- Ao finalizar, a triagem vira "Prontuário triagem", visível a quem acessar o prontuário.
- *Valida o problema central: informação fragmentada entre sistemas distintos.*

**Ordem de atendimento e desempate**
- A ordem é pela **cor** (gravidade), não por chegada.
- **Dentro da mesma cor não é puro FIFO:** o **idoso** tende a ser chamado primeiro
  (prioridade legal). *Corrige a premissa de FIFO simples.*
- Pacientes urgentes têm a ficha entregue diretamente ao médico, para não estourar o
  tempo-alvo.

**Reavaliação**
- Paciente que **piora** é reavaliado (novos sinais vitais) e pode ser reclassificado.
- **Regra importante:** a avaliação **não regride**, só pode subir de urgência. Um
  Verde que piora vira Amarelo; um Amarelo que melhora **não** volta para Verde.
  *Nova regra de negócio.*

**Falha real observada (perda de informação / concorrência)**
- O painel exibe as **mesmas senhas para os dois enfermeiros** que triam ao mesmo
  tempo. Em um caso, uma enfermeira **excluiu** uma senha que a outra já atendia; o
  paciente ficou ~2h "fora do sistema" aguardando, e precisou ser reclassificado de
  Verde para Amarelo por causa do erro. *Indica necessidade de "travar" o
  atendimento em triagem para um único profissional.*

**Gargalo de recurso**
- Demora para os Verdes por limitação de especialistas (muita dor torácica e apenas 1
  cardiologista; ~30 min por consulta). *Contexto do problema; não é foco da fatia.*

**Desejos da usuária (wishlist)**
- Fixar **doenças de base** na aba da triagem (DPOC, IAM/ano, insuficiência cardíaca,
  AVC/ano, câncer), hoje só fixa alergias. Motivo: interpretar o **basal** do paciente.
  Ex.: idoso com DPOC chega com saturação 89% (que é o basal dele), mas o sistema
  obriga classificar como Laranja abaixo de 91%; muitos vêm de lar de repouso sem
  informação clínica. *Reforça o valor de "acesso ao histórico na triagem".*
- Acessibilidade para pacientes surdos/mudos (intérprete de Libras via aplicativo).

## Protocolos e fluxogramas (complemento por áudio)

**Papel da técnica na porta.** Quando não há paciente crítico, a técnica de enfermagem
fica na entrada e faz a abordagem inicial da queixa, atenta a sinais de alerta. Ex.:
paciente relata paresia apenas do lado direito, com início há menos de 24h → suspeita de
AVC → a técnica leva direto para a triagem.

**Protocolo de AVC.** Confirmada a suspeita, o neurologista avalia; se confirmado, aciona-se
o protocolo de AVC (acionamento específico no sistema) e o paciente entra como **grande
urgência**, pois exige conduta imediata (dois acessos, estabilização, tomografia) com meta
de cerca de 1 hora.

**Dor torácica / IAM.** Dor torácica é **sempre classificada como urgente**, pois pode
evoluir rápido. Solicita-se um **eletrocardiograma** e o cardiologista avalia. Da triagem
até a avaliação do eletro há uma janela de cerca de **10 minutos**; havendo alteração,
abre-se o **protocolo de infarto**. Sem alteração, o paciente segue o fluxo normal
(recepção → ficha → aguardar cardiologista).

> **Leitura para o projeto:** a classificação Manchester opera por **fluxograma da queixa
> + discriminadores**, e **certas queixas impõem urgência mínima** (dor torácica ≥ urgente)
> independentemente dos sinais vitais. Os **protocolos especiais** (AVC, IAM) são fluxos
> clínicos com metas de tempo, relevantes como regra de negócio, mas a orquestração
> clínica em si fica **fora da fatia implementada**.

## Requisitos e regras derivados da entrevista

| Origem | Item | Situação na fatia |
|--------|------|-------------------|
| Elegibilidade | Triagem deve verificar se o caso é atendido pela unidade; se não, registrar **encaminhamento** | Recomendado (baixo custo) |
| Desempate | Fila deve priorizar **idoso** dentro da mesma cor, antes da ordem de chegada | Implementar |
| Reavaliação | Reclassificação **monotônica** (só aumenta a urgência) | Implementar |
| Classificação | Discriminadores que impõem **urgência mínima** por queixa (ex.: dor torácica ≥ urgente) | Implementar (enriquece a Strategy) |
| Protocolos | Acionamento de **protocolos especiais** com metas de tempo (AVC, IAM) | Documentar (futuro) |
| Concorrência | Atendimento em triagem deve ser **exclusivo** de um profissional (evitar exclusão/duplicidade) | Documentar (futuro) |
| Histórico | Exibir **doenças de base** na triagem para interpretar o basal | Documentar (futuro) |
| Acessibilidade | Suporte a comunicação em Libras | Documentar (futuro) |

## Confirmações da entrevistada (segunda rodada)

1. **Cor Azul:** a unidade **não usa** a cor Azul, trabalha apenas com Verde, Amarelo,
   Laranja e Vermelho.
2. **Desempate:** a senha já é tirada por categoria: **normal**, **preferencial** (PCD e
   idoso 60+) e **prioridade máxima** (idoso 80+). Essas categorias são usadas no desempate
   dentro da mesma cor.
3. **Fluxo confirmado:** tira senha → triagem (classificação) → faz a ficha (cadastro) →
   consultório médico.
4. **Encaminhamento (com registro):** o paciente não atendido **é registrado**: na triagem
   digitam tudo (motivo, sinais vitais e para qual hospital foi referenciado), e o médico da
   grande urgência **carimba** a folha, que é guardada, "porque sempre volta paciente com
   processo". Pacientes do SUS dependem de **autorização da coordenação médica** (ex.:
   transplantados). *Exemplo real relatado:* paciente com PA 210x50 cujo plano não cobria a
   unidade, e foi orientado a pagar particular ou procurar outra unidade; tudo foi registrado,
   incluindo os sinais vitais e a passagem do caso ao médico da grande urgência.

> **Implicação para o modelo:** "encaminhamento" não é apenas "não entrar na fila", e sim uma
> **disposição registrada** (motivo, sinais vitais, unidade de destino, profissional). Isso
> enriquece o estado `ENCAMINHADO` do atendimento.
