package simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import environnement.Terrain;
import tools.math.Vector;

/**
 * A test frame used to test the sensor (raycast) computation
 * @author Arthur France
 *
 */
public class TestFrame {
	private static Terrain t = null;
	private static Vector m = new Vector(0,0);

	public static void main(String[] args) {
		File fl = null;
		do {
			do{
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("Terrain file", "terrain"));
				fc.setDialogTitle("Open a terrain");
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					fl = fc.getSelectedFile();
			}while(fl == null);
			try (FileInputStream fis = new FileInputStream(fl)) {
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object o = (ois.readObject());
				ois.close();
				if(o instanceof Terrain)
					t = (Terrain)o;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(t == null) {
				System.out.println("This file does not contain a Terrain");
			}
		}while(t == null);
		JFrame f = new JFrame("Testing...");
		Pane p = new Pane();
		f.add(p);
		p.setPreferredSize(t.getWalls().getDimension());
		f.pack();
		p.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				m.setX(e.getX());
				m.setY(e.getY());
				f.repaint();
			}
		});
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	private static final int RADIUS_INDIC = 10;
	private static final int LIMIT = 300;
	
	@SuppressWarnings("serial")
	private static class Pane extends JPanel{
		public void paintComponent(Graphics g) {
			t.show(g,null,false,true,1f,new Vector(),true,false);
			Vector v0;

			g.setColor(Color.RED);
			v0 = Vector.fromAngle(0, t.getDistanceImpact(m, 0)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.ORANGE);
			v0 = Vector.fromAngle(Math.PI / 4, t.getDistanceImpact(m, Math.PI / 4)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.GREEN);
			v0 = Vector.fromAngle(Math.PI / 2, t.getDistanceImpact(m, Math.PI / 2)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.CYAN);
			v0 = Vector.fromAngle(3 * Math.PI / 4, t.getDistanceImpact(m, 3 * Math.PI / 4)).limit(LIMIT)
					.add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.BLUE);
			v0 = Vector.fromAngle(Math.PI, t.getDistanceImpact(m, Math.PI)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.MAGENTA);
			v0 = Vector.fromAngle(-3 * Math.PI / 4, t.getDistanceImpact(m, -3 * Math.PI / 4)).limit(LIMIT)
					.add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.DARK_GRAY);
			v0 = Vector.fromAngle(-Math.PI / 2, t.getDistanceImpact(m, -Math.PI / 2)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

			g.setColor(Color.BLACK);
			v0 = Vector.fromAngle(-Math.PI / 4, t.getDistanceImpact(m, -Math.PI / 4)).limit(LIMIT).add(m);
			g.drawLine((int) m.x(), (int) m.y(), (int) v0.x(), (int) v0.y());
			g.fillOval((int) (v0.x()-RADIUS_INDIC), (int)(v0.y()-RADIUS_INDIC), RADIUS_INDIC*2, RADIUS_INDIC*2);

		}
	}

}
