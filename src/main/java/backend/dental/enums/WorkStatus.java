package backend.dental.enums;

public enum WorkStatus {Control("Kontrol"),
    Protez("Protez"),
    Alci("Alci"),
    AltYapi("AltYapi"),
    Zirkonyum("Zirkonyum"),
    Opak("Opak"),
    MetalTesfiye("MetalTesfiye"),
    Prova("Prova"),
    Teslim("Teslim"),
    Glaze("Glaze"),
    Cila("Cila"),
    AltYapiMetalKaide("AltYapiMetalKaide"),
    MuflaDisDizim("MuflaDisDizim"),
    Porselen("Porselen");


    private final String value;
    WorkStatus(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
    }








