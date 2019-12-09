package menu.environnement_editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import environnement.Obstacle;
import environnement.Point;
import environnement.TerrainElement;
import menu.environnement_editor.ListModel;
import tools.math.Vector;

import javax.swing.JMenuBar;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class EnvMenu extends JFrame {

	private JTable propTable;
	private JTable listTable;

	private PropertiesModel propModel;
	private ListModel listModel;

	private EnvPanel envPanel;

	private CardLayout cards;
	private JPanel cardPan;

	private static final String PROPID = "PROPERTIES";
	private static final String LISTID = "LIST";

	private static final int TAILLE_BUTTON = 50;
	private static final int BUTTON_MARGIN = 5;

	private static final String PATH_SELECTION_ICO = "selection.png";
	private static final String PATH_SELECTION_SEL_ICO = "selectionsel.png";
	private static final String PATH_POINT_ICO = "point.png";
	private static final String PATH_POINT_SEL_ICO = "pointsel.png";
	private static final String PATH_RECTANGLE_ICO = "rectangle.png";
	private static final String PATH_RECTANGLE_SEL_ICO = "rectanglesel.png";
	private static final String PATH_CIRCLE_ICO = "cercle.png";
	private static final String PATH_CIRCLE_SEL_ICO = "cerclesel.png";

	private static final String HELP_MESSAGE = "The Selection tool is used to move and modify the elements on the Terrain.\n"
			+ "    To cycle through stacked elements, click multiple time on the same spot without moving the mouse.\n"
			+ "The Point tool is used to place points on the Terrain. Thes points will be used as starting and goal points in simulation.\n"
			+ "The Circle and Rectangle tools are used to create obstacles on the Terrain. They can then be modified with the Selection tool.\n"
			+ "The left hand board allow for direct modifications of specific properties. It can also be used to select elements.";
	/*
	 * "L'outil Sélectionner permet de déplacer et modifier les éléments du terrain.\n"
	 * +
	 * "    Pour cycler à travers des éléments empilés, cliquer plusieurs fois au même endroit en gardant la souris immobile.\n"
	 * +
	 * "L'outil Point permet de créer des points d'intérêt sur le terrain. Ces points serviront à désigner des points de départ et d'arrivée dans la simulation.\n"
	 * +
	 * "Les outils Cercle et Rectangle permettent de créer des formes servant d'obstacles. Ces formes peuvent par la suite être modifiées par l'outil Sélectionner.\n"
	 * +
	 * "Le tableau sur la gauche permet de modifier certaines propriétées directement et précisément. Il permet aussi de sélectionner des éléments."
	 * ;
	 */

	private JMenuItem mntmOpen;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmnNew;
	private JMenuItem mntmSize;
	private JMenuItem mntmExit;
	private JMenuItem mntmHelp;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;

	private JToggleButton tglbtnSelection;

	private ActionListener actExit = e -> envPanel.goBack();

	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));

		mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

		mntmSaveAs = new JMenuItem("Save as");
		mnFile.add(mntmSaveAs);
		mntmSaveAs.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));

		mntmnNew = new JMenuItem("New");
		mnFile.add(mntmnNew);
		mntmnNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

		JMenu mnTerrain = new JMenu("Terrain");
		menuBar.add(mnTerrain);

		mntmSize = new JMenuItem("Size");
		mnTerrain.add(mntmSize);

		mnTerrain.addSeparator();

		mntmUndo = new JMenuItem("Undo");
		mnTerrain.add(mntmUndo);
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));

		mntmRedo = new JMenuItem("Redo");
		mnTerrain.add(mntmRedo);
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));

		JMenu qm = new JMenu("?");
		menuBar.add(qm);

		mntmHelp = new JMenuItem("Help");
		qm.add(mntmHelp);

		setupMenuListener();
	}

	private void setupMenuListener() {
		mntmSize.addActionListener(e -> {
			Vector newSize = new ChangeSizeDialog(null, true, envPanel.getT().getWalls()).showChangeSizeDialog();
			if (newSize != null) {
				envPanel.changeTerrainSize(newSize);
				pack();
			}
		});

		mntmUndo.addActionListener(e -> envPanel.historyPrevious());
		mntmRedo.addActionListener(e -> envPanel.historyNext());

		mntmOpen.addActionListener(e -> envPanel.open());
		mntmSave.addActionListener(e -> envPanel.save());
		mntmSaveAs.addActionListener(e -> envPanel.saveAs());
		mntmnNew.addActionListener(e -> envPanel.createNew());
		mntmExit.addActionListener(actExit);

		mntmHelp.addActionListener(e -> showHelp());
	}

	private void showHelp() {
		JOptionPane.showMessageDialog(this, HELP_MESSAGE, "Help", JOptionPane.INFORMATION_MESSAGE);
	}

	private void setupContentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
	}

	private JPanel setupButtonPane() {
		JPanel btnPanel = new JPanel();
		((FlowLayout) btnPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		btnPanel.setMinimumSize(new Dimension(100, 10));

		ButtonGroup bg = new ButtonGroup();
		int sizeIco = TAILLE_BUTTON - BUTTON_MARGIN;

		tglbtnSelection = getImageButton(PATH_SELECTION_ICO, PATH_SELECTION_SEL_ICO, "Selection", sizeIco,
				"Selection Mode", TAILLE_BUTTON);
		btnPanel.add(tglbtnSelection);
		tglbtnSelection.addActionListener(e -> envPanel.changeMode(Modes.SELECTION));
		bg.add(tglbtnSelection);
		tglbtnSelection.setSelected(true);

		JToggleButton tglbtnPoint = getImageButton(PATH_POINT_ICO, PATH_POINT_SEL_ICO, "Point", sizeIco, "Add a Point",
				TAILLE_BUTTON);
		btnPanel.add(tglbtnPoint);
		tglbtnPoint.addActionListener(e -> envPanel.changeMode(Modes.POINT));
		bg.add(tglbtnPoint);

		JToggleButton tglbtnRectangle = getImageButton(PATH_RECTANGLE_ICO, PATH_RECTANGLE_SEL_ICO, "Rectangle", sizeIco,
				"Add a Rectangle", TAILLE_BUTTON);
		btnPanel.add(tglbtnRectangle);
		tglbtnRectangle.addActionListener(e -> envPanel.changeMode(Modes.RECTANGLE));
		bg.add(tglbtnRectangle);

		JToggleButton tglbtnCircle = getImageButton(PATH_CIRCLE_ICO, PATH_CIRCLE_SEL_ICO, "Circle", sizeIco,
				"Add a Circle", TAILLE_BUTTON);
		btnPanel.add(tglbtnCircle);
		tglbtnCircle.addActionListener(e -> envPanel.changeMode(Modes.CIRCLE));
		bg.add(tglbtnCircle);

		return btnPanel;
	}

	private JToggleButton getImageButton(String imagePathUnsel, String imagePathSel, String alt, int iconSize,
			String toolTip, int btnSize) {
		JToggleButton tglBtn;
		try {
			Image ico = ImageIO.read(getClass().getResourceAsStream("/"+imagePathUnsel));//ImageIO.read(new File(imagePathUnsel));
			tglBtn = new JToggleButton(new ImageIcon(getScaledImage(ico, iconSize, iconSize)));
			ico = ImageIO.read(getClass().getResourceAsStream("/"+imagePathSel));
			tglBtn.setSelectedIcon(new ImageIcon(getScaledImage(ico, iconSize, iconSize)));
		} catch (IOException e) {
			e.printStackTrace();
			tglBtn = new JToggleButton(alt);
		}
		tglBtn.setToolTipText(toolTip);
		tglBtn.setPreferredSize(new Dimension(btnSize, btnSize));
		return tglBtn;
	}

	private JPanel setupLeftPanel() {
		JPanel pan = new JPanel(new BorderLayout());
		JButton btnGoBack = new JButton("Go Back");
		btnGoBack.setFont(new Font("Arial", Font.PLAIN, 25));
		pan.add(btnGoBack, BorderLayout.NORTH);
		btnGoBack.addActionListener(actExit);
		cards = new CardLayout();
		cardPan = new JPanel(cards);
		pan.add(cardPan, BorderLayout.CENTER);

		cardPan.add(setupPropTable(), PROPID);
		cardPan.add(setupListTable(), LISTID);

		return pan;
	}

	private JScrollPane setupPropTable() {
		propModel = new PropertiesModel(this);
		propTable = createTable(propModel);
		propTable.setPreferredScrollableViewportSize(new Dimension(150, 400));
		propTable.setFillsViewportHeight(true);

		return new JScrollPane(propTable);
	}

	private JScrollPane setupListTable() {
		listModel = new ListModel(this);
		listTable = createTable(listModel);
		listTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				java.awt.Point point = mouseEvent.getPoint();
				int row = table.rowAtPoint(point);
				int col = table.columnAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && row != -1 && col == 0) {
					tglbtnSelection.setSelected(true);
					envPanel.setSelectedElement(row);
				} else {
					envPanel.setHighLightedElement(row);
				}
			}
		});
		listTable.setPreferredScrollableViewportSize(new Dimension(150, 400));
		listTable.setFillsViewportHeight(true);

		return new JScrollPane(listTable);
	}

	private static JTable createTable(TableModel model) {
		return new JTable(model) {
			@Override
			public TableCellEditor getDefaultEditor(Class<?> columnClass) {
				return new DefaultCellEditor(new JTextField()) {
					@Override
					public boolean isCellEditable(EventObject anEvent) {
						if (anEvent instanceof KeyEvent) {
							KeyEvent ke = (KeyEvent) anEvent;
							if ((ke.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK)
								return false;
						}
						return super.isCellEditable(anEvent);
					}
				};
			}
		};
	}

	private JScrollPane setupRightPanel() {
		JScrollPane panel = new JScrollPane();
		panel.setColumnHeaderView(setupButtonPane());
		envPanel = new EnvPanel(this);
		panel.setViewportView(envPanel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (envPanel.canClose())
					dispose();
			}
		});

		return panel;
	}

	private JSplitPane setupSplitPane() {
		JSplitPane pan = new JSplitPane();

		pan.setLeftComponent(setupLeftPanel());
		pan.setRightComponent(setupRightPanel());

		return pan;
	}

	/**
	 * Create the frame.
	 */
	public EnvMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setupMenuBar();
		setupContentPane();
		getContentPane().add(setupSplitPane());

		pack();
		setLocationRelativeTo(null);
	}

	public EnvMenu(File f) {
		this();
		envPanel.loadTerrain(f);
	}

	/**
	 * Scale up or down an image to show scaled on screen
	 * 
	 * @param srcImg The image to be scaled
	 * @param w      The width of the output image
	 * @param h      The height of the output image
	 * @return The scaled image
	 */
	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

	/**
	 * Call a repaint on the properties tables and cancel the current editings
	 */
	public void repaintTable() {
		propTable.repaint();
		listTable.repaint();
		TableCellEditor editor = propTable.getCellEditor();
		if (editor != null)
			editor.cancelCellEditing();
		editor = listTable.getCellEditor();
		if (editor != null)
			editor.cancelCellEditing();
	}

	public void setTableElement(TerrainElement el) {
		if (el != null) {
			cards.show(cardPan, PROPID);
			propModel.changeElement(el);
		} else
			cards.show(cardPan, LISTID);
	}

	public void setListElement(List<Obstacle> obs, List<Point> pts) {
		listModel.setList(obs, pts);
	}

	public void historyPush() {
		envPanel.historyPush();
		envPanel.repaint();
	}
}