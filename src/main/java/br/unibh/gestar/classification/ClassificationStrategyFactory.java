package br.unibh.gestar.classification;

public class ClassificationStrategyFactory {
    public static ClassificationStrategy create(ProtocolType type) {
        return switch (type) {
            case MANCHESTER -> new ManchesterClassification();
            case SIMPLE -> new SimpleClassification();
        };
    }
}
