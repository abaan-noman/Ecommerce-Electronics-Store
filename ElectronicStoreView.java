import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.*;

public class ElectronicStoreView extends Pane{
    private ElectronicStore store;
    private TextField numberSalesField;
    private TextField revenueField;
    private TextField dollarSaleField;
    private ListView<String> popularItemsList;
    private ListView<String> storeStockList;
    private ListView<String> currentCartList;
    private Button resetStoreButton;
    private Button addToCartButton;
    private Button removeFromCartButton;
    private Button completeSaleButton;
    private Label currentCartLabel;
    public Button getResetStoreButton() { return resetStoreButton; }
    public Button getAddToCartButton() { return addToCartButton; }
    public Button getRemoveFromCartButton() { return removeFromCartButton; }
    public Button getCompleteSaleButton() { return completeSaleButton; }
    public TextField getNumberSalesField() { return numberSalesField; }
    public TextField getRevenueField() { return revenueField; }
    public TextField getDollarSaleField() { return dollarSaleField; }

    public ElectronicStoreView(ElectronicStore store) {
        this.store = store;
        Label storeSummaryLabel = new Label("Store Summary:");
        storeSummaryLabel.relocate(45, 10);
        Label numberSalesLabel = new Label("# Sales:");
        numberSalesLabel.relocate(35, 35);
        Label revenueLabel = new Label("Revenue:");
        revenueLabel.relocate(27, 65);
        Label dollarSaleLabel = new Label("$ / Sale:");
        dollarSaleLabel.relocate(32, 95);
        Label popularItemsLabel = new Label("Most Popular Items:");
        popularItemsLabel.relocate(35, 125);
        Label storeStockLabel = new Label("Store Stock:");
        storeStockLabel.relocate(300, 10);
        currentCartLabel = new Label("Current Cart: ($" + String.format("%.2f", store.getCurrentCartTotal()) + ")");
        currentCartLabel.relocate(593, 10);

        numberSalesField = new TextField(Integer.toString(store.getSales()));
        numberSalesField.relocate(80, 30);
        numberSalesField.setPrefSize(90, 20);
        revenueField = new TextField(String.format("%.2f", store.getRevenue()));
        revenueField.relocate(80, 60);
        revenueField.setPrefSize(90, 20);
        dollarSaleField = new TextField(String.format("%.2f", store.getRevenue()/store.getStock().length));
        dollarSaleField.relocate(80, 90);
        dollarSaleField.setPrefSize(90, 20);

        popularItemsList = new ListView<>();
        popularItemsList.relocate(10, 150);
        popularItemsList.setPrefSize(160, 165);
        storeStockList = new ListView<>();
        storeStockList.relocate(180, 30);
        storeStockList.setPrefSize(295, 285);
        currentCartList = new ListView<>();
        currentCartList.relocate(485, 30);
        currentCartList.setPrefSize(295, 285);

        resetStoreButton = new Button("Reset Store");
        resetStoreButton.relocate(30, 320);
        resetStoreButton.setPrefSize(120, 40);
        addToCartButton = new Button("Add to Cart");
        addToCartButton.relocate(270, 320);
        addToCartButton.setPrefSize(115, 40);
        addToCartButton.setDisable(true);
        removeFromCartButton = new Button("Remove from Cart");
        removeFromCartButton.relocate(485, 320);
        removeFromCartButton.setPrefSize(147.5, 40);
        removeFromCartButton.setDisable(true);
        completeSaleButton = new Button("Complete Sale");
        completeSaleButton.relocate(632.5, 320);
        completeSaleButton.setPrefSize(147.5, 40);
        completeSaleButton.setDisable(true);



        getChildren().addAll(storeSummaryLabel, numberSalesLabel, revenueLabel, dollarSaleLabel, popularItemsLabel,
                storeStockLabel, currentCartLabel, numberSalesField, revenueField, dollarSaleField, popularItemsList,
                storeStockList, currentCartList, resetStoreButton, addToCartButton, removeFromCartButton, completeSaleButton);

        setPrefSize(800, 400);

        storeStockList.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                String selectedItem = storeStockList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    addToCartButton.setDisable(false);
                } else {
                    addToCartButton.setDisable(true);
                }
            }
        });

        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String selectedItem = storeStockList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    for (Product product: store.getStock()) {
                        if (product != null && product.toString().equals(selectedItem)) {
                            store.addToCart(product);
                            product.sellUnits(1);
                            if (product.getStockQuantity() == 0) {
                                storeStockList.getItems().remove(selectedItem);
                            }
                        }
                    }
                    updateCurrentCartList();
                    updateCompleteSaleButton();
                }
            }
        });

        removeFromCartButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String selectedItem = currentCartList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String productName = selectedItem.split(" x ")[1];
                    for (Product product: store.getCurrentCart()) {
                        if (product != null && product.toString().equals(productName)) {
                            product.restockUnits(1);
                            product.returnUnits(1);
                            store.removeFromCart(product);
                            boolean alreadyInStock = false;
                            for (String i: storeStockList.getItems()) {
                                if (i.equals(product.toString())) {
                                    alreadyInStock = true;
                                    break;
                                }
                            }
                            if (!alreadyInStock) {
                                storeStockList.getItems().add(product.toString());
                            }
                            break;
                        }
                    }
                    updateCurrentCartList();
                    updateCompleteSaleButton();
                }
            }
        });

        currentCartList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                String selectedItem = currentCartList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    removeFromCartButton.setDisable(false);
                } else {
                    removeFromCartButton.setDisable(true);
                }
            }
        });

        completeSaleButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (!store.getCurrentCart().isEmpty()) {
                    double totalRevenue = store.getCurrentCartTotal();
                    int currentSales = Integer.parseInt(numberSalesField.getText());
                    numberSalesField.setText(Integer.toString(currentSales + 1));

                    double currentRevenue = Double.parseDouble(revenueField.getText());
                    double updatedRevenue = currentRevenue + totalRevenue;
                    revenueField.setText(String.format("%.2f", updatedRevenue));

                    double dollarSale = updatedRevenue/(currentSales + 1);
                    dollarSaleField.setText(String.format("%.2f", dollarSale));
                    store.getCurrentCart().clear();
                    updateCurrentCartList();
                    updateCompleteSaleButton();
                    showMostPopularItems();
                }
            }
        });

        resetStoreButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                store.resetStore();
                updateListViews();
            }
        });
    }

    public void addStoreStockList() {
        for (Product product: store.getStock()) {
            if (product != null) {
                storeStockList.getItems().add(product.toString());
            }
        }
    }

    public void showMostPopularItems() {
        List<Product> sortedItemsList = new ArrayList<>();
        for (Product product: store.getStock()) {
            if (product != null) {
                sortedItemsList.add(product);
            }
        }
        sortedItemsList.sort(Comparator.comparingInt(Product::getSoldQuantity).reversed());
        popularItemsList.getItems().clear();
        int count = 0;
        for (Product product: sortedItemsList) {
            if (product != null && product.getSoldQuantity() > 0) {
                popularItemsList.getItems().add(product.toString());
                count++;
            }
            if (count >= 3) {
                break;
            }
        }

        if (count < 3) {
            List<Product> allItems = new ArrayList<>();
            for (Product product: store.getStock()) {
                if (product != null && !popularItemsList.getItems().contains(product.toString())) {
                    allItems.add(product);
                }
            }
            Collections.shuffle(allItems);
            for (Product product: allItems) {
                if (count >= 3) {
                    break;
                }
                popularItemsList.getItems().add(product.toString());
                count++;
            }
        }
    }

    public void updateCurrentCartList() {
        currentCartList.getItems().clear();
        for (Product product: store.getCurrentCart()) {
            int count = Collections.frequency(store.getCurrentCart(), product);
            if (!currentCartList.getItems().contains(count + " x " + product.toString())) {
                currentCartList.getItems().add(count + " x " + product.toString());
            }
        }
        currentCartLabel.setText("Current Cart: ($" + String.format("%.2f", store.getCurrentCartTotal()) + ")");
    }

    public void updateCompleteSaleButton() {
        if (store.getCurrentCart().isEmpty()) {
            completeSaleButton.setDisable(true);
        } else {
            completeSaleButton.setDisable(false);
        }
    }

    public void updateListViews() {
        storeStockList.getItems().clear();
        currentCartList.getItems().clear();
        popularItemsList.getItems().clear();
        numberSalesField.setText(Integer.toString(store.getSales()));
        revenueField.setText(String.format("%.2f", store.getRevenue()));
        dollarSaleField.setText("0.00");
        addStoreStockList();
        showMostPopularItems();
        updateCurrentCartList();
        updateCompleteSaleButton();
    }
}
