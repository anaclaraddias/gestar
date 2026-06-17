package br.unibh.gestar;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.alert.MedicalPanel;
import br.unibh.gestar.interfaces.ApiServer;
import br.unibh.gestar.classification.ClassificationStrategy;
import br.unibh.gestar.classification.ClassificationStrategyFactory;
import br.unibh.gestar.classification.ProtocolType;
import br.unibh.gestar.infra.PostgresMedicalCareRepository;
import br.unibh.gestar.queue.QueueManager;
import br.unibh.gestar.repository.MedicalCareRepository;
import br.unibh.gestar.service.TriageService;

/**
 * Entry point of Gestar: wires the dependencies and starts the Javalin HTTP API
 * that exposes the triage and prioritized-queue flow. Persistence is Postgres
 * (run db/schema.sql once); the queue lives in memory.
 *
 * Routes:
 *   POST   /triages           perform triage of an eligible patient
 *   POST   /referrals         register a referred (non-eligible) patient
 *   GET    /triages           list all medical care
 *   GET    /triages/{id}      get one medical care
 *   PATCH  /triages/{id}      update: new vital signs reclassify (RN05);
 *                             {"status":"FINISHED"} finishes it
 *   GET    /queue             queue sizes per color + who is next
 *   POST   /queue/calls       call the next patient
 */
public class Main {

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        MedicalCareRepository repository = new PostgresMedicalCareRepository();
        QueueManager queue = new QueueManager();
        ClinicalNotifier notifier = new ClinicalNotifier();
        notifier.register(new MedicalPanel());
        ClassificationStrategy strategy = ClassificationStrategyFactory.create(ProtocolType.MANCHESTER);

        TriageService service = new TriageService(strategy, repository, queue, notifier);

        new ApiServer(service).start(port);
    }
}
