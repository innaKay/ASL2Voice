package de.ruzman.demo;

//import asl.MainController;
//import asl.TrainingController;
import asl.MainController;
import com.leapmotion.leap.Controller;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.LeapAppBuilder;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;
import de.ruzman.leap.fx.HandFX3D;
import java.awt.TextField;
import java.awt.Toolkit;
import java.io.File;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;

public class HandTrackingApp extends Application implements PointMotionListener {	
	private Group group;
	private Map<Integer, HandFX3D> hands;
        private MainController mainController;
        private Scene scene;

	public static void main(String[] args) {//                rX.setAxis(Rotate.X_AXIS);
//                Controller controller = new Controller();
//                controller.getController().setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
                
                LeapApp app = new LeapAppBuilder().initLeapApp();
                app.getController().setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
                //Controllers
                mainController = new MainController();
                               
                //Visualizer group
		group = new Group();               
		group.setDepthTest(DepthTest.ENABLE);		
		hands = new HashMap<>();
                
		//Camera
		PerspectiveCamera camera = new PerspectiveCamera(true);              
		camera.setTranslateZ(-500);
		camera.setTranslateY(-200);
		//camera.setFarClip(1000);
		camera.setFieldOfView(80);

                //ROOT
                AnchorPane root = new AnchorPane();
                
                int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
                int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
                
                //Loaders
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/asl/Menu1.fxml"));
                
                loader.setRoot(root);
                root = loader.load();
                root.prefWidth(width);
                root.prefHeight(height-50);
                root.setMaxSize(width, height-50);
                
        
                SplitPane s = (SplitPane)((AnchorPane)((AnchorPane) root.getChildren().get(0)).getChildren().get(1)).getChildren().get(0);
               // AnchorPane root2 = ((AnchorPane) root1.getChildren().get(1));
                //Access Components of Main1.fxml
              // SplitPane s = ((SplitPane) root1.getChildren().get(0));
                               
//                Integrate visualizer into BorderPane
                SubScene subscene_visualizer = new SubScene(group, 300,500);
                subscene_visualizer.setCamera(camera);
                BorderPane vizualizer = new BorderPane();
                //vizualizer.setPrefSize(s.getWidth()/2, s.getHeight());
                vizualizer.setCenter(group);
                
                
               //Component Access for Translate
               ObservableList<Node> translateComponentList = s.getItems();
//               AnchorPane panel 
//               panel.getChildren().add(vizualizer);
//              
                AnchorPane panel = (AnchorPane) translateComponentList.get(0);
//                panel.setPrefSize(width/2, height);
//                panel.setCenterShape(true);
                panel.getChildren().add(vizualizer);
                vizualizer.setTranslateX(200);
                
//               SubScene subscene_visualizer = new SubScene(panel, 500, 500);               
//               subscene_visualizer.setCamera(camera);
               
                //Set scene to root
                scene = new Scene(root,width,height);
                //primaryStage.setResizable(false);
                primaryStage.setTitle("American Sign Language to Voice");
		primaryStage.setScene(scene);
		primaryStage.show();
                
		//Visualizer components
		synchronizeWithLeapMotion();                
		LeapApp.getMotionRegistry().addListener(this);
	}
        
        

	private void synchronizeWithLeapMotion() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(4.0 / 60.0), ea -> LeapApp.update()));
		timeline.play();
	}
	
	@Override
	public void enteredViewoport(PointEvent event) {
		HandFX3D hand = new HandFX3D();
                LeapApp.getController().setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
                hand.setRotate(30);
                hand.setScaleX(2.5);
                hand.setScaleY(2.5);
                hand.setScaleZ(2.5);
		hands.put(event.getSource().id(), hand);
		group.getChildren().add(hand);
                
              
	}
	
	@Override
	public void moved(PointEvent event) {
		int handId = event.getSource().id();
		HandFX3D hand = hands.get(handId);
		hand.update(LeapApp.getController().frame().hand(handId));
                

	}

	@Override
	public void leftViewport(PointEvent event) {
		int handId = event.getSource().id();
		hands.remove(handId);
		group.getChildren().remove(hands.get(handId));
	}
        
        
}
