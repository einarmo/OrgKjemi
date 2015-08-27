package space;
import javax.swing.*;

import java.awt.*;

import javax.swing.border.*;

import java.io.*;
import java.util.Scanner;

import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.*;

public class EditPanel {
	JList<String> objlist, filelist;
	int selnum;
	DefaultListModel<String> listM, listF;
	RawFile[] rawfiles;
	int objselection, fileselection;
	JTextField[] fields;
	JButton b1,b2,b3,b4,b5;
	boolean saved;
	JFrame frame;
	Space s;
	JTextField nameField;
	public static Object[] objects;

	EditPanel(int width, int height, Space s) {
		saved = true;

		genRawFiles(Space.objPath);
		setObjDisplay();
		this.objselection = -1;
		listM = new DefaultListModel<String>();
		this.objlist = new JList<String>(listM);
		objlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		objlist.setLayoutOrientation(JList.VERTICAL);
		objlist.setVisibleRowCount(-1);
		ListSelectionModel objlistselmod = objlist.getSelectionModel();
		objlistselmod.addListSelectionListener(new selobjlist());

		JScrollPane mainScroll = new JScrollPane(objlist);
		mainScroll.setBorder(new EmptyBorder(0,0,0,0));
		mainScroll.setPreferredSize(new Dimension(width, height/2));

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridwidth = 9;
		pane.add(mainScroll, c);

		listF = new DefaultListModel<String>();
		setFileDisplay();
		this.filelist = new JList<String>(listF);
		filelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filelist.setLayoutOrientation(JList.VERTICAL);
		filelist.setVisibleRowCount(-1);
		ListSelectionModel filelistselmod = filelist.getSelectionModel();
		filelistselmod.addListSelectionListener(new selfilelist());

		JScrollPane fileScroll = new JScrollPane(filelist);
		fileScroll.setBorder(new EmptyBorder(0,0,0,0));
		fileScroll.setPreferredSize(new Dimension(200, height/2));
		c.gridx = 9;
		c.gridwidth = 2;
		pane.add(fileScroll, c);

		b1 = new JButton("Toggle on");
		b1.setToolTipText("Toggle selected object on or off");
		b1.setActionCommand("toggle");
		b1.addActionListener(new buttonControl());
		b2 = new JButton("Save Changes");
		b2.setToolTipText("Save all changes to file");
		b2.setActionCommand("save");
		b2.addActionListener(new buttonControl());
		b3 = new JButton("Delete");
		b3.setToolTipText("Delete selected object");
		b3.setActionCommand("delete");
		b3.addActionListener(new buttonControl());
		b4 = new JButton("Launch");
		b4.setToolTipText("Launch the program using the last launch profile");
		b4.setActionCommand("launch");
		b4.addActionListener(new buttonControl());
		b5 = new JButton("Edit Launch");
		b5.setToolTipText("Change launch profile, or make a new one");
		b5.setActionCommand("elaunch");
		b5.addActionListener(new buttonControl());

		String[] labelstrings = {"x-pos", "y-pos", "x-vel", "y-vel", "mass", "red", "green", "blue", "parent"};
		fields = new JTextField[labelstrings.length];
		JLabel[] label = new JLabel[labelstrings.length];
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		for(int i = 0; i<labelstrings.length; i++) {
			fields[i] = new JTextField(4);
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 2;
			c.gridx = i;
			fields[i].getDocument().addDocumentListener(new fieldList());
			PlainDocument doc = (PlainDocument) fields[i].getDocument();
			doc.setDocumentFilter(new DocFilter());
			pane.add(fields[i], c);

			label[i] = new JLabel(labelstrings[i]);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.SOUTH;
			c.gridy = 1;
			pane.add(label[i], c);
		}

		for(int k = 0; k<fields.length; k++) {
			if(k<5) {
				fields[k].setText("0.0");
			} else {
				fields[k].setText("0");
			}
		}

		nameField = new JTextField(4);
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridy = 3;

		c.gridx = 1;
		pane.add(b3, c);

		c.gridx = 3;
		pane.add(b1, c);

		c.gridx = 5;
		pane.add(b2, c);

		c.gridx = 7;
		pane.add(b3, c);

		c.gridwidth = 1;
		c.gridx = 9;
		pane.add(b4, c);

		c.gridx = 10;
		pane.add(b5, c);

		c.gridy = 2;
		c.gridx = 10;
		pane.add(nameField, c);

		JLabel nameLabel = new JLabel("Filename");
		c.gridy = 1;
		pane.add(nameLabel, c);

		frame = new JFrame("Edit");
		frame.add(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		//frame.setResizable(false);
		frame.setVisible(true);

		synchronized(frame) {
			try {
				frame.wait();

			} catch (InterruptedException e) {
			}
		}

		frame.dispose();
		s.launch(objects);
	}
	public String createString(int num,int dispnum, RawFile file) {
		String finstr = "";
		String[] info = file.returnInfo(num);
		finstr = (finstr + "Object_" + (dispnum+1));
		finstr = (finstr + ": x: " + info[0]);
		finstr = (finstr + ", y: " + info[1]);
		finstr = (finstr + ", xv: " + info[2]);
		finstr = (finstr +", yv: " + info[3]);
		finstr = (finstr + ", mass: " + info[4]);
		finstr = (finstr + ", colourRed: " + info[5]);
		finstr = (finstr + ", colourGreen: " + info[6]);
		finstr = (finstr + ", colourBlue: " + info[7]);
		if(info[8].equals("0")) {
			finstr = (finstr + ", parent: none");
		}
		else {
			finstr = (finstr + ", parent: " + info[8]);	
		}
		if(info[9].equals("0")) {
			finstr = ("<html><strike>" + finstr + "</strike></html>");
		}
		return finstr;
	}

	public void setFileDisplay() {
		listF.clear();

		for (int i = 0; i<rawfiles.length; i++) {
			listF.addElement(rawfiles[i].name);
		}
		listF.addElement("New entry...");
	}
	public void handleRemove(int removed) {
		RawFile tempfile = rawfiles[fileselection];
		int pos = getActualPosition(removed)-1;
		for (int i = 0; i<tempfile.getLength(); i++) {
			String[] info = tempfile.returnInfo(i);
			if (Integer.valueOf(info[8]) == pos + 1) {
				tempfile.setLP(Integer.valueOf(info[8]), i);
				tempfile.editEntry(i, 8, "0");
			}
			else if (Integer.valueOf(info[8]) > pos+1) {
				tempfile.editEntry(i, 8, Integer.toString(Integer.valueOf(info[8])-1));
			}
		}
	}
	public void handleEnable(int enabled) {
		int pos = getActualPosition(enabled);
		RawFile tempfile = rawfiles[fileselection];
		for (int i = 0; i<tempfile.getLength(); i++) {
			String[] info = tempfile.returnInfo(i);
			if (Integer.valueOf(getActualPosition(tempfile.getLP(i))-1) == pos) {
				tempfile.editEntry(i, 8, Integer.toString(pos));
				tempfile.setLP(-1, i);
			}
			else if (Integer.valueOf(info[8]) >= pos) {
				tempfile.editEntry(i, 8, Integer.toString(Integer.valueOf(info[8])+1));
			}
		}
	}
	public int getActualPosition(int entry) {
		int v = 0;
		for (int i = 0; i<entry; i++) {
			if (rawfiles[fileselection].isenabled(i)) {
				v++;
			}
		}
		v++;
		return v;
	}
	public void addFile(String name) {
		RawFile[] tmpFile = new RawFile[rawfiles.length+1];
		for(int i = 0; i<rawfiles.length; i++) {
			tmpFile[i] = rawfiles[i];
		}
		File f = new File(Space.objPath + "Obj" + name);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
			tmpFile[rawfiles.length] = new RawFile(f);
			rawfiles = new RawFile[tmpFile.length];
			for(int i = 0; i<tmpFile.length; i++) {
				rawfiles[i] = tmpFile[i];
			}
		}
	}
	public void genRawFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		int k = 0;
		File[] verfiles = new File[100];
		for (int i = 0; i<files.length; i++) {
			String nm = files[i].getName();
			if(nm.startsWith("Obj")) {
				verfiles[k] = files[i];
				k++;
			}
		}
		rawfiles = new RawFile[k];
		for(int i = 0; i<k; i++) { 
			rawfiles[i] = new RawFile(verfiles[i]);
		}
	}

	public void setObjDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listM.clear();
				int clength = rawfiles[fileselection].getLength();
				int tmp = 0;
				for(int i = 0; i<clength; i++) {
					listM.addElement(createString(i, tmp, rawfiles[fileselection]));
					if(rawfiles[fileselection].isenabled(i)) {
						tmp++;
					}
				}
				listM.addElement("new entry...");

			}
		});
	}
	public static String[] loadSetup() {
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File("last"));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		inFile.useDelimiter("\n");
		String[] temp = new String[100];
		int j = 0;
		while(inFile.hasNext()) {
			temp[j] = inFile.next();
			j++;
		}
		String[] ret = new String[j];
		for(int i = 0; i<j; i++) {
			ret[i] = temp[i];
		}
		inFile.close();
		return ret;
	}
	public void updateObjDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int clength = rawfiles[fileselection].getLength();
				int tmp = 0;
				for(int i = 0; i<clength; i++) {
					listM.set(i, createString(i, tmp, rawfiles[fileselection]));
					if(rawfiles[fileselection].isenabled(i)) {
						tmp++;
					}
				}
				listM.set(clength, "new entry...");
			}
		});
	}
	public class selobjlist implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!(e.getValueIsAdjusting())) {
				if(fileselection != rawfiles.length) {
					if(objlist.getSelectedIndex() == -1 || objlist.getSelectedIndex() == rawfiles[fileselection].getLength()) {
						objselection = objlist.getSelectedIndex();
						for(int k = 0; k<fields.length; k++) {
							fields[k].setText("0");
						}
						if(objselection==rawfiles[fileselection].getLength()) {

							b1.setText("New Entry");
							b1.setActionCommand("newentry");
							b1.setToolTipText("Create a new object");
						}
					}
					else {
						b1.setText("Toggle on");
						b1.setActionCommand("toggle");
						b1.setToolTipText("Toggle selected object on or off");
						objselection = objlist.getSelectedIndex();
						for(int k = 0; k<fields.length; k++) {
							String[] info = rawfiles[fileselection].returnInfo(objselection);
							if(k>5) {
								fields[k].setText(info[k]);
							} else if ((info[k].equals("0") && k<5)){
								fields[k].setText("0.0");
							} else {
								fields[k].setText(info[k]);
							}
						}
					}
				}
			}
		}
	}
	public class selfilelist implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!(e.getValueIsAdjusting())) {
				if(filelist.getSelectedIndex() == -1 || filelist.getSelectedIndex() == rawfiles.length+1) {
					objselection = -1;
				}
				else {
					fileselection = filelist.getSelectedIndex();
					if(fileselection == rawfiles.length) {
						b1.setText("New File");
						b1.setActionCommand("newfile");
						b1.setToolTipText("Create a new file with the entered name");
						objselection = -1;
						listM.clear();
					}
					else {
						b1.setText("Toggle on");
						b1.setActionCommand("toggle");
						b1.setToolTipText("Toggle selected object on or off");
						objselection = -1;
						setObjDisplay();
					}
				}
			}
		}
	}
	public class buttonControl implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ((command.equals("toggle"))&&objselection != -1&&fileselection != rawfiles.length) {
				if (objselection != rawfiles[fileselection].getLength()) {
					saved = false;
					if (rawfiles[fileselection].isenabled(objselection)) {
						handleRemove(objselection);
						rawfiles[fileselection].editEntry(objselection, 9, "0");
					}
					else {
						rawfiles[fileselection].editEntry(objselection, 9, "1");
						handleEnable(objselection);
					}
					updateObjDisplay();
				}
			}
			else if (command.equals("save")) {
				saved = true;
				for(int i = 0; i<rawfiles.length; i++) {
					rawfiles[i].writeToFile();
				}
			}
			else if (command.equals("delete")) {
				rawfiles[fileselection].removePerm(objselection);
				handleRemove(objselection);
				setObjDisplay();
				saved = false;
			}
			else if (command.equals("launch")) {
				if(fileselection != -1 && fileselection != rawfiles.length) {
					objects = rawfiles[fileselection].createObjects();
					if(!saved) {
						new Dialog("Save before launching?", rawfiles, frame);
					}
					else {
						synchronized(frame) {
							frame.notify();
						}
					}
				}
			}
			else if (command.equals("elaunch")) {
				if(fileselection != -1 && fileselection != rawfiles.length) {
					new LaunchSetup(rawfiles, fileselection, frame);
					if(!saved) {
						new Dialog("Save files?", rawfiles);
					}
				}
			}
			else if (command.equals("newfile")) {
				String name = nameField.getText();
				if(name.length() > 1) {
					addFile(name);
					setFileDisplay();
					fileselection = rawfiles.length-1;
				}
			}
			else if (command.equals("newentry")) {
				rawfiles[fileselection].addEntry();
				objselection = rawfiles[fileselection].getLength()-1;
				setObjDisplay();
				objlist.setSelectedIndex(objselection);
			}
		}
	}
	public class fieldList implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			Document source = e.getDocument();
			if(rawfiles.length != fileselection) {
				if(rawfiles[fileselection].getLength()!=objselection) {
					for(int w = 0; w<fields.length; w++) {
						if (source.equals(fields[w].getDocument())&& objselection != -1) {
							saved = false;
							rawfiles[fileselection].editEntry(objselection, w, fields[w].getText());
						}
					}
					updateObjDisplay();
				}
			}
		}
		public void removeUpdate(DocumentEvent e) {
			Document source = e.getDocument();
			if(rawfiles.length != fileselection) {
				if(rawfiles[fileselection].getLength()!=objselection) {
					for(int w = 0; w<fields.length; w++) {
						if (source.equals(fields[w].getDocument())&& objselection != -1) {
							saved = false;
							rawfiles[fileselection].editEntry(objselection, w, fields[w].getText());
						}
					}
					updateObjDisplay();
				}
			}
		}
		public void changedUpdate(DocumentEvent e) {
		}
	}
	class DocFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i<5) {
						if(isInt(string) || string.equals("-") && (offset == 0)) {
							super.insertString(fb, offset, string, attr);
						}
					}else if(i<8 && i>4) {
						if(isInt(string)) {
							if ((Integer.valueOf(sb.toString()) < 256) && (Integer.valueOf(sb.toString()) > -1)) {
								super.insertString(fb, offset, string, attr);
							}
						}
					} else if(i==8) {
						System.out.println(sb.toString() + rawfiles[fileselection].getLength());

						if(isInt(string)) {
							if((Integer.valueOf(sb.toString()) < (rawfiles[fileselection].getLength()+1)) 
									&& (Integer.valueOf(sb.toString())> -1)) {
								super.insertString(fb, offset, string, attr);
							}
						}
					}
				}
			}
		}
		private boolean isInt(String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		private boolean isDouble(String text) {
			try {
				Double.parseDouble(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		@Override
		public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, string);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i<5) {
						if(isDouble(sb.toString())) {
							super.replace(fb, offset, length, string, attr);
						}
					}else if(i<8 && i>4) {
						if(isInt(sb.toString())) {
							if ((Integer.valueOf(sb.toString()) < 256) && (Integer.valueOf(sb.toString()) > -1)) {
								super.replace(fb, offset, length, string, attr);
							}
						}
					}else if(i==8) {
						if(isInt(sb.toString())) {
								if ((Integer.valueOf(sb.toString()) < rawfiles[fileselection].getLength()+1) 
										&& (Integer.valueOf(sb.toString())> -1)) {
								super.replace(fb, offset, length, string, attr);
							}
						}
					}
				}
			}
		}
		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i<5) {
						if (isDouble(sb.toString())) {
							super.remove(fb, offset, length);
						} 
					} else if(i>4 && i<8) {
						if (isInt(sb.toString())) {
							if(Integer.parseInt(sb.toString()) > -1 && Integer.parseInt(sb.toString()) < 256) {
								super.remove(fb, offset, length);
							}
						}
					} else if(i == 8) {
						if (isInt(sb.toString())) {
							if(Integer.parseInt(sb.toString()) > -1 && Integer.parseInt(sb.toString()) < rawfiles[fileselection].getLength()) {
								super.remove(fb, offset, length);
							}
						}
					}
				}
			}
			if(sb.length() == 0) {
				super.remove(fb, 0, 1);
			}
		}
	}
}
