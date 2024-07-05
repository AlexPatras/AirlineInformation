module persistence_module {
    requires java.logging;
    requires java.sql;
    requires java.naming;
    requires org.postgresql.jdbc;
    requires datarecords_module;
    exports persistence;
}
