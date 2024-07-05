module businesslogic_module {
    requires transitive datarecords_module;
    requires transitive persistence_module;
    requires java.sql;
    requires com.google.gson;

    exports businessLogic;
}