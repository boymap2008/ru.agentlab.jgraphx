/**
 *
 */
package ru.agentlab.jgraphx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.nodes.Connection;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;

/**
 *
 */
public class JGraphX
    extends Application {

    private static final Color FIGURE_STROKE_COLOR = Color.BLACK;
    private static final Color FIGURE_DEFAULT_FILL_COLOR = Color.BISQUE;
    private static final Color FIGURE_SELECTED_FILL_COLOR = Color.YELLOW;
    private static final int GRAPH_HEIGHT = 600;
    private static final int GRAPH_WIDTH = 800;
    private static final int OPTIONS_WIDTH = 250;
    private static final int FIGURE_SIZE = 40;
    private static final int INITIAL_X = GRAPH_WIDTH / 2 - FIGURE_SIZE / 2;
    private static final int INITIAL_Y = GRAPH_HEIGHT / 2 - FIGURE_SIZE / 2;

    private Group group;
    private List<Shape> selectedShapes;
    private TextField textField;


    public JGraphX() {
        selectedShapes = new ArrayList<>();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();
        Scene scene = new Scene(mainPane, GRAPH_WIDTH + OPTIONS_WIDTH, GRAPH_HEIGHT);
        primaryStage.setScene(scene);

        mainPane.setCenter(createGraph());
        mainPane.setRight(createOptions());

        primaryStage.setTitle("Graph");
        primaryStage.show();
    }

    private Node createGraph() {
        group = new Group();
        Shape background = new Rectangle(0, 0, GRAPH_WIDTH, GRAPH_HEIGHT);
        background.setFill(Color.ALICEBLUE);
        group.getChildren().add(background);
        fillGroup(group);

        return group;
    }

    private void fillGroup(Group group) {
        group.getChildren().add(createRectangle(GRAPH_WIDTH / 2 - FIGURE_SIZE, GRAPH_HEIGHT / 2));
        group.getChildren().add(createRectangle(GRAPH_WIDTH / 2 + FIGURE_SIZE, GRAPH_HEIGHT / 2));
    }

    private Node createRectangle(int x, int y) {
        final Shape rectangle = new Rectangle(0, 0, FIGURE_SIZE, FIGURE_SIZE);
        setInitialParams(x, y, rectangle);
        addMouseHandler(rectangle);

        return rectangle;

    }

    private Node createCircle(int x, int y) {
        final Shape circle = new Circle(FIGURE_SIZE / 2, FIGURE_SIZE / 2, FIGURE_SIZE / 2);
        setInitialParams(x, y, circle);
        addMouseHandler(circle);

        return circle;
    }

    private Node createTriangle(int x, int y) {
        final Polygon triangle = new Polygon();
        triangle.setTranslateY(FIGURE_SIZE);
        triangle.getPoints().addAll(0d, 0d, FIGURE_SIZE / 2d, -Double.valueOf(FIGURE_SIZE), Double.valueOf(FIGURE_SIZE),
            0d);

        setInitialParams(x, y, triangle);
        addMouseHandler(triangle);

        return triangle;
    }

    private Connection createConnection(Shape source, Shape target) {
        Connection connection = new Connection();
        connection.setEndDecoration(new ArrowHead());
        connection.setStartAnchor(new DynamicAnchor(source));
        connection.setEndAnchor(new DynamicAnchor(target));

        return connection;
    };

    private void setInitialParams(int x, int y, final Shape shape) {
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        shape.setStroke(FIGURE_STROKE_COLOR);
        shape.setFill(FIGURE_DEFAULT_FILL_COLOR);
    }

    private void addMouseHandler(final Shape shape) {
        EventHandler<MouseEvent> mouseHandler = new ShapeMouseEventHandler(shape);
        shape.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        shape.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        shape.addEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);
    }

    private TitledPane createOptions() {
        VBox vbox = new VBox();
        TitledPane options = new TitledPane("Options:", vbox);
        vbox.getChildren().addAll(createAddRectangleButton(), createAddCircleButton(), createAddTriangleButton(),
            createSelectedShapesText(), createAddConnectionButton(), createDeleteButton());

        return options;
    }

    private Button createAddRectangleButton() {
        Button addRectangleButton = new Button("Add rectangle");
        addRectangleButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                group.getChildren().add(createRectangle(INITIAL_X, INITIAL_Y));
            };
        });

        return addRectangleButton;
    }

    private Button createAddCircleButton() {
        Button addCircleButton = new Button("Add circle");
        addCircleButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                group.getChildren().add(createCircle(INITIAL_X, INITIAL_Y));
            };
        });

        return addCircleButton;
    }

    private Button createAddTriangleButton() {
        Button addTriangleButton = new Button("Add triangle");
        addTriangleButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                group.getChildren().add(createTriangle(INITIAL_X, INITIAL_Y));
            };
        });

        return addTriangleButton;
    }

    private Node createSelectedShapesText() {
        HBox selectedShapesRow = new HBox();
        textField = new TextField();
        selectedShapesRow.getChildren().addAll(new Label("SelectedShapes:"), textField);

        return selectedShapesRow;
    }

    private Button createAddConnectionButton() {
        Button addConnectionButton = new Button("Add connection");
        addConnectionButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                if (selectedShapes.size() == 2)
                {
                    Connection connection = createConnection(selectedShapes.get(0), selectedShapes.get(1));
                    group.getChildren().add(connection);
                }
                else
                {
                    Alert informationMessage = new Alert(AlertType.INFORMATION);
                    informationMessage.setTitle("Creating connection");
                    informationMessage.setHeaderText("2 shapes should be selected to create connection");
                    informationMessage.showAndWait();
                }
            }
        });

        return addConnectionButton;
    }

    private Button createDeleteButton() {
        Button deleteButton = new Button("Delete selected");
        deleteButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                for (Shape shape : selectedShapes)
                {
                    group.getChildren().remove(shape);
                    checkConnections(shape);
                }
            }

            private void checkConnections(Shape shape) {
                Iterator<Node> iterator = group.getChildren().iterator();
                while (iterator.hasNext())
                {
                    Node node = iterator.next();
                    if (node instanceof Connection)
                    {
                        Connection connection = (Connection)node;
                        if (shape.equals(connection.getStartAnchor().getAnchorage())
                            || shape.equals(connection.getEndAnchor().getAnchorage()))
                        {
                            iterator.remove();
                        }
                    }
                }
            }
        });

        return deleteButton;
    }

    private class ShapeMouseEventHandler
        implements EventHandler<MouseEvent> {
        private Shape shape;
        private double oldX;
        private double oldY;

        public ShapeMouseEventHandler(Shape shape) {
            this.shape = shape;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
            {
                handleMousePress(event);
            }
            else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
            {
                handleMouseDrag(event);
            }
            else if (event.getEventType() == MouseEvent.MOUSE_MOVED)
            {
                handleMouseMove(event);
            }
        }

        private void handleMouseDrag(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY)
            {
                double dx = event.getSceneX() - oldX;
                double dy = event.getSceneY() - oldY;

                shape.setLayoutX(shape.getLayoutX() + dx);
                shape.setLayoutY(shape.getLayoutY() + dy);

              /*  checkShapeLauout(shape);
*/
                oldX = event.getSceneX();
                oldY = event.getSceneY();
            }
        }

        private void handleMousePress(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY)
            {
                oldX = event.getSceneX();
                oldY = event.getSceneY();

                deselectShapes(selectedShapes);
                changeSelectedTextField();
            }
            else
            {
                if (!selectedShapes.contains(shape))
                {
                    selectShape(shape);
                    changeSelectedTextField();
                }
            }
        }

        private void changeSelectedTextField() {
            String newValue = "";
            for (Shape shape : selectedShapes)
            {
                newValue += getShapeType(shape) + ", ";
            }
            if (newValue.length() > 2)
            {
                newValue = newValue.substring(0, newValue.length() - 2);
            }

            textField.setText(newValue);
        }

        private String getShapeType(Shape shape) {
            return shape.getClass().getSimpleName();
        }

        private void handleMouseMove(MouseEvent event) {
            shape.toFront();
        }

        private void checkShapeLauout(Shape shape) {
            shape.setLayoutX(
                Math.max(0, Math.min(GRAPH_WIDTH - shape.getLayoutBounds().getWidth(), shape.getLayoutX())));
            shape.setLayoutY(
                Math.max(0, Math.min(GRAPH_HEIGHT - shape.getLayoutBounds().getHeight(), shape.getLayoutY())));
        }

        private void selectShape(Shape shape) {
            selectedShapes.add(shape);
            shape.setFill(FIGURE_SELECTED_FILL_COLOR);
        }

        private void deselectShapes(List<Shape> selectedShapes) {
            for (Shape selectedShape : selectedShapes)
            {
                selectedShape.setFill(FIGURE_DEFAULT_FILL_COLOR);
            }
            selectedShapes.clear();
        }
    }

    public static class ArrowHead
        extends Polygon {
        public ArrowHead() {
            super(0, 0, 10, 3, 10, -3);
            setFill(Color.BLACK);
            setStroke(Color.BLACK);
            setStrokeLineJoin(StrokeLineJoin.ROUND);
        }
    }
}
