package businessLogic;

import persistence.PersistenceAPI;

public interface BusinessLogicFactory {
    static BusinessLogicAPI getImplementation(PersistenceAPI persistenceAPI) {
        return new BusinessLogicAPIImpl(persistenceAPI);
    }
}
