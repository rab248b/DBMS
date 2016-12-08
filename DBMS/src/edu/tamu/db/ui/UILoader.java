package edu.tamu.db.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import edu.tamu.db.parser.QueryExecution;
import storageManager.Disk;
import storageManager.MainMemory;

public class UILoader extends Frame {

	/**
		 * 
		 */

	private static final long serialVersionUID = 1L;
	private static boolean sqlCommandFlag = false;
	private static boolean sqlFileFlag = false;

	public static MainMemory mem = new MainMemory();
	public static Disk disk = new Disk();
	 private Label statusLabel;
	private int frame_width = 600;
	private int frame_height = 600;

	private boolean selectFlag = false;
	private static QueryExecution qe;

	UILoader() {
		qe = new QueryExecution();
		Frame f = new Frame();
		f.setLayout(new GridLayout(3, 1));
		JFileChooser fc = new JFileChooser();
		JTextArea textArea;
		textArea = new JTextArea();
//		AboutDialog aboutDialog = new AboutDialog(this);
		textArea.setLineWrap(true);
		Button b1 = new Button("SQL Command");
		b1.setBounds((int) (frame_width / 10), (int) (frame_height / 10), (int) (frame_width / 5),
				(int) (frame_height / 10));
		Button b2 = new Button("Select File");
		
		b2.setBounds(2 * (int) (frame_width / 10) + (int) (frame_width / 5), (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 10));
		Button b3 = new Button("Run");
		b3.setBounds(3 * (int) (frame_width / 10) + 2 * (int) (frame_width / 5), (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 10));
		Button b4 = new Button("Yes");
		b4.setBounds((int) (frame_width / 10) , 9 * (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 20));
		Button b5 = new Button("No");
		b5.setBounds(2 * (int) (frame_width / 10) + (int) (frame_width / 5), 9 * (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 20));
		Button b6 = new Button("Output");
		b6.setBounds(2 * (int) (frame_width / 10) + (int) (frame_width / 5), 5 * (int) (frame_height / 10),
				(int) (frame_width / 5), (int) (frame_height / 20));
		JScrollPane scrollPane = new JScrollPane(null);
		// TextField inputTextField = new TextField("");

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		textArea.setBounds(new Rectangle((int) (frame_width / 10), 3 * (int) (frame_height / 10),
				(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));

		/*
		 * inputTextField.setBounds(new Rectangle((int) (frame_width / 10), 3 *
		 * (int) (frame_height / 10), (int) (frame_width * 0.8), 2 * (int)
		 * (frame_height / 10)));
		 */

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
				// inputTextField.setVisible(true);
				// f.add(inputTextField);
				textArea.setVisible(true);
				f.add(textArea);
				textArea.setLineWrap(true);
				textArea.setBorder(BorderFactory.createLineBorder(Color.black));
				sqlCommandFlag = true;
				sqlFileFlag = false;
			}
		});
		JTextArea consoleText = new JTextArea();
		consoleText.setLineWrap(true);
		 statusLabel = new Label();
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sqlCommandFlag && !sqlFileFlag) {

				} else {
					if (sqlCommandFlag) {
						// String data = textArea.getText();
						// consoleText.setText("");
						// System.out.println(consoleText.getText());
						// consoleText.setBounds(new Rectangle((int)
						// (frame_width / 10), 5 * (int) (frame_height / 10),
						// (int) (frame_width * 0.8), 1 * (int) (frame_height /
						// 10)));
						// consoleText.setVisible(true);
						// f.add(consoleText);
						try {
							qe.createFile(!closeFlag);
							for (String line : textArea.getText().split("\\n")) {

								qe.runQuery(line);

								if (selectFlag) {
									Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3" },
											{ "Row2-Column1", "Row2-Column2", "Row2-Column3" } };
									Object columnNames[] = { "Column One", "Column Two", "Column Three" };
									JTable table = new JTable(rowData, columnNames);
									JScrollPane scrollPane = new JScrollPane(table);
									scrollPane.setBounds(
											new Rectangle((int) (frame_width / 10), 6 * (int) (frame_height / 10),
													(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));
									f.add(scrollPane, BorderLayout.CENTER);
								} else {
									consoleText.setText(consoleText.getText() + qe.getResult() + "\n");
									
									
								}
							}
							scrollPane.setViewportView(consoleText);
							scrollPane.setBounds(
									new Rectangle((int) (frame_width / 10), 6 * (int) (frame_height / 10),
											(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));
//							aboutDialog.setVisible(true);
							statusLabel.setText("Do you want to continue?");
							statusLabel.setBounds((int) (frame_width / 10), 8 * (int) (frame_height / 10),
						 (int) (frame_width * 0.8), 1 * (int) (frame_height /
						 10));
							scrollPane.setVisible(true);
							f.add(scrollPane, BorderLayout.CENTER);
							statusLabel.setVisible(true);
							b4.setVisible(true);
							b5.setVisible(true);
							b6.setVisible(true);
							f.add(statusLabel);
							f.add(b4);
							f.add(b5);
							f.add(b6);
//							qe.saveData();
						} catch (Exception e1) {
							e1.printStackTrace();
						}

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
							consoleText.setText(fileData);
							JScrollPane scrollPane = new JScrollPane(consoleText);
							scrollPane.setBounds(
									new Rectangle((int) (frame_width / 10), 6 * (int) (frame_height / 10),
											(int) (frame_width * 0.8), 2 * (int) (frame_height / 10)));
							f.add(scrollPane, BorderLayout.CENTER);

						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					// QueryExecution qe = new QueryExecution(mem, disk);

					// qe.runQuery(statusLabel.getText());

				}
			}
		});
		
		b4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeFlag = false;
				b4.setVisible(false);
				b5.setVisible(false);
				statusLabel.setVisible(false);
				textArea.setText(null);
			}
		});
		

		b6.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		b5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeFlag = true;
				try {
					qe.saveData();
				} catch (IOException e) {
					System.out.println("Error in saving file");
					e.printStackTrace();
				}
				f.dispose();
			}
		});
		b2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setVisible(false);
				// Show dialog; this method does not return until dialog is
				// closed
				sqlCommandFlag = false;
				sqlFileFlag = true;
				chooser.showOpenDialog(frame);
			}

		});
		
	}

//	class AboutDialog extends Dialog {
//		public AboutDialog(Frame parent) {
//			super(parent, true);
//			setBackground(Color.gray);
//			setLayout(new BorderLayout());
//			setTitle("Do you want to Continue?");
//			setSize(frame_height/2,frame_height/2);
//			Panel panel = new Panel();
//			panel.add(new Button("Yes"));
//			panel.add(new Button("No"));
//			add("South", panel);
//			setSize(200, 200);
//			closeFlag = false;
//			addWindowListener(new WindowAdapter() {
//				public void windowClosing(WindowEvent windowEvent) {
//					dispose();
//				}
//			});
//		}
//
//		public boolean action(Event evt, Object arg) {
//			if (arg.equals("No")) {
//				dispose();
//				closeFlag = true;
//				return true;
//			} else if (arg.equals("Yes")) {
//				dispose();
//				closeFlag = false;
//				return true;
//			}
//			return false;
//		}
//	}

	private boolean closeFlag = true;

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
