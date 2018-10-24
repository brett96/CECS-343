package View;

import Controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class ViewFX extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		Controller controller = Controller.getInstance();
	}
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}

}
