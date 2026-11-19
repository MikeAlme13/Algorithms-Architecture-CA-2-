import controller.StoreController;
import model.EmployeeRepository;
import view.ConsoleView;

/**
 * Application entry point.
 *
 * Main has exactly one job: create the three MVC parts and connect them. The
 * model does not know the view, the view does not know the model, and only this
 * class knows all three. That is the wiring point of the MVC pattern.
 *
 * CCT College Dublin - BSc (Hons) in Computing
 * Algorithms & Architecture - CA2 - Grafton & Hughes Department Store
 *
 * Usage:
 *   java -cp out Main
 *   java -cp out Main data/Applicants_Form.txt
 */
public class Main {

    public static void main(String[] args) {
        EmployeeRepository repository = new EmployeeRepository();   // MODEL
        ConsoleView view = new ConsoleView();                       // VIEW
        StoreController controller = new StoreController(repository, view);  // CONTROLLER

        String fileNameFromArguments = (args.length > 0) ? args[0] : null;
        controller.run(fileNameFromArguments);
    }
}
