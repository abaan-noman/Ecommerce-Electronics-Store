import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ElectronicStoreApp extends Application {

    public void start(Stage primaryStage) {
        ElectronicStore store = ElectronicStore.createStore();
        ElectronicStoreView storeView = new ElectronicStoreView(store);

        Scene scene = new Scene(storeView, 800, 400);
        primaryStage.setTitle("Electronic Store Application - " + store.getName());
        primaryStage.setScene(scene);

        storeView.addStoreStockList();
        storeView.showMostPopularItems();
        storeView.updateCompleteSaleButton();
        storeView.updateCurrentCartList();

        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
