package View;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import Controller.Controller;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.JOptionPane;

public class CalendarProgram{
	static Controller controller = Controller.getInstance();
    static JLabel lblMonth, lblYear;
    static JButton btnPrev, btnNext;
    static JTable tblCalendar;
    static JComboBox cmbYear;
    static JFrame frmMain;
    static Container pane;
    static DefaultTableModel mtblCalendar; //Table model
    static JScrollPane stblCalendar; //The scrollpane
    static JPanel pnlCalendar;
    static int realYear, realMonth, realDay, currentYear, currentMonth;
    static boolean boolDayView, boolWeekView, boolMonthView;
    
    public static void main (String args[]){
    	boolMonthView = true;
    	
        //Look and feel
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}
        
        //Prepare frame
        frmMain = new JFrame ("Calendar"); //Create frame
        frmMain.setSize(330, 375); //Set size to 400x400 pixels
        pane = frmMain.getContentPane(); //Get content pane
        pane.setLayout(null); //Apply null layout
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close when X is clicked
        
        JMenuBar menubar = new JMenuBar();
        
        //create account menu
        JMenu accountMenu = new JMenu("account");
        
        JMenuItem createAccountButton = new JMenuItem(new AbstractAction("create user account") {
        	public void actionPerformed(ActionEvent e) {
        		String username, password, email, age;
        		int bDay, bMonth, bYear;
        		username = JOptionPane.showInputDialog("Enter a username:");
        		password = JOptionPane.showInputDialog("Enter a password:");
        		email = JOptionPane.showInputDialog("Enter an email:");
        		email = "'" + email + "'";
        		age = JOptionPane.showInputDialog("Enter your age:");
        		bYear = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth year:"));
        		bMonth = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth month:"));
        		bDay = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth day:"));
        		System.out.println(controller.signUpUser(username, email, password, bYear, bMonth, bDay));
        		//System.out.println(controller.signInUser(email, password));
        		
        
        	}
        });
        accountMenu.add(createAccountButton);
        
        JMenuItem loginButton = new JMenuItem(new AbstractAction("Login") {
        	public void actionPerformed(ActionEvent e) {
        		String username, password, email;
        		int bDay, bMonth, bYear;
        		email = JOptionPane.showInputDialog("Enter your email:");
        		email = "'" + email + "'";
        		password = JOptionPane.showInputDialog("Enter your password:");
        		System.out.println(controller.signInUser(email, password));
        		//System.out.println(controller.signInUser(email, password));
        		
        
        	}
        });
        accountMenu.add(loginButton);
        
        JMenuItem changeUsername = new JMenuItem(new AbstractAction("change username") {
        	public void actionPerformed(ActionEvent e) 
        	{
        		String email = JOptionPane.showInputDialog("Enter your email: ");
        		email = "'" + email + "'";
        		int year = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth year:"));
        		int month = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth month:"));
        		int day = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth day:"));
        		String newName = JOptionPane.showInputDialog("Enter Your New Name: ");	
        		LocalDate birthday = LocalDate.of(year, month, day);
        		System.out.println(controller.resetName(newName, email, birthday));
        		//System.out.println("changeUsername");
        	}
        });
        accountMenu.add(changeUsername);
        
        JMenuItem changePassword = new JMenuItem(new AbstractAction("change password") {
        	public void actionPerformed(ActionEvent e) 
        	{
        		String email = JOptionPane.showInputDialog("Enter your email: ");
        		email = "'" + email + "'";
        		int year = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth year:"));
        		int month = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth month:"));
        		int day = Integer.parseInt(JOptionPane.showInputDialog("Enter your birth day:"));
        		String newPass = JOptionPane.showInputDialog("Enter Your New Password: ");	
        		LocalDate birthday = LocalDate.of(year, month, day);
        		System.out.println(controller.resetPassword(newPass, email, birthday));
        	}
        });
        accountMenu.add(changePassword);
        
        JMenuItem changeUserInformation = new JMenuItem(new AbstractAction("change email") 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		String email = JOptionPane.showInputDialog(accountMenu, "Enter your current email: ", "Current Email", JOptionPane.QUESTION_MESSAGE);
        		email = "'" + email + "'";
        		int year = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth year:", "Birth Year", JOptionPane.QUESTION_MESSAGE));
        		int month = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth month:", "Birth Month", JOptionPane.QUESTION_MESSAGE));
        		int day = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth day:", "Birth Day", JOptionPane.QUESTION_MESSAGE));
        		LocalDate birthday = LocalDate.of(year, month, day);
        		String newEmail = JOptionPane.showInputDialog(accountMenu, "Enter your new email: ", "New Email", JOptionPane.QUESTION_MESSAGE);
        		newEmail = "'" + newEmail + "'";
        		//LocalDate newBirthday = LocalDate.of(newYear, newMonth, newDay);
        		System.out.println(controller.resetEmail(email, newEmail, birthday));
        	}
        });
        //accountMenu.add(changeUserInformation);
        
        
        
        menubar.add(accountMenu);
        
        //Change Birthday
        JMenu birthdayMenu = new JMenu("Change User Information");
        JMenuItem changeBirthday = new JMenuItem(new AbstractAction("Change Birthday") {
        	public void actionPerformed(ActionEvent e)
        	{
        		String email = JOptionPane.showInputDialog(accountMenu, "Enter your current email: ", "Current Email", JOptionPane.QUESTION_MESSAGE);
        		email = "'" + email + "'";
        		int year = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth year:", "Birth Year", JOptionPane.QUESTION_MESSAGE));
        		int month = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth month:", "Birth Month", JOptionPane.QUESTION_MESSAGE));
        		int day = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your birth day:", "Birth Day", JOptionPane.QUESTION_MESSAGE));
        		LocalDate birthday = LocalDate.of(year, month, day);
        		int newYear = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your new birth year: ", "New Birth Year", JOptionPane.QUESTION_MESSAGE));
        		int newMonth = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your new birth month", "New Month", JOptionPane.QUESTION_MESSAGE));
        		int newDay = Integer.parseInt(JOptionPane.showInputDialog(accountMenu, "Enter your new birth day", "New Day", JOptionPane.QUESTION_MESSAGE));
        		System.out.println(controller.resetBirthday(email, birthday, newYear, newMonth, newDay));
        	}
        });
        birthdayMenu.add(changeBirthday);
        birthdayMenu.add(changeUserInformation);
        accountMenu.add(birthdayMenu);
        
        //create appointment menu
        JMenu appointmentMenu = new JMenu("appointment");
        
        JMenuItem makeAppointmentButton = new JMenuItem(new AbstractAction("make appointment") {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("makeAppointmentButton");
        	}
        });
        appointmentMenu.add(makeAppointmentButton);
        
        JMenuItem cancelAppointmentButton = new JMenuItem(new AbstractAction("cancel appointment") {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("changeAppointmentButton");
        	}
        });
        appointmentMenu.add(cancelAppointmentButton);
        
        JMenuItem changeAppointmentButton = new JMenuItem(new AbstractAction("change appointment") {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("changeAppointmentButton");
        	}
        });
        appointmentMenu.add(changeAppointmentButton);
        menubar.add(appointmentMenu);
        
        //calendar view menu bar
        JMenu calendarViewBar = new JMenu("view");
        
        JMenuItem weekView = new JMenuItem(new AbstractAction("week view") {
            public void actionPerformed(ActionEvent e) {
                boolMonthView = false;
                boolDayView = false;
                boolWeekView = true;
                refreshCalendar(currentMonth, currentYear);  
            }
        });
        calendarViewBar.add(weekView);
        
        
        JMenuItem monthView = new JMenuItem(new AbstractAction("month view") {
        	public void actionPerformed(ActionEvent e) {
        		boolMonthView = true;
                boolDayView = false;
                boolWeekView = false;
                refreshCalendar(currentMonth, currentYear);  
        	}
        });
        calendarViewBar.add(monthView);
        

        JMenuItem dayView = new JMenuItem(new AbstractAction("day view") {
        	public void actionPerformed(ActionEvent e) {
        		boolMonthView = false;
                boolDayView = true;
                boolWeekView = false;
                refreshCalendar(currentMonth, currentYear);  
        	}
        });
        calendarViewBar.add(dayView);
        
        JMenuItem colorView = new JMenuItem(new AbstractAction("change calendar color") {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("color button");
        	}
        });
        calendarViewBar.add(colorView);
        menubar.add(calendarViewBar);
        
        
        frmMain.setJMenuBar(menubar);
        
        //Create controls
        lblMonth = new JLabel ("January");
        lblYear = new JLabel ("Change year:");
        cmbYear = new JComboBox();
        btnPrev = new JButton ("<");
        btnNext = new JButton (">");
        mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tblCalendar = new JTable(mtblCalendar);
        stblCalendar = new JScrollPane(tblCalendar);
        pnlCalendar = new JPanel(null);
        
        //Set border
        pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        //Register action listeners
        btnPrev.addActionListener(new btnPrev_Action());
        btnNext.addActionListener(new btnNext_Action());
        cmbYear.addActionListener(new cmbYear_Action());
        //calendarViewBar.addActionListener(new weekView_Action);
        
        //Add controls to pane
        pane.add(pnlCalendar);
        pnlCalendar.add(lblMonth);
        pnlCalendar.add(lblYear);
        pnlCalendar.add(cmbYear);
        pnlCalendar.add(btnPrev);
        pnlCalendar.add(btnNext);
        pnlCalendar.add(stblCalendar);
        
        //Set bounds
        pnlCalendar.setBounds(0, 0, 320, 335);
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 100, 25);
        lblYear.setBounds(10, 305, 150, 20);
        cmbYear.setBounds(225, 305, 90, 20);
        btnPrev.setBounds(10, 25, 50, 25);
        btnNext.setBounds(260, 25, 50, 25);
        stblCalendar.setBounds(10, 50, 300, 250);
        
        //Make frame visible
        frmMain.setResizable(false);
        frmMain.setVisible(true);
        
        //Get real month/year
        GregorianCalendar cal = new GregorianCalendar(); //Create calendar
        realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); //Get day
        realMonth = cal.get(GregorianCalendar.MONTH); //Get month
        realYear = cal.get(GregorianCalendar.YEAR); //Get year
        currentMonth = realMonth; //Match month and year
        currentYear = realYear;
        
        if (boolMonthView == true)
        {
            //Add headers
            String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; //All headers
            for (int i=0; i<7; i++){
                mtblCalendar.addColumn(headers[i]);
            }
        }
        else if (boolWeekView == true)
        {
            //Add headers
            String headers = "Week View"; //All headers
            mtblCalendar.addColumn(headers);
        }   
        else if (boolDayView == true)
        {
            
            String headers = "Day View"; //All headers
            mtblCalendar.addColumn(headers);
        }   
        
        tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background
        
        //No resize/reorder
        tblCalendar.getTableHeader().setResizingAllowed(false);
        tblCalendar.getTableHeader().setReorderingAllowed(false);
        
        //Single cell selection
        tblCalendar.setColumnSelectionAllowed(true);
        tblCalendar.setRowSelectionAllowed(true);
        tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //Set row/column count
        tblCalendar.setRowHeight(38);
        
        if (boolMonthView == true)
        {
        	mtblCalendar.setColumnCount(7);
        	mtblCalendar.setRowCount(6);
        }
        else if (boolWeekView == true)
        {
            mtblCalendar.setColumnCount(1);
            mtblCalendar.setRowCount(4);
        }   
        else if (boolDayView == true)
        {
            mtblCalendar.setColumnCount(1);
            mtblCalendar.setRowCount(1);
        }  
        
       
        
        //Populate table
        for (int i=realYear-100; i<=realYear+100; i++){
            cmbYear.addItem(String.valueOf(i));
        }
        
        //Refresh calendar
        refreshCalendar (realMonth, realYear); //Refresh calendar
    }
    
    public static void refreshCalendar(int month, int year){
        //Variables
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som; //Number Of Days, Start Of Month
        
        if (boolMonthView == true)
        {
        	
        	mtblCalendar.setColumnCount(0);
            String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; //All headers
            for (int i=0; i<7; i++){
                mtblCalendar.addColumn(headers[i]);
            }
            
        }
        else if (boolWeekView == true)
        {
            mtblCalendar.setColumnCount(0);
            mtblCalendar.addColumn("week view");
        }   
        else if (boolDayView == true)
        {
            mtblCalendar.setColumnCount(0);
            mtblCalendar.addColumn("day view");
        }  
        
        //Allow/disallow buttons
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (month == 0 && year <= realYear-10){btnPrev.setEnabled(false);} //Too early
        if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);} //Too late
        lblMonth.setText(months[month]); //Refresh the month label (at the top)
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 180, 25); //Re-align label with calendar
        cmbYear.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box
        
        if(boolMonthView == true)
        {
            //Clear table
            for (int i=0; i<6; i++){
                for (int j=0; j<7; j++){
                    mtblCalendar.setValueAt(null, i, j);
                }
            }
        }
        else if (boolWeekView == true)
        {
            //Clear table
            for (int i=0; i<6; i++){
                    mtblCalendar.setValueAt(null, i, 0);
            }
        }
        else if (boolDayView == true)
        {
            //Clear table
            for (int i=0; i<6; i++){
                    mtblCalendar.setValueAt(null, i, 0);
            }
        }

        //Get first day of month and number of days
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        if(boolMonthView == true)
        {
            //Draw calendar
            for (int i=1; i<=nod; i++){
                int row = new Integer((i+som-2)/7);
                int column  =  (i+som-2)%7;
                mtblCalendar.setValueAt(i, row, column);
            }
        }
        else if (boolWeekView == true)
        {
            //Draw calendar week view
            for (int i=1; i<5; i++){
                int column  = 0;
                mtblCalendar.setValueAt(i, i, column);
            }
        }
        else if (boolDayView == true)
        {
            //Draw calendar week view
            for (int i=1; i<2; i++){
                int column  = 0;
                mtblCalendar.setValueAt(i, i, column);
            }
        }
        
        //Apply renderers
        tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
    }
    
    static class tblCalendarRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (column == 0 || column == 6){ //Week-end
                setBackground(new Color(255, 255, 255));
            }
            else{ //Week
                setBackground(new Color(255, 255, 255));
            }
            if (value != null){
                if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){ //Today
                    setBackground(new Color(220, 220, 255));
                }
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
    }
    
    static class btnPrev_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0){ //Back one year
                currentMonth = 11;
                currentYear -= 1;
            }
            else{ //Back one month
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class btnNext_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){ //Foward one year
                currentMonth = 0;
                currentYear += 1;
            }
            else{ //Foward one month
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class cmbYear_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (cmbYear.getSelectedItem() != null){
                String b = cmbYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear);
            }
        }
    }
    
}