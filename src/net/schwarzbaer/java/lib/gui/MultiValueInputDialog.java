package net.schwarzbaer.java.lib.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MultiValueInputDialog extends StandardDialog
{
	private static final long serialVersionUID = 6253272604228496786L;
	private final JPanel contentPane;
	private final Vector<Runnable> listOfSetValues;

	public MultiValueInputDialog(Window parent, String title)
	{
		this(parent, title, DEFAULT__MODALITY_TYPE, DEFAULT__REPEATED_USE_OF_DIALOG_OBJECT);
	}

	public MultiValueInputDialog(Window parent, String title, ModalityType modality)
	{
		this(parent, title, modality, DEFAULT__REPEATED_USE_OF_DIALOG_OBJECT);
	}

	public MultiValueInputDialog(Window parent, String title, ModalityType modality, boolean repeatedUseOfDialogObject)
	{
		super(parent, title, modality, repeatedUseOfDialogObject);
		listOfSetValues = new Vector<>();
		
		contentPane = new JPanel(new GridBagLayout());
		createGUI(contentPane,
				createButton("Ok", e->{
					listOfSetValues.forEach(t->t.run());
					closeDialog();
				}),
				createButton("Cancel", e->{
					closeDialog();
				})
		);
	}
	
	public MultiValueInputDialog addText(String text)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		contentPane.add(new JLabel(text), c);
		
		return this;
	}
	
	public interface ValueConverter<ValueType> {
		ValueType convert(String str) throws Exception;
	}
	
	public MultiValueInputDialog addLongField(String label, int fieldSize, String initialValue, LongPredicate isOk, LongConsumer setFinalValue)
	{
		return addField(label, fieldSize, initialValue, Long::parseLong, n->isOk.test(n), n->setFinalValue.accept(n));
	}
	
	public MultiValueInputDialog addIntField(String label, int fieldSize, String initialValue, IntPredicate isOk, IntConsumer setFinalValue)
	{
		return addField(label, fieldSize, initialValue, Integer::parseInt, n->isOk.test(n), n->setFinalValue.accept(n));
	}
	
	public MultiValueInputDialog addDoubleField(String label, int fieldSize, String initialValue, DoublePredicate isOk, DoubleConsumer setFinalValue)
	{
		return addField(label, fieldSize, initialValue, Double::parseDouble, n->isOk.test(n), n->setFinalValue.accept(n));
	}
	
	public MultiValueInputDialog addStringField(String label, int fieldSize, String initialValue, Predicate<String> isOk, Consumer<String> setFinalValue)
	{
		return addField(label, fieldSize, initialValue, str->str, isOk, setFinalValue);
	}
	
	public <ValueType> MultiValueInputDialog addField(String label, int fieldSize, String initialValue, ValueConverter<ValueType> parseStr, Predicate<ValueType> isOk, Consumer<ValueType> setFinalValue)
	{
		JTextField textField = new JTextField(initialValue, fieldSize);
		Color defaultBG = textField.getBackground();
		textField.addKeyListener(new KeyAdapter()
		{
			@Override public void keyReleased(KeyEvent e) {
				processInput(
						textField, parseStr, isOk,
						v  -> textField.setBackground(defaultBG),
						v  -> textField.setBackground(Color.RED),
						() -> textField.setBackground(Color.RED)
				);
			}
		});
		listOfSetValues.add(()->{
			processInput(
					textField, parseStr, isOk,
					setFinalValue, null, null
			);
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		contentPane.add(new JLabel(label+": "), c);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		contentPane.add(textField, c);
		
		return this;
	}

	private static <ValueType> void processInput(
			JTextField textField,
			ValueConverter<ValueType> parseStr, Predicate<ValueType> isOk,
			Consumer<ValueType> whenOk, Consumer<ValueType> whenNotOk, Runnable whenNull)
	{
		String valueStr = textField.getText();
		if (valueStr!=null)
		{
			ValueType value = null;
			
			boolean wasParsed = true;
			try { value = parseStr.convert(valueStr); }
			catch (Exception e1) { wasParsed = false; }
			
			if (wasParsed && (isOk==null || isOk.test(value)))
			{
				if (whenOk!=null)
					whenOk.accept(value);
			}
			else
			{
				if (whenNotOk!=null)
					whenNotOk.accept(value);
			}
		}
		else
		{
			if (whenNull!=null)
				whenNull.run();
		}
	}

	private static JButton createButton(String text, ActionListener al)
	{
		JButton comp = new JButton(text);
		if (al!=null) comp.addActionListener(al);
		return comp;
	}

	@Override
	public void showDialog(Position position)
	{
		pack();
		// TODO Auto-generated method stub
		super.showDialog(position);
	}
}
