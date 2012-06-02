package org.imagesci.robopaint;

import java.io.File;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class RoboPaint {
	protected final static Display display = new Display(); //Changed to static to be used as parent
	protected final static Shell shell = new Shell(display); //View in GeometryViewDescription class.

	public static void main(String[] args) {
		Random randn = new Random(23214821);
		for (int i = 0; i < 10; i++) {
			ObjectDescription obj = new ObjectDescription("Object " + i, i);
			obj.setColor(randn.nextInt(256), randn.nextInt(256),
					randn.nextInt(256));
			obj.setTransparency(randn.nextFloat());
			obj.setVisible((i % 2) == 0);
			GeometryViewDescription.getInstance().getObjectDescriptions()
					.add(obj);

		}
		RoboPaint robo = new RoboPaint();
	}

	public RoboPaint() {
		shell.setText("RoboPaint");
		BorderLayout blayout = new BorderLayout();
		shell.setLayout(blayout);
		RoboToolbar toolbar = new RoboToolbar(shell);

		SashForm form = new SashForm(shell, SWT.HORIZONTAL);
		form.setLayoutData(new BorderLayout.BorderData(BorderLayout.CENTER));
		form.setLayout(new FillLayout());
		Composite controlComp = new Composite(form, SWT.BORDER);
		Composite renderComp = new Composite(form, SWT.BORDER);
		renderComp.setLayout(new FillLayout());
		controlComp.setLayout(new FillLayout());
		form.setWeights(new int[] { 25, 75 });
		RoboControlPane controlPane = new RoboControlPane(controlComp);
		RoboRenderPane renderPane = new RoboRenderPane(renderComp);
		RoboMenubar menu = new RoboMenubar(shell);

		shell.setSize(1024, 768);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
