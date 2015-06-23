package applet;

//The package containing our stubs.
import AdditionApp.Addition;
import AdditionApp.AdditionHelper;

//HelloClient will use the naming service.
import org.omg.CosNaming.*;
//The package containing special exceptions thrown by the name service.
import org.omg.CosNaming.NamingContextPackage.*;
//All CORBA applications need these classes.
import org.omg.CORBA.*;

import java.awt.Button;
import java.awt.Event;
//Needed for the applet.
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;

public class AddApplet extends java.applet.Applet {
	
	private TextField aField, bField, sumField, logField;
	private Button run;
	
	 Addition addobj;
	
	private void layOut(){
		setLayout(new GridLayout(4,2, 10, 10));
		
		add(new Label("a"));
		add(aField = new TextField());
		aField.setText("0");
		
		add(new Label("b"));
		add(bField = new TextField());
		bField.setText("0");
		
		add(new Label("sum:"));
		add(sumField = new TextField());
		sumField.setText("0");
		
		add(run = new Button("ADD"));
		add(logField = new TextField());
	}

	public void init() {
		try {
			
			layOut();

			showStatus("Initializing the ORB");

			// Create and initialize the ORB
			// The applet 'this' is passed to make parameters from the tag
			// available to initialize the ORB
			ORB orb = ORB.init(this, null);

			org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
		    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		    addobj = (Addition) AdditionHelper.narrow(ncRef.resolve_str("ABC"));
		    
		    showStatus("Done Initializing the ORB");

		} catch (Exception e) {
			System.out.println("AddApplet exception: " + e);
			e.printStackTrace(System.out);
		}
	}
	
	private void showMsg(String msg){
		logField.setText(msg);
	}

	public boolean action(Event ev, java.lang.Object arg) {
		if (ev.target == run) {
			try { 
				// Set Sum to initial value of 0
				showMsg("Setting Sum to 0");
				sumField.setText("0");

				// get data from and set value of applet fields
				showMsg("Adding...");
				int a = Integer.parseInt(aField.getText());

				int b = Integer.parseInt(bField.getText());
				
				showMsg("add a, b");

				int addition = addobj.add(a, b);

				sumField.setText("" + addition);

				showMsg("Sum = " + addition);
			} catch (Exception e) {
				showStatus("System Exception" + e);
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}


}
