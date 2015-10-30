package org.openml.weka.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class AuthenticationDialog extends JDialog {
	private static final long serialVersionUID = 4714724734714L;

	private int m_returnValue;

	private static final JLabel[] label = { new JLabel("Api key: ") };

	private static final String[] tooltip = { "Your OpenML Api key" };

	JButton cancel = new JButton("Cancel");
	JButton submit = new JButton("Submit");

	private static final JTextComponent[] textComponent = { new JTextField() };

	public int getReturnValue() {
		return m_returnValue;
	}

	public String getApiKey() {
		return textComponent[0].getText();
	}

	public AuthenticationDialog(JFrame parent ) {
		super(parent, "Authenticate on OpenML.org", true);
		
		add(getContents());
		
		getRootPane().setDefaultButton(submit);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
	}

	private JPanel getContents() {
		JPanel contents = new JPanel();
		contents.setLayout(new BorderLayout());
		contents.setPreferredSize(new Dimension(320, 110));
		contents.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		contents.add(getInputPanel(), BorderLayout.CENTER);
		contents.add(getButtonPanel(), BorderLayout.SOUTH);
		return contents;
	}

	private JPanel getInputPanel() {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		for (int i = 0; i < label.length; ++i) {
			textComponent[i].setBorder(BorderFactory.createEtchedBorder());
			textComponent[i].setAlignmentX(JComponent.LEFT_ALIGNMENT);
			textComponent[i].setText("");
			label[i].setToolTipText(tooltip[i]);
			label[i].setAlignmentX(JComponent.LEFT_ALIGNMENT);
			inputPanel.add(label[i]);
			inputPanel.add(textComponent[i]);
		}
		return inputPanel;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				m_returnValue = JOptionPane.CLOSED_OPTION;
				AuthenticationDialog.this.dispose();
			}
		});

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				m_returnValue = JOptionPane.OK_OPTION;
				AuthenticationDialog.this.dispose();
			}
		});

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				m_returnValue = JOptionPane.CLOSED_OPTION;
			}
		});

		buttonPanel.add(cancel);
		buttonPanel.add(submit);
		return buttonPanel;
	}

}
