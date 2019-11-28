package neural_net_matrix;

import java.io.Serializable;
import java.util.Random;

/**
 * A mathematical matrix used to store net parameters
 * 
 * @author Arthur France
 */
public class Matrix implements Serializable {
	private static final long serialVersionUID = 1136685684377678340L;
	
	private float[][] matrix;
	private int x, y;

	/**
	 * General constructor
	 * 
	 * @param x    The Width of the matrix
	 * @param y    The Height of the matrix
	 * @param max  The maximum random value
	 * @param min  The minimum random value
	 * @param rand The Random object used to generate the values. null to put min to
	 *             everything
	 */
	private Matrix(int x, int y, float max, float min, Random rand) {
		matrix = new float[x][y];
		this.x = x;
		this.y = y;

		if (min == max || rand == null) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					matrix[i][j] = min;
				}
			}
		} else {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					matrix[i][j] = rand.nextFloat() * (max-min) + min;
				}
			}
		}
	}

	/**
	 * Constructeur Simple, crée une matrice de taille x,y dont les valeurs sont
	 * choisies aleatoirement entre -1 et 1
	 * 
	 * @param x Largeur de la matrice
	 * @param y Hauteur de la matrice
	 */
	public Matrix(int x, int y) {
		this(x, y, 1, -1, new Random());
	}

	/**
	 * Constructeur a valeur unie, crée une matrice de taille x,y dont toutes les
	 * cases valent valeur
	 * 
	 * @param x     Largeur de la matrice
	 * @param y     Hauteur de la matrice
	 * @param value Valeur a mettre dans les cases
	 */
	public Matrix(int x, int y, float value) {
		this(x, y, value, value, null);
	}

	/**
	 * Constructeur le plus complet, crée une matrice de taille x,y dont les valeurs
	 * sont fixées aléatoirement entre min et max
	 * 
	 * @param x Largeur de la matrice
	 * @param y Hauteur de la matrice
	 * @param max La valeur maximale attribuable aux cases
	 * @param min La valeur minimale attribuable aux cases
	 */
	public Matrix(int x, int y, float max, float min) {
		this(x, y, max, min, new Random());
	}
	
	/**
	 * Perform a matrix multiplication this*mat and retrieve the resulting matrix
	 * The operands are untouched
	 * @param mat The Matrix to be multiplied
	 * @return The Matrix resulting from this*mat
	 * @throws UnsupportedOperationException when the matrix sizes doesnt correspond
	 */
	public Matrix multiply(Matrix mat) throws UnsupportedOperationException{
		//TODO multithread
		if(this.x != mat.y) throw new UnsupportedOperationException("Matrix sizes doesn't correspond");
		Matrix resp = new Matrix(mat.x,this.y,0);
		
		//on balaye toutes les cases de la nouvelle matrice
		for(int i = 0;i < resp.y;i++) {
			for(int j = 0;j < resp.x;j++) {
				//on fait le multipladd
				float temp = 0;
				for(int k = 0;k < this.x;k++) {
					temp += this.matrix[k][i]*mat.matrix[j][k];
				}
				resp.matrix[j][i] = temp;
			}
		}
		return resp;
	}
	
	/**
	 * Perform a multiplication term by term of the matrix elements and retrieve the resulting matrix
	 * The operands are untouched
	 * @param mat The matrix to multiply with
	 * @return A new Matrix with each term (i,j) = this(i,j)*mat(i,j)
	 * @throws UnsupportedOperationException when the matrix sizes doesn't correspond
	 */
	public Matrix multipliyTerm(Matrix mat) throws UnsupportedOperationException{
		if(this.x != mat.x || this.y != mat.y) throw new UnsupportedOperationException("Matrix sizes doesn't match");
		Matrix resp = new Matrix(x,y,0);
		for(int i = 0;i < x;i++) {
			for(int j = 0;j < y;j++) {
				resp.matrix[i][j] = this.matrix[i][j]*mat.matrix[i][j];
			}
		}
		return resp;
	}
	
	/**
	 * Perform a term by term add operation and retrieve the resulting matrix
	 * @param mat The lmatrix to add with
	 * @return A new Matrix with each term (i,j) = this(i,j)+mat(i,j)
	 * @throws UnsupportedOperationException when the matrix sizes doesn't correspond
	 */
	public Matrix add(Matrix mat) throws UnsupportedOperationException{
		//TODO multithread
		if(x != mat.x || y != mat.y) throw new UnsupportedOperationException("Matrixes of different sizes cannot be added");
		Matrix resp = new Matrix(x,y,0);
		
		//on balaye toutes les cases de la nouvelle matrice
		for(int i = 0;i < resp.x;i++) {
			for(int j = 0;j < resp.y;j++) {
				resp.matrix[i][j] = matrix[i][j]+mat.matrix[i][j];
			}
		}
		return resp;
	}
	
	public String toString() {
		String resp = "";
		for(int i = 0;i < y;i++) {
			for(int j = 0;j < x;j++) {
				resp+=matrix[j][i]+"\t";
			}
			resp+="\n";
		}
		return resp;
	}
	
	/**
	 * Pass every value in the matrix trough the given function
	 * @param function The function to apply to every element in the matrix
	 * @param args The arguments to pass down to the function
	 */
	public void applyFunction(TfFunction function,Object...args) {
		//TODO multithread
		for(int i = 0;i < x;i++) {
			for(int j = 0;j < y;j++) {
				matrix[i][j] = function.compute(matrix[i][j],args);
			}
		}
	}
	
	/**
	 * Create a data copy of the matrix
	 * @return A new independant matrix with the same data
	 */
	public Matrix copy() {
		Matrix resp = new Matrix(x,y,0);
		for(int i = 0;i < x;i++) {
			for(int j = 0;j < y;j++) {
				resp.matrix[i][j] = matrix[i][j];
			}
		}
		return resp;
	}
	
	/**
	 * Create a column matrix given the values
	 * @param values The values to put in the matrix
	 * @return A new matrix of size 1,values.length
	 */
	public static Matrix getColumnMatrix(float[] values) {
		Matrix resp = new Matrix(1,values.length,0);
		for(int i = 0;i < values.length;i++)
			resp.matrix[0][i] = values[i];
		return resp;
	}
	
	/**
	 * Create a tab with the content of the column
	 * @return A float[] containing the column of the matrix
	 * @throws UnsupportedOperationException when the matrix isn't a column
	 */
	public float[] getColumn() throws UnsupportedOperationException{
		if(x != 1) throw new UnsupportedOperationException("The Matrix isn't one column");
		float[] resp = new float[y];
		for(int i = 0;i < y;i++) {
			resp[i] = matrix[0][i];
		}
		return resp;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	/**
	 * Perform a mathematical transpose operation
	 * @return A new matrix M^(T)
	 */
	public Matrix transpose() {
		Matrix resp = new Matrix(y,x,0);
		for(int i = 0;i < x;i++) {
			for(int j = 0;j < y;j++) {
				resp.matrix[j][i] = matrix[i][j];
			}
		}
		return resp;
	}
	
	/**
	 * Used to test different function of this class
	 */
	public static void main(String args[]) {
		Matrix a = new Matrix(1,3);
		a.matrix[0][0] = 1;
		a.matrix[0][1] = 2;
		a.matrix[0][2] = 3;
		Matrix b = new Matrix(1,1);
		b.matrix[0][0] = 2;
		System.out.println(a);
		System.out.println(b);
		System.out.println(a.multiply(b));
	}
}
