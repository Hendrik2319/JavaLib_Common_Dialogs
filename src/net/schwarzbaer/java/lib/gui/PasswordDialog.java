package net.schwarzbaer.java.lib.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordDialog extends StandardDialog
{
	private static final long serialVersionUID = -4678662276683068635L;
	
	private char[] result;

	private final JPanel contentPane;
	private final JPasswordField passwordField;
	private final JLabel dialogText;
	private final TextVisibleButton btnTextVisible;

	public PasswordDialog(Window parent, String initialValue, boolean repeatedUseOfDialogObject)
	{
		super(parent, "Enter Password", ModalityType.APPLICATION_MODAL, repeatedUseOfDialogObject);
		result = null;
		
		dialogText = new JLabel("Enter password :");
		passwordField = new JPasswordField(initialValue, 20);
		passwordField.addKeyListener(new KeyAdapter()
		{
			@Override public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					copyValueAndCloseDialog();
			}
		});
		
		btnTextVisible = new TextVisibleButton();
		
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(dialogText, BorderLayout.NORTH);
		contentPane.add(passwordField, BorderLayout.CENTER);
		//contentPane.add(btnTextVisible, BorderLayout.EAST);
		
		createGUI(
				contentPane,
				createButton("Ok"    , e -> copyValueAndCloseDialog()),
				createButton("Cancel", e -> closeDialog())
		);
	}

	private void copyValueAndCloseDialog()
	{
		result = passwordField.getPassword();
		closeDialog();
	}
	
	public void activateTextVisibleButton()
	{
		activateTextVisibleButton(null,null,null,null);
	}
	
	public void activateTextVisibleButton(Icon visibleEnabledIcon, Icon visibleDisabledIcon, Icon notVisibleEnabledIcon, Icon notVisibleDisabledIcon)
	{
		contentPane.add(btnTextVisible, BorderLayout.EAST);
		btnTextVisible.setIcons(visibleEnabledIcon, visibleDisabledIcon, notVisibleEnabledIcon, notVisibleDisabledIcon);
	}
	
	private class TextVisibleButton extends JButton
	{
		private static final long serialVersionUID = -3747929739289393908L;
		
		private char defaultEchoChar;
		private boolean isTextVisible;
		private Icon visibleEnabledIcon;
		private Icon visibleDisabledIcon;
		private Icon notVisibleEnabledIcon;
		private Icon notVisibleDisabledIcon;
		private boolean showButtonText;

		TextVisibleButton()
		{
			defaultEchoChar = passwordField.getEchoChar();
			isTextVisible = false;
			showButtonText = true;
			
			addActionListener(e -> {
				isTextVisible = !isTextVisible;
				passwordField.setEchoChar(isTextVisible ? (char)0 : defaultEchoChar);
				updateButtonView();
			});
			setMargin(new Insets(2,2,2,2));
		}

		void updateButtonView()
		{
			if (showButtonText)
			{
				setText(isTextVisible ? "hide" : "show");
				setIcon(null);
				setDisabledIcon(null);
			}
			else
			{
				setText(null);
				setIcon        (isTextVisible ? notVisibleEnabledIcon  : visibleEnabledIcon );
				setDisabledIcon(isTextVisible ? notVisibleDisabledIcon : visibleDisabledIcon);
			}
		}
		
		void setIcons(Icon visibleEnabledIcon, Icon visibleDisabledIcon, Icon notVisibleEnabledIcon, Icon notVisibleDisabledIcon)
		{
			if (visibleEnabledIcon==null || visibleDisabledIcon==null || notVisibleEnabledIcon==null || notVisibleDisabledIcon==null)
			{
				this.visibleEnabledIcon = null;
				this.visibleDisabledIcon = null;
				this.notVisibleEnabledIcon = null;
				this.notVisibleDisabledIcon = null;
				showButtonText = true;
			}
			else
			{
				this.visibleEnabledIcon = visibleEnabledIcon;
				this.visibleDisabledIcon = visibleDisabledIcon;
				this.notVisibleEnabledIcon = notVisibleEnabledIcon;
				this.notVisibleDisabledIcon = notVisibleDisabledIcon;
				showButtonText = false;
			}
			updateButtonView();
		}
	}

	private static JButton createButton(String text, ActionListener al)
	{
		JButton comp = new JButton(text);
		if (al!=null) comp.addActionListener(al);
		return comp;
	}

	public void setDialogText(String dialogText)
	{
		this.dialogText.setText(dialogText);
	}

	public void setInitialValue(String initialValue)
	{
		passwordField.setText(initialValue);
	}
	
	public String showDialog(String initialValue)
	{
		setInitialValue(initialValue);
		return showDialog_();
	}

	public String showDialog_()
	{
		super.showDialog(Position.PARENT_CENTER);
		
		if (result == null)
			return null;
		
		String str = new String( result );
		Arrays.fill(result, (char)0);
		return str;
	}
	
	static public String showDialog(Window parent)
	{
		return showDialog(parent, null);
	}
	
	static public String showDialog(Window parent, String initialValue)
	{
		return new PasswordDialog(parent, initialValue, false).showDialog_();
	}
}