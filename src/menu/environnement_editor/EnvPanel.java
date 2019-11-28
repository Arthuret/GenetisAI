package menu.environnement_editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.filechooser.FileNameExtensionFilter;

import environnement.*;
import environnement.objects.Circle;
import environnement.objects.Rectangle;
import menu.MainMenu;
import tools.math.Vector;

@SuppressWarnings("serial")
public class EnvPanel extends JPanel implements Scrollable {

	private Terrain t;
	private TerrainElement selectedElement = null;
	private int indiceSelectedElement = 0;
	private TerrainElement highLightedElement = null;

	private static final Color HIGHLIGHTED_COLOR = new Color(0, 192, 0);
	private static final Color SELECTED_COLOR = new Color(255, 0, 0);
	private static final Color DEFAULT_COLOR = new Color(0, 0, 255);

	private File filePath = null;
	private File emplacement = null;

	private boolean modifiedTerrain = false;

	private EnvMenu parentMenu;

	private Modes mode = Modes.SELECTION;

	private List<Terrain> history = new ArrayList<>();
	private int historyIndex = -1;
	private int savedIndex = -1;

	public EnvPanel(EnvMenu parent) {
		super();
		this.parentMenu = parent;
		init();
		t = new Terrain();
		initTerrain();
		historyPush();
		setSaved(true);
	}

	public EnvPanel(String file, EnvMenu parent) {// load a terrain directly
		super();
		this.parentMenu = parent;
		init();
		loadTerrain(new File(file));
		initTerrain();
		filePath = new File(file);
		emplacement = new File(file);
		historyPush();
	}

	private void init() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Vector mpos = new Vector(e.getX(), e.getY());
				switch (mode) {
				case SELECTION:
					selObs(mpos);
					selectCursor(mpos);
					break;
				case RECTANGLE:
					t.getObstacles().add(new Rectangle(mpos));
					modifiedTerrain = true;
					historyPush();
					break;
				case CIRCLE:
					t.getObstacles().add(new Circle(mpos));
					modifiedTerrain = true;
					historyPush();
					break;
				case POINT:
					t.getPoints().add(new Point(mpos));
					modifiedTerrain = true;
					historyPush();
					break;
				default:
					break;
				}
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				highLightedElement = null;
				if (selectedElement != null) {
					selectedElement.grab(new Vector(e.getX(), e.getY()));
					elemGrabed = true;
					dragged = false;
				}
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				elemGrabed = false;
				selectCursor(new Vector(e.getX(), e.getY()));
				if (dragged) {
					modifiedTerrain = true;
					historyPush();
				}
				repaint();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (elemGrabed && selectedElement != null) {
					selectedElement.drag(new Vector(e.getX(), e.getY()), t.getWalls());
					dragged = true;
				}
				repaint();
				parentMenu.repaintTable();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				selectCursor(new Vector(e.getX(), e.getY()));
				repaint();
			}
		});
		setKeyBindings();
		this.setBackground(Color.LIGHT_GRAY);
	}

	private void initTerrain() {
		parentMenu.setListElement(t.getObstacles(), t.getPoints());
		this.setPreferredSize(t.getWalls().getDimension());
	}

	private void setKeyBindings() {
		String vkDelete = "VK_DELETE";
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), vkDelete);
		getActionMap().put(vkDelete, new KeyAction(vkDelete));
	}

	private class KeyAction extends AbstractAction {

		public KeyAction(String actionCommand) {
			putValue(ACTION_COMMAND_KEY, actionCommand);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "VK_DELETE":
				deleteObstacle();
				break;
			default:
				break;
			}
		}

		private void deleteObstacle() {
			if (selectedElement != null) {
				if (selectedElement instanceof Obstacle)
					t.getObstacles().remove(selectedElement);
				else
					t.getPoints().remove(selectedElement);
				selectedElement = null;
				indiceSelectedElement = 0;
				elemGrabed = false;
				modifiedTerrain = true;
				historyPush();
				setCursor(Cursor.getDefaultCursor());
				repaint();
			}
		}
	}

	private boolean mouseIsInsideActualSelected(Vector mPos) {
		return (selectedElement != null) ? selectedElement.mouseIsInside(mPos) : false;
	}

	private void selectCursor(Vector mousePos) {
		if (mode == Modes.SELECTION && selectedElement != null)
			setCursor(selectedElement.getCursorAtLocation(mousePos));
		else
			setCursor(Cursor.getDefaultCursor());
	}

	private boolean selObs(Vector mousePosition) {
		boolean trouve = false;
		// si on est dans la liste
		if (selectedElement != null && mouseIsInsideActualSelected(mousePosition)) {
			// on la termine
			trouve = selObsAfterStart(mousePosition, indiceSelectedElement + 1);
		}
		// si on a rien de selectionne ou si on est arrivé au bout de la liste
		if (!trouve) {
			trouve = selObsAll(mousePosition);
		}

		if (!trouve) {
			selectedElement = null;
			indiceSelectedElement = -1;
		}
		return trouve;
	}

	/**
	 * Allow the selection of elements when they are stacked
	 */
	private boolean selObsAfterStart(Vector mousePosition, int start) {
		boolean trouve = false;
		for (int i = start; i < t.getObstacles().size() + t.getPoints().size(); i++) {// on balaye la liste a partir du
																						// start
			TerrainElement temp = (i < t.getObstacles().size()) ? t.getObstacles().get(i)
					: t.getPoints().get(i - t.getObstacles().size());
			if (temp.isInside(mousePosition)) {// si le curseur est dans l'obstacle
				trouve = true;// on a trouve l'obstacle
				selectedElement = temp;
				indiceSelectedElement = i;
				break;
			}
		}
		return trouve;
	}

	private boolean selObsAll(Vector mousePosition) {
		boolean trouve = false;
		for (int i = 0; i < t.getObstacles().size() + t.getPoints().size(); i++) {// on repart du debut de la liste
																					// jusqu'au start
			TerrainElement temp = (i < t.getObstacles().size()) ? t.getObstacles().get(i)
					: t.getPoints().get(i - t.getObstacles().size());
			if (temp.isInside(mousePosition)) {
				trouve = true;
				selectedElement = temp;
				indiceSelectedElement = i;
				break;
			}
		}
		return trouve;
	}

	private boolean elemGrabed = false;
	private boolean dragged = false;

	@Override
	public void paintComponent(Graphics g) {
		this.setSize(t.getWalls().getDimension());
		super.paintComponent(g);

		t.getObstacles().forEach(el -> paintElement(el, g));
		t.getPoints().forEach(el -> paintElement(el, g));

		String titre = "";
		titre = (filePath == null) ? "New Terrain" : filePath.getName();
		if (modifiedTerrain)
			titre += "*";
		parentMenu.setTitle(titre + " - GenetisAI Terrain Editor");
		parentMenu.setTableElement(selectedElement);
		// parentMenu.repaintTable();
	}

	private void paintElement(TerrainElement el, Graphics g) {
		// TODO distinction pour les points selectionnes
		if (el == selectedElement && el instanceof Obstacle) {
			g.setColor(SELECTED_COLOR);
			((Obstacle) el).showCenter(g);
		} else if (el == highLightedElement && el instanceof Obstacle) {
			g.setColor(HIGHLIGHTED_COLOR);
		} else {
			g.setColor(DEFAULT_COLOR);
		}
		el.show(g);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return t.getWalls().getDimension();
	}

	@Override
	public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void changeMode(Modes m) {
		mode = m;
		selectedElement = null;
		this.repaint();
	}

	public Terrain getT() {
		return t;
	}

	public void changeTerrainSize(Vector newSize) {
		t.setWalls(newSize);
		setPreferredSize(newSize.getDimension());
		modifiedTerrain = true;
		historyPush();
		repaint();
	}

	private File selectFile(boolean open) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Terrain file (.terrain)", "terrain"));
		fc.setDialogTitle((open) ? "Open" : "Save as");
		if (emplacement != null)
			fc.setCurrentDirectory(emplacement);
		if (open) {
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				emplacement = fc.getSelectedFile().getParentFile();
				return fc.getSelectedFile();
			}
		} else {
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				emplacement = fc.getSelectedFile().getParentFile();
				if (!fc.getSelectedFile().getAbsolutePath().endsWith(".terrain"))
					return new File(fc.getSelectedFile().getAbsolutePath() + ".terrain");
				else
					return fc.getSelectedFile();
			}
		}
		return null;
	}

	private boolean askOverwrite() {
		return JOptionPane.showConfirmDialog(this, "This file already exist. Do you want to overwrite it ?",
				"Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * Ask the user to save the changes if needed
	 * @return true if the action shall continue
	 */
	private boolean saveChanges() {
		if (modifiedTerrain) {
			switch (JOptionPane.showConfirmDialog(this, "Save the changes ?", "Changes unsaved",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			case JOptionPane.YES_OPTION:
				save();
			case JOptionPane.NO_OPTION:
				return true;
			}
			return false;
		}else
			return true;
	}

	public void save() {
		if (filePath == null)
			saveAs();
		else
			saver(filePath);
	}

	public void saveAs() {
		File selectedFile = selectFile(false);
		if (selectedFile != null && (!selectedFile.exists() || askOverwrite()))
			saver(selectedFile);
	}

	public void open() {
		if (!saveChanges()) {
			return;
		}
		File f = selectFile(true);
		if(f != null) {
			filePath = f;
			loadTerrain(f);
		}
	}
	
	private void reset() {
		historyClear();
		historyPush();
		modifiedTerrain = false;
		setSaved(true);
		initTerrain();
		parentMenu.pack();
		repaint();
	}

	public void createNew() {
		if (!saveChanges()) {
			return;
		}
		filePath = null;
		t = new Terrain();
		reset();
	}

	private void saver(File file) {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.flush();
			filePath = file;
			modifiedTerrain = false;
			setSaved(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error when saving file\n" + e.getMessage(), "Save error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		repaint();
	}

	private void loadTerrain(File terrainFile) {
		try (FileInputStream fis = new FileInputStream(terrainFile)) {
			this.t = (Terrain) new ObjectInputStream(fis).readObject();
		} catch (ClassNotFoundException | IOException | ClassCastException e) {
			JOptionPane.showMessageDialog(this, "Error when opening file\n" + e.getMessage(), "Read error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		reset();
	}

	public boolean canClose() {
		return saveChanges();
	}

	public void goBack() {
		if (saveChanges()) {
			parentMenu.dispose();
			new MainMenu().setVisible(true);
		}
	}

	/**
	 * Set an element to be selected
	 * @param index the index of the selected element
	 */
	public void setSelectedElement(int index) {
		if (index >= 0 && index < (t.getObstacles().size() + t.getPoints().size())) {
			selectedElement = (index < t.getObstacles().size()) ? t.getObstacles().get(index)
					: t.getPoints().get(index - t.getObstacles().size());
			indiceSelectedElement = index;
			mode = Modes.SELECTION;
			repaint();
		}
	}

	/**
	 * Set an element to be visually highlighted (changing color)
	 * @param index the index of the selected element
	 */
	public void setHighLightedElement(int index) {
		if (index >= 0 && index < (t.getObstacles().size() + t.getPoints().size())) {
			highLightedElement = (index < t.getObstacles().size()) ? t.getObstacles().get(index)
					: t.getPoints().get(index - t.getObstacles().size());
		} else {
			highLightedElement = null;
		}
		repaint();
	}

	/**
	 * Push a new version in the history. Used by the undo/redo mechanic
	 */
	public void historyPush() {
		if (historyIndex < history.size() - 1) {
			for (int i = history.size() - 1; i > historyIndex; i--) {
				history.remove(i);
			}
		}
		history.add(t.deepCopy());
		historyIndex++;
		if (savedIndex >= historyIndex) {
			savedIndex = -1;
		}
		parentMenu.repaintTable();
	}
	
	/**
	 * Update all datas after an undo/redo operation
	 */
	private void historyUpdate() {
		this.t = history.get(historyIndex).deepCopy();
		modifiedTerrain = !(historyIndex == savedIndex);
		setPreferredSize(t.getWalls().getDimension());
		initTerrain();
		this.repaint();
		parentMenu.repaintTable();
	}

	/**
	 * perform a redo operation
	 */
	public void historyNext() {
		if (historyIndex < history.size() - 1) {
			historyIndex++;
			historyUpdate();
		}
	}

	/**
	 * perform an undo operation
	 */
	public void historyPrevious() {
		if (historyIndex > 0) {
			historyIndex--;
			historyUpdate();
		}
	}

	/**
	 * clear all history data from memory. used when changin file
	 */
	private void historyClear() {
		history.clear();
		historyIndex = -1;
	}

	/**
	 * tell the history that the actual version is saved on disk
	 * @param saved
	 */
	private void setSaved(boolean saved) {
		if (saved)
			savedIndex = historyIndex;
	}
}
