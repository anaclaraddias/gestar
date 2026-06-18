package br.unibh.gestar;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.alert.MedicalPanel;
import br.unibh.gestar.interfaces.ApiServer;
import br.unibh.gestar.classification.ClassificationStrategy;
import br.unibh.gestar.classification.ClassificationStrategyFactory;
import br.unibh.gestar.classification.ProtocolType;
import br.unibh.gestar.infra.PostgresMedicalCareRepository;
import br.unibh.gestar.infra.PostgresPatientRepository;
import br.unibh.gestar.queue.QueueManager;
import br.unibh.gestar.repository.MedicalCareRepository;
import br.unibh.gestar.repository.PatientRepository;
import br.unibh.gestar.service.PatientService;
import br.unibh.gestar.service.TriageService;

public class Main {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        MedicalCareRepository repository = new PostgresMedicalCareRepository();
        PatientRepository patientRepository = new PostgresPatientRepository();

        QueueManager queue = new QueueManager();
        ClinicalNotifier notifier = new ClinicalNotifier();
        notifier.register(new MedicalPanel());
        ClassificationStrategy strategy = ClassificationStrategyFactory.create(ProtocolType.MANCHESTER);

        TriageService service = new TriageService(strategy, repository, queue, notifier);
        PatientService patientService = new PatientService(patientRepository);

        new ApiServer(service, patientService).start(port);
    }
}
