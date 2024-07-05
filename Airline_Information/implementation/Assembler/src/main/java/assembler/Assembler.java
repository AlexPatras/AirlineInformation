package assembler;

import businessLogic.BusinessLogicAPI;
import businessLogic.BusinessLogicFactory;
import gui.GUIApp;
import persistence.PersistenceAPI;
import persistence.PersistenceFactory;

public class Assembler {
    public static void main(String[] args) {
        PersistenceAPI persistenceAPI = PersistenceFactory.getImplementation();
        BusinessLogicAPI businessLogicAPI = BusinessLogicFactory.getImplementation(persistenceAPI);

        new GUIApp(businessLogicAPI).show();
    }
}