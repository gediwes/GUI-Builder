package bdl.view.right;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.build.javafx.scene.control.GMenuBar;
import bdl.build.properties.FieldName;
import bdl.build.properties.GUISizeProperty;
import bdl.build.properties.ListenerEnabledProperty;
import bdl.build.properties.PanelProperty;
import bdl.build.properties.PropertyListenerEnabledProperty;
import bdl.build.properties.StrokeProperty;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettings;
import bdl.model.ListenerProperty;
import bdl.model.Property;
import bdl.model.history.HistoryManager;
import di.menubuilder.MenuBuilder;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PropertyEditPane extends GridPane {

	/**
	 * Constructor for "No component selected" pane
	 */
	public PropertyEditPane() {
		add(new Label(LabelGrabber.getLabel("no.component.text")), 0, 0);
	}

	public PropertyEditPane(GUIObject guiObj, ComponentSettings componentSettings) {
		int currentRow = 0;
		this.getChildren().clear();
		// this.setMaxWidth(200);
		Label propertiesHeading = new Label(LabelGrabber.getLabel("properties.text") + ":");
		// propertiesHeading.setMinWidth(90);
		propertiesHeading.setFont(Font.font(propertiesHeading.getFont().getFamily(), FontWeight.BOLD,
				propertiesHeading.getFont().getSize() + 0.5));
		add(propertiesHeading, 0, currentRow++);
		GUISizeProperty guisp = new GUISizeProperty(guiObj, guiObj.getGUITitle(), this, currentRow);
		currentRow += 3;
		List<PanelProperty> panelPropertyList = new ArrayList<>();
		if (componentSettings.getListenerProperties().size() > 0) {
			Label eventsHeading = new Label(LabelGrabber.getLabel("events.text") + ":");
			eventsHeading.setMinWidth(120);
			eventsHeading.setFont(Font.font(eventsHeading.getFont().getFamily(), FontWeight.BOLD,
					propertiesHeading.getFont().getSize() + 0.5));
			add(eventsHeading, 0, currentRow++);
		}
		for (ListenerProperty lprop : componentSettings.getListenerProperties()) {
			try {
				PanelProperty panelProperty = null;
				// panelProperty = new ListenerHintProperty(gObj, guiObject,
				// lprop.getName(), lprop.getText(), this, currentRow++);
				// panelPropertyList.add(panelProperty);
				if (lprop.getListenertype().equals("standard")) {
					panelProperty = new ListenerEnabledProperty(guiObj, lprop.getListenerMethod(), lprop.getName(),
							lprop.getListenerEvent(), lprop.getDefaultValue(), lprop.getPackageName(), this,
							currentRow++);
				} else if (lprop.getListenertype().equals("propertyChange")) {
					panelProperty = new PropertyListenerEnabledProperty(guiObj, lprop.getPropertyname(),
							lprop.getPropertytype(), lprop.getListenerEvent(), lprop.getDefaultValue(),
							lprop.getPackageName(), this, currentRow++);
				}
				if (panelProperty != null) {
					panelPropertyList.add(panelProperty);
				}
			} catch (Exception e) {
				System.out.println(lprop.getName() + "ListenerProperty failed.");
				e.printStackTrace();
			}
		}

		guiObj.setPanelProperties(panelPropertyList);
	}

	/**
	 * Constructor for gObject pane
	 */
	public PropertyEditPane(GObject gObj, ComponentSettings componentSettings, ArrayList<String> fieldNames,
			GUIObject guiObject, Node settingsNode, HistoryManager historyManager) {
		// For reference, old properties panel: http://i.imgur.com/UBb7P4k.png
		int currentRow = 0;
		this.getChildren().clear();
		// this.setMaxWidth(300);

		Label propertiesHeading = new Label(LabelGrabber.getLabel("properties.text") + ":");
		propertiesHeading.setMinWidth(120);
		propertiesHeading.setFont(Font.font(propertiesHeading.getFont().getFamily(), FontWeight.BOLD,
				propertiesHeading.getFont().getSize() + 0.5));
		add(propertiesHeading, 0, currentRow++);

		new FieldName(gObj, fieldNames, componentSettings.getType(), this, currentRow++, historyManager);

		List<PanelProperty> panelPropertyList = new ArrayList<>();
		for (Property property : componentSettings.getProperties()) {
			String type = property.getPseudotype();
			if (property.isStyleProperty()) {
				try {
					Class panelPropertyClass = Class.forName("bdl.build.properties." + type + "Property");
					Constructor constructor = panelPropertyClass.getConstructor(GObject.class, String.class,
							String.class, String.class, GridPane.class, int.class, Node.class,
							HistoryManager.class);
					PanelProperty panelProperty = (PanelProperty) constructor.newInstance(gObj, property.getName(),
							property.getProperty(),  property.getDefaultValue(), this, currentRow++,
							settingsNode, historyManager);
					panelPropertyList.add(panelProperty);
				} catch (Exception e) {
					System.err.println(type + "StyleProperty failed.");
					e.printStackTrace();
				}
			} else {
				try {
					Class panelPropertyClass = Class.forName("bdl.build.properties." + type + "Property");
					Constructor constructor = panelPropertyClass.getConstructor(GObject.class, String.class,
							String.class, String.class, String.class, GridPane.class, int.class, Node.class,
							HistoryManager.class);
					PanelProperty panelProperty = (PanelProperty) constructor.newInstance(gObj, property.getName(),
							property.getProperty(), property.getFxml(), property.getDefaultValue(), this, currentRow++,
							settingsNode, historyManager);
					if (!property.isGenerateJavaCode()) {
						panelProperty.disableJavaCodeGeneration();
					}
					if (panelProperty instanceof StrokeProperty) {
						currentRow++;
					}
					panelPropertyList.add(panelProperty);
				} catch (Exception e) {
					System.out.println(type + "Property failed.");
					e.printStackTrace();
				}
			}
		}
		if (componentSettings.getListenerProperties().size() > 0) {
			Label eventsHeading = new Label(LabelGrabber.getLabel("events.text") + ":");
			eventsHeading.setMinWidth(120);
			eventsHeading.setFont(Font.font(eventsHeading.getFont().getFamily(), FontWeight.BOLD,
					propertiesHeading.getFont().getSize() + 0.5));
			add(eventsHeading, 0, currentRow++);
		}
		for (ListenerProperty lprop : componentSettings.getListenerProperties()) {
			try {
				PanelProperty panelProperty = null;
				// panelProperty = new ListenerHintProperty(gObj, guiObject,
				// lprop.getName(), lprop.getText(), this, currentRow++);
				// panelPropertyList.add(panelProperty);
				if (lprop.getListenertype().equals("standard")) {
					panelProperty = new ListenerEnabledProperty(gObj, lprop.getListenerMethod(), lprop.getName(),
							lprop.getListenerEvent(), lprop.getDefaultValue(), lprop.getPackageName(), this,
							currentRow++);
				} else if (lprop.getListenertype().equals("propertyChange")) {
					panelProperty = new PropertyListenerEnabledProperty(gObj, lprop.getPropertyname(),
							lprop.getPropertytype(), lprop.getListenerEvent(), lprop.getDefaultValue(),
							lprop.getPackageName(), this, currentRow++);
				}
				if (panelProperty != null) {
					panelPropertyList.add(panelProperty);
				}
			} catch (Exception e) {
				System.out.println(lprop.getName() + "ListenerProperty failed.");
				e.printStackTrace();
			}
		}
		if (gObj instanceof GMenuBar) {
			MenuBuilder mb = ((GMenuBar) gObj).getMenuBuilder();
			this.add(mb.getPane(), 0, currentRow, 4, 6);
		}
		gObj.setPanelProperties(panelPropertyList);
	}
}
