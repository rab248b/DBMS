package edu.tamu.db.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.tamu.db.parser.QueryExecution;

public class UILoader extends Frame {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private static boolean sqlCommandFlag =false;
	private static boolean sqlFileFlag = false;
	private Label statusLabel;
	private int frame_width = 1024;
	private int frame_height = 1024;
	
	private boolean selectFlag = false;
	

	UILoader() {
		Frame f = new Frame();
		f.setLayout(new GridLayout(3, 1));
		JFileChooser fc = new JFileChooser();
		Button b1 = new Button("SQL Command");
		b1.setBounds((int) (frame_width / 10), (int) (frame_height / 10), (int) (frame_width / 5),
				(int) (frame_height / 10));
		Button b2 = new Button("Select File");
		b2.setBounds(2 * (int) (frame_width / 10) + (int) (frame_width / 5), (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 10));
		Button b3 = new Button("Run");
		b3.setBounds(3 * (int) (frame_width / 10) + 2 * (int) (frame_width / 5), (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 10));
		TextField inputTextField = new TextField("");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		inputTextField.setBounds(new Rectangle((int) (frame_width / 10), 3 * (int) (frame_height / 10),
				(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));

		
		f.add(b1);
		f.add(b2);
		f.add(b3);
		f.setSize(frame_width, frame_height);
		
		f.setLayout(null);
		f.setVisible(true);

		JFileChooser chooser = new JFileChooser();
		JFrame frame = new JFrame();
		// File file = null;
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputTextField.setVisible(true);
				f.add(inputTextField);
				sqlCommandFlag = true;
				sqlFileFlag = false;
			}
		});
		statusLabel = new Label();
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sqlCommandFlag && !sqlFileFlag) {

				} else {
					if (sqlCommandFlag) {
						String data = inputTextField.getText();
						statusLabel.setText(data);
						System.out.println(statusLabel.getText());
						statusLabel.setBounds(new Rectangle((int) (frame_width / 10), 5 * (int) (frame_height / 10),
								(int) (frame_width * 0.8), 1 * (int) (frame_height / 10)));
						statusLabel.setVisible(true);
						f.add(statusLabel);
					} else {
						File file = chooser.getSelectedFile();
						String fileData;
						try {
							BufferedReader br = new BufferedReader(new FileReader(file));
							try {
							    StringBuilder sb = new StringBuilder();
							    String line = br.readLine();

							    while (line != null) {
							        sb.append(line);
							        sb.append(System.lineSeparator());
							        line = br.readLine();
							    }
							    fileData = sb.toString();
							} finally {
							    br.close();
							}
							statusLabel.setText(fileData);
							System.out.println(statusLabel.getText());
							statusLabel.setBounds(new Rectangle((int) (frame_width / 10), 5 * (int) (frame_height / 10),
									(int) (frame_width * 0.8), 1 * (int) (frame_height / 10)));
							statusLabel.setVisible(true);
							f.add(statusLabel);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					QueryExecution qe = new QueryExecution();
					qe.runQuery(statusLabel.getText());
					if (selectFlag) {
						Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3" },
								{ "Row2-Column1", "Row2-Column2", "Row2-Column3" } };
						Object columnNames[] = { "Column One", "Column Two", "Column Three" };
						JTable table = new JTable(rowData, columnNames);
						JScrollPane scrollPane = new JScrollPane(table);
						scrollPane.setBounds(new Rectangle((int) (frame_width / 10), 6 * (int) (frame_height / 10),
								(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));
						f.add(scrollPane, BorderLayout.CENTER);
					}else{
						statusLabel.setText(qe.getResult());
					}
					
				}
			}
		});
		b2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inputTextField.setVisible(false);
				// Show dialog; this method does not return until dialog is
				// closed
				sqlCommandFlag = false;
				sqlFileFlag = true;
				chooser.showOpenDialog(frame);
			}

		});
		
		
	}

	public static void main(String[] args) {
		UILoader uiLoader = new UILoader();
		
	}

	public boolean isSelectFlag() {
		return selectFlag;
	}

	public void setSelectFlag(boolean selectFlag) {
		this.selectFlag = selectFlag;
	}
}
