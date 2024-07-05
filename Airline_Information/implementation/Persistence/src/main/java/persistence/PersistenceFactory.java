package persistence;

public interface PersistenceFactory {

    static PersistenceAPI getImplementation() {
        return new PersistenceAPIImpl();
    }

}
